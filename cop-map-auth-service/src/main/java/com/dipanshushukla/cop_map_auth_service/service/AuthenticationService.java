package com.dipanshushukla.cop_map_auth_service.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.dipanshushukla.cop_map_auth_service.dto.ChangePasswordDTO;
import com.dipanshushukla.cop_map_auth_service.dto.JwtResponseDTO;
import com.dipanshushukla.cop_map_auth_service.dto.UserLoginCredentialsDTO;
import com.dipanshushukla.cop_map_auth_service.entity.User;
import com.dipanshushukla.cop_map_auth_service.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImp userDetailsService;

    /**
     * Authenticates the user and returns tokens.
     */
    public JwtResponseDTO authenticate(UserLoginCredentialsDTO request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getBadgeNumber(),
                        request.getPassword()));

        User user = repository.findByBadgeNumber(request.getBadgeNumber())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return new JwtResponseDTO(accessToken, refreshToken);
    }

    /**
     * Refreshes the access token.
     */
    public JwtResponseDTO refreshToken(String refreshToken) {
        String badgeNumber = jwtService.extractUsername(refreshToken);

        if (badgeNumber == null) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        User user = repository.findByBadgeNumber(badgeNumber)
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));

        if (!jwtService.isValid(refreshToken, userDetailsService.loadUserByUsername(badgeNumber))) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        String newAccessToken = jwtService.generateAccessToken(user);

        return new JwtResponseDTO(newAccessToken, refreshToken);
    }

    /**
     * Allows an authenticated user to change their password.
     */
    public void changePassword(String badgeNumber, ChangePasswordDTO request) {
        User user = repository.findByBadgeNumber(badgeNumber)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid old password");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        repository.save(user);
    }
}