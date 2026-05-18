package com.dipanshushukla.cop_map_core_service.dto;

import com.dipanshushukla.cop_map_core_service.entity.Operation;
import com.dipanshushukla.cop_map_core_service.model.Coordinate;
import com.dipanshushukla.cop_map_core_service.model.OperationStatus;
import com.dipanshushukla.cop_map_core_service.model.OperationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CreateOperationDTO {
    @NotBlank(message = "Title is required")
    private String title;

    @NotNull(message = "Operation type is required")
    private OperationType type;

    @NotNull(message = "Start time is required")
    private LocalDateTime startTime;

    private LocalDateTime endTime;
    private String referenceId;
    private List<Coordinate> customGeofence;

    /** Convert DTO → Entity */
    public Operation toEntity(String thanaId) {
        return Operation.builder()
                .thanaId(thanaId)
                .title(this.title)
                .type(this.type)
                .status(OperationStatus.DRAFT) // Always enforce Draft on creation
                .startTime(this.startTime)
                .endTime(this.endTime)
                .referenceId(this.referenceId)
                .customGeofence(this.customGeofence)
                .build();
    }
}