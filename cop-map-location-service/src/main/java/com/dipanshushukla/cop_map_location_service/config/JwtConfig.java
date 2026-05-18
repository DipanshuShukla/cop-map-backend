package com.dipanshushukla.cop_map_location_service.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import java.util.List;

@Configuration
public class JwtConfig {

    @Autowired
    private DiscoveryClient discoveryClient;

    @Bean
    public JwtDecoder jwtDecoder() {
        List<ServiceInstance> instances = discoveryClient.getInstances("cop-map-auth-service");

        if (instances.isEmpty()) {
            throw new IllegalStateException(
                    "Authentication service instance not found in Eureka registry. Please start Auth Service first.");
        }

        String jwkSetUri = instances.get(0).getUri().toString() + "/api/v1/auth/.well-known/jwks.json";
        return NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
    }
}