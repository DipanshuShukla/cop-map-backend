package com.dipanshushukla.cop_map_core_service.dto;

import com.dipanshushukla.cop_map_core_service.entity.Naka;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NakaDTO {
    private UUID id;

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Latitude is required")
    private Double latitude;

    @NotNull(message = "Longitude is required")
    private Double longitude;

    private String laneDirection;

    public Naka toEntity(String thanaId) {
        return Naka.builder()
                .thanaId(thanaId)
                .name(this.name)
                .latitude(this.latitude)
                .longitude(this.longitude)
                .laneDirection(this.laneDirection)
                .build();
    }

    public static NakaDTO fromEntity(Naka naka) {
        return NakaDTO.builder()
                .id(naka.getId())
                .name(naka.getName())
                .latitude(naka.getLatitude())
                .longitude(naka.getLongitude())
                .laneDirection(naka.getLaneDirection())
                .build();
    }
}