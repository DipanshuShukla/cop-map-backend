package com.dipanshushukla.cop_map_core_service.repository;

import com.dipanshushukla.cop_map_core_service.entity.DutyLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface DutyLogRepository extends JpaRepository<DutyLog, UUID> {
    List<DutyLog> findAllByAssignmentId(UUID assignmentId);
}