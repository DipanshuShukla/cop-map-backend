package com.dipanshushukla.cop_map_auth_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.dipanshushukla.cop_map_auth_service.dto.UserDTO;
import com.dipanshushukla.cop_map_auth_service.dto.UserUpdateDTO;
import com.dipanshushukla.cop_map_auth_service.entity.User;
import com.dipanshushukla.cop_map_auth_service.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Create a new officer. Only accessible by Admins, Supervisors, and SHOs.
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'SHO')")
    public ResponseEntity<UserDTO> createUser(
            @Valid @RequestBody UserDTO userDTO,
            @AuthenticationPrincipal User currentUser) {

        UserDTO createdUser = userService.createUser(userDTO, currentUser);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    /**
     * Get a specific officer's details by Badge Number.
     */
    @GetMapping("/{badgeNumber}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'SHO', 'CONSTABLE')")
    public ResponseEntity<UserDTO> getUserByBadgeNumber(
            @PathVariable String badgeNumber,
            @AuthenticationPrincipal User currentUser) {

        return ResponseEntity.ok(userService.getUserByBadgeNumber(badgeNumber, currentUser));
    }

    /**
     * Get all officers for a specific Thana.
     */
    @GetMapping("/thana/{thanaId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'SHO')")
    public ResponseEntity<List<UserDTO>> getUsersByThana(
            @PathVariable String thanaId,
            @AuthenticationPrincipal User currentUser) {

        return ResponseEntity.ok(userService.getUsersByThana(thanaId, currentUser));
    }

    /**
     * Partially update an officer's information.
     */
    @PatchMapping("/{badgeNumber}") // Changed to PATCH
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'SHO')")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable String badgeNumber,
            @Valid @RequestBody UserUpdateDTO userUpdateDTO, // Using the new Update DTO
            @AuthenticationPrincipal User currentUser) {

        return ResponseEntity.ok(userService.updateUserStatusOrInfo(badgeNumber, userUpdateDTO, currentUser));
    }
}