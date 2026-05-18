package com.dipanshushukla.cop_map_core_service.dto;

import com.dipanshushukla.cop_map_core_service.entity.Beat;
import com.dipanshushukla.cop_map_core_service.model.Coordinate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BeatDTO implements Serializable {
    private UUID id;

    @NotBlank(message = "Name is required")
    private String name;

    @NotEmpty(message = "Route coordinates are required")
    private List<Coordinate> routeCoordinates;

    public Beat toEntity(String thanaId) {
        return Beat.builder()
                .thanaId(thanaId)
                .name(this.name)
                .routeCoordinates(this.routeCoordinates)
                .build();
    }

    public static BeatDTO fromEntity(Beat beat) {
        return BeatDTO.builder()
                .id(beat.getId())
                .name(beat.getName())
                .routeCoordinates(beat.getRouteCoordinates())
                .build();
    }
}