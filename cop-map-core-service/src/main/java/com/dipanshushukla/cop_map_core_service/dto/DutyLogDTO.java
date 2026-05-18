package com.dipanshushukla.cop_map_core_service.dto;

import com.dipanshushukla.cop_map_core_service.entity.Assignment;
import com.dipanshushukla.cop_map_core_service.entity.DutyLog;
import com.dipanshushukla.cop_map_core_service.model.LogStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DutyLogDTO {
    private UUID id;

    @NotNull(message = "Status is required")
    private LogStatus status;

    private String notes;
    private LocalDateTime timestamp;

    public DutyLog toEntity(Assignment assignment, String badgeNumber) {
        return DutyLog.builder()
                .assignment(assignment)
                .badgeNumber(badgeNumber)
                .status(this.status)
                .notes(this.notes)
                .build();
    }

    public static DutyLogDTO fromEntity(DutyLog log) {
        return DutyLogDTO.builder()
                .id(log.getId())
                .status(log.getStatus())
                .notes(log.getNotes())
                .timestamp(log.getTimestamp())
                .build();
    }
}