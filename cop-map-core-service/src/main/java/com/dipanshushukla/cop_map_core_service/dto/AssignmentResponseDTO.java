package com.dipanshushukla.cop_map_core_service.dto;

import com.dipanshushukla.cop_map_core_service.entity.Assignment;
import com.dipanshushukla.cop_map_core_service.model.AssignmentStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class AssignmentResponseDTO {
    private UUID id;
    private UUID operationId;
    private String badgeNumber;
    private String teamName;
    private AssignmentStatus status;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;

    /** Convert Entity → DTO */
    public static AssignmentResponseDTO fromEntity(Assignment assignment) {
        return AssignmentResponseDTO.builder()
                .id(assignment.getId())
                .operationId(assignment.getOperation().getId()) // Just send the ID, not the whole object
                .badgeNumber(assignment.getBadgeNumber())
                .teamName(assignment.getTeamName())
                .status(assignment.getStatus())
                .checkInTime(assignment.getCheckInTime())
                .checkOutTime(assignment.getCheckOutTime())
                .build();
    }
}