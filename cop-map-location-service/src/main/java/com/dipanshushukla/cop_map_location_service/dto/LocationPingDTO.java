package com.dipanshushukla.cop_map_location_service.dto;

import lombok.Data;
import java.time.Instant;

@Data
public class LocationPingDTO {
    private String badgeNumber;
    private String thanaId;
    private Double latitude;
    private Double longitude;
    private Instant timestamp;
}