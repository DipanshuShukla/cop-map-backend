package com.dipanshushukla.cop_map_auth_service.model;

public enum Role {
    CONSTABLE, // End-user executing the task
    SHO, // Planner / Station In-charge
    SUPERVISOR, // High-level monitoring and audit
    ADMIN // System administration (IT/Tech)
}
