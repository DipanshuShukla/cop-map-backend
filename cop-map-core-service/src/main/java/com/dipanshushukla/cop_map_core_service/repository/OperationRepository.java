package com.dipanshushukla.cop_map_core_service.repository;

import com.dipanshushukla.cop_map_core_service.entity.Operation;
import com.dipanshushukla.cop_map_core_service.model.OperationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface OperationRepository extends JpaRepository<Operation, UUID> {
    // Get all operations for a specific station
    List<Operation> findAllByThanaId(String thanaId);

    // Get all active operations for a specific station
    List<Operation> findAllByThanaIdAndStatus(String thanaId, OperationStatus status);
}