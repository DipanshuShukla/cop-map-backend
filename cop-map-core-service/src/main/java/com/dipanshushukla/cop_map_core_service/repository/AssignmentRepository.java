package com.dipanshushukla.cop_map_core_service.repository;

import com.dipanshushukla.cop_map_core_service.entity.Assignment;
import com.dipanshushukla.cop_map_core_service.model.AssignmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface AssignmentRepository extends JpaRepository<Assignment, UUID> {
    List<Assignment> findAllByBadgeNumber(String badgeNumber);

    List<Assignment> findAllByBadgeNumberAndStatus(String badgeNumber, AssignmentStatus status);
}