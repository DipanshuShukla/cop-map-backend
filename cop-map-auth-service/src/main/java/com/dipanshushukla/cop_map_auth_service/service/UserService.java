package com.dipanshushukla.cop_map_auth_service.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.dipanshushukla.cop_map_auth_service.dto.UserDTO;
import com.dipanshushukla.cop_map_auth_service.dto.UserUpdateDTO;
import com.dipanshushukla.cop_map_auth_service.entity.User;
import com.dipanshushukla.cop_map_auth_service.exception.BadgeIdAlreadyExistsException;
import com.dipanshushukla.cop_map_auth_service.model.Role;
import com.dipanshushukla.cop_map_auth_service.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UserDTO createUser(UserDTO request, User currentUser) {
        // 1. RBAC & Hierarchy Check
        validateHierarchy(currentUser, request.getRole(), request.getThanaId());

        if (repository.existsByBadgeNumber(request.getBadgeNumber())) {
            throw new BadgeIdAlreadyExistsException(
                    "Officer already exists with Badge ID: " + request.getBadgeNumber());
        }

        User user = request.toEntity(passwordEncoder.encode(request.getPassword()));
        user = repository.save(user);

        return UserDTO.fromEntity(user);
    }

    public List<UserDTO> getUsersByThana(String thanaId, User currentUser) {
        // If an SHO is asking, they can only query their own Thana
        if (currentUser.getRole() == Role.SHO && !currentUser.getThanaId().equals(thanaId)) {
            throw new SecurityException("You do not have jurisdiction over this Thana.");
        }

        return repository.findAllByThanaId(thanaId).stream()
                .map(UserDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Partially update an officer's information.
     */
    public UserDTO updateUserStatusOrInfo(String targetBadgeNumber, UserUpdateDTO updates, User currentUser) {
        User targetUser = repository.findByBadgeNumber(targetBadgeNumber)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // 1. RBAC Check: Ensure the current user has authority over the target user
        validateHierarchy(currentUser, targetUser.getRole(), targetUser.getThanaId());

        // 2. Apply partial updates (Only if the frontend provided them)
        if (updates.getFullName() != null) {
            targetUser.setFullName(updates.getFullName());
        }
        if (updates.getPhoneNumber() != null) {
            targetUser.setPhoneNumber(updates.getPhoneNumber());
        }
        if (updates.getEmail() != null) {
            targetUser.setEmail(updates.getEmail());
        }

        // 3. Admin/Supervisor specific updates (Thana Transfer or Promotion)
        if (currentUser.getRole() == Role.ADMIN || currentUser.getRole() == Role.SUPERVISOR) {
            if (updates.getThanaId() != null) {
                targetUser.setThanaId(updates.getThanaId());
            }
            if (updates.getRole() != null) {
                targetUser.setRole(updates.getRole());
            }
        } else if (updates.getThanaId() != null || updates.getRole() != null) {
            // Throw an error if an SHO tries to transfer or promote someone
            throw new SecurityException("You do not have permission to change an officer's Thana or Rank.");
        }

        repository.save(targetUser);
        return UserDTO.fromEntity(targetUser);

    }

    /**
     * Fetch a specific user by their Badge Number.
     */
    public UserDTO getUserByBadgeNumber(String badgeNumber, User currentUser) {
        User targetUser = repository.findByBadgeNumber(badgeNumber)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Allow users to view their own profile
        if (currentUser.getBadgeNumber().equals(badgeNumber)) {
            return UserDTO.fromEntity(targetUser);
        }

        // Otherwise, enforce the hierarchy rules (e.g., SHO can only view their own
        // Constables)
        validateHierarchy(currentUser, targetUser.getRole(), targetUser.getThanaId());

        return UserDTO.fromEntity(targetUser);
    }

    /**
     * CORE DOMAIN LOGIC: Determines who can manage whom.
     */
    private void validateHierarchy(User currentUser, Role targetRole, String targetThanaId) {
        Role currentRole = currentUser.getRole();

        if (currentRole == Role.ADMIN)
            return; // Admin can do anything

        if (currentRole == Role.SUPERVISOR) {
            if (targetRole == Role.ADMIN || targetRole == Role.SUPERVISOR) {
                throw new SecurityException("Supervisors cannot manage equals or superiors.");
            }
            return; // Supervisors can manage any SHO or Constable across any Thana
        }

        if (currentRole == Role.SHO) {
            if (targetRole != Role.CONSTABLE) {
                throw new SecurityException("SHOs can only manage Constables.");
            }
            if (!currentUser.getThanaId().equals(targetThanaId)) {
                throw new SecurityException("SHOs can only manage officers within their own Thana.");
            }
            return;
        }

        throw new SecurityException("Constables do not have management privileges.");
    }
}