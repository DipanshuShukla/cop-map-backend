package com.dipanshushukla.cop_map_core_service.repository;

import com.dipanshushukla.cop_map_core_service.entity.Naka;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface NakaRepository extends JpaRepository<Naka, UUID> {
    List<Naka> findAllByThanaId(String thanaId);
}