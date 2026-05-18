package com.dipanshushukla.cop_map_location_service.controller;

import com.dipanshushukla.cop_map_location_service.dto.LocationPingDTO;
import com.dipanshushukla.cop_map_location_service.service.LocationRedisService;
import com.dipanshushukla.cop_map_location_service.service.RedisMessagePublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.Instant;

@Controller
@RequiredArgsConstructor
public class LocationController {

    private final LocationRedisService locationService;
    private final RedisMessagePublisher redisPublisher; // Inject the Publisher

    @MessageMapping("/ping")
    public void handleLocationPing(LocationPingDTO ping, Principal principal) {

        // Security check
        if (principal == null || !ping.getBadgeNumber().equals(principal.getName())) {
            throw new SecurityException("Unauthorized location broadcast attempt.");
        }

        if (ping.getTimestamp() == null) {
            ping.setTimestamp(Instant.now());
        }

        locationService.updateOfficerLocation(ping);

        redisPublisher.publishLocationUpdate(ping);
    }
}