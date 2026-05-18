package com.dipanshushukla.cop_map_core_service.dto;

import com.dipanshushukla.cop_map_core_service.entity.Assignment;
import com.dipanshushukla.cop_map_core_service.entity.Operation;
import com.dipanshushukla.cop_map_core_service.model.AssignmentStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AssignOfficerDTO {
    @NotBlank(message = "Badge number is required")
    private String badgeNumber;

    private String teamName;

    /** Convert DTO → Entity */
    public Assignment toEntity(Operation operation) {
        return Assignment.builder()
                .operation(operation)
                .badgeNumber(this.badgeNumber)
                .teamName(this.teamName)
                .status(AssignmentStatus.PENDING) // Always starts as pending
                .build();
    }
}