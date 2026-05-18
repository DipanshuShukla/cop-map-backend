package com.dipanshushukla.cop_map_location_service.service;

import com.dipanshushukla.cop_map_location_service.dto.LocationPingDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisMessageSubscriber {

    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    /**
     * This method is triggered automatically by the MessageListenerAdapter
     */
    public void onMessage(String message, String channel) {
        try {
            // 1. Deserialize the message from Redis
            LocationPingDTO ping = objectMapper.readValue(message, LocationPingDTO.class);

            // 2. Broadcast to the local WebSocket clients connected to this instance
            String destinationTopic = "/topic/thana/" + ping.getThanaId();
            messagingTemplate.convertAndSend(destinationTopic, ping);

        } catch (Exception e) {
            System.err.println("Error processing Redis pub/sub message: " + e.getMessage());
        }
    }
}