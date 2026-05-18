package com.dipanshushukla.cop_map_core_service.service;

import com.dipanshushukla.cop_map_core_service.dto.AssignOfficerDTO;
import com.dipanshushukla.cop_map_core_service.dto.AssignmentResponseDTO;
import com.dipanshushukla.cop_map_core_service.dto.CreateOperationDTO;
import com.dipanshushukla.cop_map_core_service.dto.OperationResponseDTO;
import com.dipanshushukla.cop_map_core_service.entity.Assignment;
import com.dipanshushukla.cop_map_core_service.entity.Operation;
import com.dipanshushukla.cop_map_core_service.model.OperationStatus;
import com.dipanshushukla.cop_map_core_service.repository.AssignmentRepository;
import com.dipanshushukla.cop_map_core_service.repository.OperationRepository;
import com.dipanshushukla.cop_map_core_service.security.CopMapPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OperationService {

    private final OperationRepository operationRepository;
    private final AssignmentRepository assignmentRepository;

    @Transactional
    public OperationResponseDTO createOperation(CreateOperationDTO dto, CopMapPrincipal principal) {

        Operation operation = dto.toEntity(principal.thanaId());
        operation = operationRepository.save(operation);

        return OperationResponseDTO.fromEntity(operation);
    }

    @Transactional
    public OperationResponseDTO publishOperation(UUID operationId, CopMapPrincipal principal) {
        Operation operation = getOperationAndVerifyJurisdiction(operationId, principal);

        operation.setStatus(OperationStatus.PUBLISHED);
        operation = operationRepository.save(operation);

        return OperationResponseDTO.fromEntity(operation);
    }

    @Transactional
    public AssignmentResponseDTO assignOfficer(UUID operationId, AssignOfficerDTO dto, CopMapPrincipal principal) {
        Operation operation = getOperationAndVerifyJurisdiction(operationId, principal);

        Assignment assignment = dto.toEntity(operation);
        assignment = assignmentRepository.save(assignment);

        return AssignmentResponseDTO.fromEntity(assignment);
    }

    public List<OperationResponseDTO> getActiveOperations(CopMapPrincipal principal) {
        return operationRepository.findAllByThanaIdAndStatus(principal.thanaId(), OperationStatus.PUBLISHED)
                .stream()
                .map(OperationResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Helper method to ensure an SHO doesn't modify another Thana's operations.
     */
    private Operation getOperationAndVerifyJurisdiction(UUID operationId, CopMapPrincipal principal) {
        Operation operation = operationRepository.findById(operationId)
                .orElseThrow(() -> new IllegalArgumentException("Operation not found"));

        if (!operation.getThanaId().equals(principal.thanaId()) && !principal.role().equals("ADMIN")) {
            throw new SecurityException("You do not have jurisdiction over this operation.");
        }
        return operation;
    }
}