package com.dipanshushukla.cop_map_core_service.controller;

import com.dipanshushukla.cop_map_core_service.dto.AssignmentResponseDTO;
import com.dipanshushukla.cop_map_core_service.dto.DutyLogDTO;
import com.dipanshushukla.cop_map_core_service.security.CopMapPrincipal;
import com.dipanshushukla.cop_map_core_service.service.AssignmentActionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/core/assignments")
@RequiredArgsConstructor
public class AssignmentActionController {

    private final AssignmentActionService assignmentService;

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('CONSTABLE', 'SHO')")
    public ResponseEntity<List<AssignmentResponseDTO>> getMyAssignments(
            @AuthenticationPrincipal CopMapPrincipal principal) {
        return ResponseEntity.ok(assignmentService.getMyAssignments(principal));
    }

    @PostMapping("/{id}/check-in")
    @PreAuthorize("hasRole('CONSTABLE')")
    public ResponseEntity<AssignmentResponseDTO> checkIn(
            @PathVariable UUID id,
            @AuthenticationPrincipal CopMapPrincipal principal) {
        return ResponseEntity.ok(assignmentService.checkIn(id, principal));
    }

    @PostMapping("/{id}/check-out")
    @PreAuthorize("hasRole('CONSTABLE')")
    public ResponseEntity<AssignmentResponseDTO> checkOut(
            @PathVariable UUID id,
            @AuthenticationPrincipal CopMapPrincipal principal) {
        return ResponseEntity.ok(assignmentService.checkOut(id, principal));
    }

    @PostMapping("/{id}/logs")
    @PreAuthorize("hasRole('CONSTABLE')")
    public ResponseEntity<DutyLogDTO> submitDutyLog(
            @PathVariable UUID id,
            @Valid @RequestBody DutyLogDTO dto,
            @AuthenticationPrincipal CopMapPrincipal principal) {
        return new ResponseEntity<>(assignmentService.submitDutyLog(id, dto, principal), HttpStatus.CREATED);
    }
}