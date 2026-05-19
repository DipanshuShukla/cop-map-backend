# CopMap Backend Engine (Microservices Architecture)

CopMap is an enterprise-grade, highly scalable backend ecosystem designed to manage real-time police operations—specifically:

* **Patrolling**
* **Nakabandi (Blockades)**
* **Bandobast (Mass Security Deployments)**.

This system is designed for real-world policing constraints: strict hierarchical role-based access control (RBAC), station-level jurisdiction isolation, decoupled security , and horizontal scalability for features like high-frequency real-time location data ingestion.

---

## 1. Problem Understanding & Domain Research

Through intentional domain analysis, the core requirements of law enforcement resource deployment were translated into concrete software patterns:

* **The Hierarchy:** Police operations strictly split into **Planning** and **Execution**. Station House Officers (SHOs), Supervisors, and Admins plan and delegate; Constables execute on the ground.
* **Jurisdiction Enforcement:** An SHO belongs to a specific *Thana* (Station). They must only see, create, or modify assets within their station's physical boundary. They cannot view or allocate personnel belonging to other jurisdictions.

**Operational Typology:**

* *Patrolling:* Predefined paths or routes (**Beats**) designed for ongoing surveillance.
* *Nakabandi:* Targeted, checkpoint-based deployments (**Nakas**) focusing on lane-specific blocking (e.g., Northbound lane checking).
* *Bandobast:* Large-scale, ad-hoc security setups handling mass events. They rely on custom polygon boundaries (**Geofences**) and platoon/team-level deployments rather than individual personnel assignments.

---

## 2. System Architecture

The ecosystem relies on a decentralized, zero-trust microservice topology orchestrated seamlessly via Docker.

### Microservice Boundary Definitions

1. **Nginx Ingress Edge Router:** Acts as the single entrance into the infrastructure.
2. **Spring Cloud Gateway:** Handles gateway operations along with dynamic load balancing with the help of Eureka discovery.
3. **Central Registry Service (Eureka):** Discovery server allowing services to find each other dynamically without hardcoded runtime locations.
4. **Central Config Service:** Externalizes application properties, supplying configurations at boot-time dynamically.
5. **Auth Service:** Identity management, user registrations, issues RSA-based JWTs, and serves public verification keys via a `.well-known/jwks.json` endpoint.
6. **Core Operational Service:** The core service for managing Nakas, Beats, Operations, Assignments, and Duty Log generation.
7. **Location Tracking Service:** A decoupled locaiton ingestion engine running on persistent STOMP over WebSockets. Designed to be stateless using Redis Pub/Sub.

---

## 3. Core Technical Decisions & Engineering Flexes

### Zero-Trust Internal Container Networking

To emulate production cloud perimeters, **no backing database, microservice, or registry exposes ports to the host machine**. All internal communication happens isolated inside a private docker bridge network (`copmap-network`). External requests hit Nginx, which proxies traffic down to the Gateway. Nginx is explicitly configured with custom handshake headers to allow long-lived HTTP WebSocket protocol upgrades:

```nginx
proxy_http_version 1.1;
proxy_set_header Upgrade $http_upgrade;
proxy_set_header Connection "upgrade";

```

### Decoupled Stateless Security (Gateway + JWKS)

Downstream services do not share user databases or know about security credentials.

1. The Gateway interceptor decodes incoming bearer JWTs using public cryptographic keys exposed by the Auth service (`/well-known/jwks.json`) which it resolves dynamically via Eureka.
2. Once verified, the Gateway converts claims into secure HTTP headers (`X-Badge-Number`, `X-Role`, `X-Thana-Id`) and forwards them down the chain.
3. Downstream microservices intercept these headers via a clean `OncePerRequestFilter`, mapping them directly to a standard `AuthenticationPrincipal` record context.

### Context-Driven Anti-Spoofing & Jurisdiction Security

When an SHO creates a Naka or publishes an operation, they **do not supply a Thana ID in the JSON body**. The Core Service strictly extracts the `X-Thana-Id` from the authenticated principal header. This prevents malicious API users from modifying coordinates or scheduling actions outside their designated boundaries.

### High-Throughput Real-Time Ingest (Redis Pub/Sub WebSocket Backplane)

If multiple instances of a WebSocket service are load-balanced behind a gateway, they suffer from the "split-brain" problem (an update on Instance 1 is hidden from an SHO connected to Instance 2).
To make location tracking highly scalable, this project implements a customized **Redis Pub/Sub backplane**:

1. A tracking client broadcasts coordinates via a secure STOMP session to the `/app/ping` mapping.
2. The receiving node validates the token using a custom `ChannelInterceptor` checking the STOMP `CONNECT` header frame.
3. The node persists coordinates in Redis using high-speed native geospatial points (`GEOADD`) and publishes the payload to a central Redis topic (`location-updates`).
4. Every running instance listens to this Redis channel via a background `RedisMessageListenerContainer` and immediately streams the message down to its locally active WebSocket channels (`/topic/thana/{id}`).

---

## 4. Architectural Trade-offs & Future Scope

### Write Bottleneck Protection (Location History Batching)

* *The Trade-off:* Writing GPS coordinates directly to a relational database like PostgreSQL every 10 seconds across hundreds of active mobile clients creates immediate write-lock bottlenecks and severe database bloat.
* *The Strategy:* In this phase, real-time location data is handled entirely in-memory using Redis Geospatial sets, alongside a secondary `last_seen` timestamp string stringently recording radio silence to trace MIA officers. In Phase 2, coordinates will be accumulated sequentially inside temporary Redis Lists during active shifts. When a constable triggers the final `/check-out` endpoint, a worker will pull the list, serialize the historical travel path into a single JSON trajectory array, and commit *one single row* to the relational database, saving thousands of operations.

### Distributed Asynchronous Auditing

* *The Trade-off:* Currently, operational history and audit trails are maintained synchronously via relational cascading records (Duty Logs). While this ensures immediate consistency, processing heavy forensic logs directly within the main API execution path can degrade the performance of high-frequency operations.
* *The Strategy:* In Phase 2, the architecture will introduce a decoupled, asynchronous auditing pipeline using a message broker (e.g., Apache Kafka). Core domain events (state shifts, operation closures, assignment modifications) will emit immutable events to a dedicated audit topic. A separate Auditing Microservice will consume these events and persist them into a tamper-proof datastore. This ensures government-grade compliance and legal non-repudiation without compromising the sub-millisecond response times of the primary operational engines.

---

## 5. Database Schema Design

```text
   ┌────────────────────────────────┐
   │             nakas              │
   ├────────────────────────────────┤
   │ id: UUID [PK]                  │
   │ thanaId: VARCHAR               │
   │ name: VARCHAR                  │
   │ latitude: DOUBLE               │
   │ longitude: DOUBLE              │
   │ laneDirection: VARCHAR         │
   └────────────────────────────────┘

   ┌────────────────────────────────┐
   │             beats              │
   ├────────────────────────────────┤
   │ id: UUID [PK]                  │
   │ thanaId: VARCHAR               │
   │ name: VARCHAR                  │
   │ routeCoordinates: JSONB ───────┼─► [Array of Lat/Lng objects]
   └────────────────────────────────┘

   ┌────────────────────────────────┐
   │           operations           │
   ├────────────────────────────────┤
   │ id: UUID [PK]                  │
   │ thanaId: VARCHAR               │
   │ title: VARCHAR                 │
   │ type: VARCHAR (Enum)           │
   │ status: VARCHAR (Enum)         │
   │ startTime: TIMESTAMP           │
   │ endTime: TIMESTAMP             │
   │ referenceId: VARCHAR           │
   │ customGeofence: JSONB          │
   └───────────────┬────────────────┘
                   │
                   │ 1
                   ▼ 0..*
   ┌────────────────────────────────┐
   │          assignments           │
   ├────────────────────────────────┤
   │ id: UUID [PK]                  │
   │ operation_id: UUID [FK] ───────┼─► Links to operations
   │ badgeNumber: VARCHAR           │
   │ teamName: VARCHAR              │
   │ status: VARCHAR (Enum)         │
   │ checkInTime: TIMESTAMP         │
   │ checkOutTime: TIMESTAMP        │
   └───────────────┬────────────────┘
                   │
                   │ 1
                   ▼ 0..*
   ┌────────────────────────────────┐
   │           duty_logs            │
   ├────────────────────────────────┤
   │ id: UUID [PK]                  │
   │ assignment_id: UUID [FK] ──────┼─► Links to assignments
   │ badgeNumber: VARCHAR           │
   │ status: VARCHAR (Enum)         │
   │ notes: TEXT                    │
   │ timestamp: TIMESTAMP           │
   └────────────────────────────────┘

```

---

## 6. Local Quick-Start & Docker Deployment

### Prerequisites

* Docker & Docker Compose installed and running.
* Allocated memory in Docker Desktop set to at least `4GB` to comfortably house the JVM processes.
* Your Central Config repository updated with backing properties matching the container hostnames (`jdbc:postgresql://copmap-postgres:5432/copmap_db` and `host: copmap-redis`).

### One-Click Execution

Navigate to the root directory containing your `docker-compose.yml` file and execute:

```bash
docker compose up -d --build

```

### Verification & Infrastructure Health Check

You can tail individual service logs to confirm that services are booting and successfully connecting to Eureka and the Central Config Server:

```bash
# Check if Nginx and the Gateway router are up
docker compose logs -f copmap-nginx
docker compose logs -f copmap-gateway-service

# Check on backing databases
docker compose logs -f copmap-postgres

```

To shut down the entire localized cluster cleanly without dropping database volumes:

```bash
docker compose down

```

---

## 7. API Testing & Postman Instructions

The Postman testing configuration is structured exactly 1:1 with your service controllers for quick navigation. The collection JSON file is stored inside the `/postman` directory of this repository.

* **Target Ingress Base URL:** `http://localhost:8080` (All communication routes smoothly through Nginx).

### Core Testing Workflows (Postman Mappings)

1. **Auth Operations (`AuthController`):** Use `POST /api/v1/auth/login` mapping individual badge IDs to receive your bearer tokens.
2. **Master Resource Creation (`MasterDataController`):** Use an SHO bearer token to post to `/api/v1/core/master/nakas` and `/api/v1/core/master/beats` to initialize the layout configurations for your station.
3. **Operations Engine (`OperationController`):** Create drafts, assign field units by badge markers, and shift states by hitting `/api/v1/core/operations/{id}/publish`.
4. **Field Mobilization (`AssignmentActionController`):** Switch your auth tokens to mimic a Constable client. Hit `/api/v1/core/assignments/me` to read your orders, follow up with structural posts to check-in, issue incident reports to the log, and check-out.
5. **Live Ingestion WebSocket Test:** Create a WebSocket connection in Postman pointing to `ws://localhost:8080/ws/stream`. Issue text-based STOMP `CONNECT` frames carrying your bearer strings to verify real-time packet processing across the Redis data backplane.
