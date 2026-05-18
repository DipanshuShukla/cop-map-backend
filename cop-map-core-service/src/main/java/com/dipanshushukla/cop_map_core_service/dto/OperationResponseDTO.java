package com.dipanshushukla.cop_map_core_service.dto;

import com.dipanshushukla.cop_map_core_service.entity.Operation;
import com.dipanshushukla.cop_map_core_service.model.Coordinate;
import com.dipanshushukla.cop_map_core_service.model.OperationStatus;
import com.dipanshushukla.cop_map_core_service.model.OperationType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@Builder
public class OperationResponseDTO {
    private UUID id;
    private String thanaId;
    private String title;
    private OperationType type;
    private OperationStatus status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String referenceId;
    private List<Coordinate> customGeofence;
    private List<AssignmentResponseDTO> assignments;

    /** Convert Entity → DTO */
    public static OperationResponseDTO fromEntity(Operation op) {
        return OperationResponseDTO.builder()
                .id(op.getId())
                .thanaId(op.getThanaId())
                .title(op.getTitle())
                .type(op.getType())
                .status(op.getStatus())
                .startTime(op.getStartTime())
                .endTime(op.getEndTime())
                .referenceId(op.getReferenceId())
                .customGeofence(op.getCustomGeofence())
                // Safely convert assignments to DTOs to prevent infinite JSON recursion
                .assignments(op.getAssignments() != null ? op.getAssignments().stream()
                        .map(AssignmentResponseDTO::fromEntity)
                        .collect(Collectors.toList()) : new ArrayList<>())
                .build();
    }
}