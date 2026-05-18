package com.dipanshushukla.cop_map_core_service.service;

import com.dipanshushukla.cop_map_core_service.dto.AssignmentResponseDTO;
import com.dipanshushukla.cop_map_core_service.dto.DutyLogDTO;
import com.dipanshushukla.cop_map_core_service.entity.Assignment;
import com.dipanshushukla.cop_map_core_service.entity.DutyLog;
import com.dipanshushukla.cop_map_core_service.model.AssignmentStatus;
import com.dipanshushukla.cop_map_core_service.repository.AssignmentRepository;
import com.dipanshushukla.cop_map_core_service.repository.DutyLogRepository;
import com.dipanshushukla.cop_map_core_service.security.CopMapPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssignmentActionService {

    private final AssignmentRepository assignmentRepository;
    private final DutyLogRepository dutyLogRepository;

    public List<AssignmentResponseDTO> getMyAssignments(CopMapPrincipal principal) {
        return assignmentRepository.findAllByBadgeNumber(principal.badgeNumber()).stream()
                .map(AssignmentResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public AssignmentResponseDTO checkIn(UUID assignmentId, CopMapPrincipal principal) {
        Assignment assignment = getVerifiedAssignment(assignmentId, principal);

        if (assignment.getStatus() != AssignmentStatus.PENDING) {
            throw new IllegalStateException("Can only check-in to PENDING assignments.");
        }

        assignment.setStatus(AssignmentStatus.CHECKED_IN);
        assignment.setCheckInTime(LocalDateTime.now());

        return AssignmentResponseDTO.fromEntity(assignmentRepository.save(assignment));
    }

    @Transactional
    public AssignmentResponseDTO checkOut(UUID assignmentId, CopMapPrincipal principal) {
        Assignment assignment = getVerifiedAssignment(assignmentId, principal);

        if (assignment.getStatus() != AssignmentStatus.CHECKED_IN) {
            throw new IllegalStateException("Must be CHECKED_IN before checking out.");
        }

        assignment.setStatus(AssignmentStatus.COMPLETED);
        assignment.setCheckOutTime(LocalDateTime.now());

        return AssignmentResponseDTO.fromEntity(assignmentRepository.save(assignment));
    }

    @Transactional
    public DutyLogDTO submitDutyLog(UUID assignmentId, DutyLogDTO dto, CopMapPrincipal principal) {
        Assignment assignment = getVerifiedAssignment(assignmentId, principal);

        if (assignment.getStatus() != AssignmentStatus.CHECKED_IN) {
            throw new IllegalStateException("You must be checked into an assignment to submit a log.");
        }

        DutyLog dutyLog = dto.toEntity(assignment, principal.badgeNumber());
        return DutyLogDTO.fromEntity(dutyLogRepository.save(dutyLog));
    }

    /**
     * Ensures an officer can only modify their OWN assignments.
     */
    private Assignment getVerifiedAssignment(UUID assignmentId, CopMapPrincipal principal) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("Assignment not found"));

        if (!assignment.getBadgeNumber().equals(principal.badgeNumber())) {
            throw new SecurityException("You are not authorized to modify this assignment.");
        }
        return assignment;
    }
}