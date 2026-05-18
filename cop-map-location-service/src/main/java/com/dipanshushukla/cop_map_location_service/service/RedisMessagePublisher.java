package com.dipanshushukla.cop_map_location_service.service;

import com.dipanshushukla.cop_map_location_service.config.RedisPubSubConfig;
import com.dipanshushukla.cop_map_location_service.dto.LocationPingDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisMessagePublisher {

    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    public void publishLocationUpdate(LocationPingDTO ping) {
        try {
            String jsonPayload = objectMapper.writeValueAsString(ping);
            stringRedisTemplate.convertAndSend(RedisPubSubConfig.LOCATION_TOPIC, jsonPayload);
        } catch (Exception e) {
            throw new RuntimeException("Failed to publish location update to Redis", e);
        }
    }
}