package com.dipanshushukla.cop_map_core_service.repository;

import com.dipanshushukla.cop_map_core_service.entity.Beat;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface BeatRepository extends JpaRepository<Beat, UUID> {
    List<Beat> findAllByThanaId(String thanaId);
}