package com.dipanshushukla.cop_map_auth_service.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dipanshushukla.cop_map_auth_service.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByBadgeNumber(String badgeNumber);

    boolean existsByBadgeNumber(String badgeNumber);

    List<User> findAllByThanaId(String thanaId);
}
