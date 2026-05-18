package com.dipanshushukla.cop_map_core_service.controller;

import com.dipanshushukla.cop_map_core_service.dto.AssignOfficerDTO;
import com.dipanshushukla.cop_map_core_service.dto.AssignmentResponseDTO;
import com.dipanshushukla.cop_map_core_service.dto.CreateOperationDTO;
import com.dipanshushukla.cop_map_core_service.dto.OperationResponseDTO;
import com.dipanshushukla.cop_map_core_service.security.CopMapPrincipal;
import com.dipanshushukla.cop_map_core_service.service.OperationService;
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
@RequestMapping("/api/v1/core/operations")
@RequiredArgsConstructor
public class OperationController {

    private final OperationService operationService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'SHO')")
    public ResponseEntity<OperationResponseDTO> createOperation(
            @Valid @RequestBody CreateOperationDTO dto,
            @AuthenticationPrincipal CopMapPrincipal principal) {
        return new ResponseEntity<>(operationService.createOperation(dto, principal), HttpStatus.CREATED);
    }

    @PutMapping("/{id}/publish")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'SHO')")
    public ResponseEntity<OperationResponseDTO> publishOperation(
            @PathVariable UUID id,
            @AuthenticationPrincipal CopMapPrincipal principal) {
        return ResponseEntity.ok(operationService.publishOperation(id, principal));
    }

    @PostMapping("/{id}/assignments")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'SHO')")
    public ResponseEntity<AssignmentResponseDTO> assignOfficer(
            @PathVariable UUID id,
            @Valid @RequestBody AssignOfficerDTO dto,
            @AuthenticationPrincipal CopMapPrincipal principal) {
        return new ResponseEntity<>(operationService.assignOfficer(id, dto, principal), HttpStatus.CREATED);
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'SHO', 'CONSTABLE')")
    public ResponseEntity<List<OperationResponseDTO>> getActiveOperations(
            @AuthenticationPrincipal CopMapPrincipal principal) {
        return ResponseEntity.ok(operationService.getActiveOperations(principal));
    }
}