package com.dipanshushukla.cop_map_auth_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dipanshushukla.cop_map_auth_service.dto.JwtResponseDTO;
import com.dipanshushukla.cop_map_auth_service.dto.RefreshTokenRequestDTO;
import com.dipanshushukla.cop_map_auth_service.dto.UserLoginCredentialsDTO;
import com.dipanshushukla.cop_map_auth_service.service.AuthenticationService;
import com.dipanshushukla.cop_map_auth_service.service.JwtService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authService;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<JwtResponseDTO> login(@Valid @RequestBody UserLoginCredentialsDTO request) {
        return ResponseEntity.ok(authService.authenticate(request));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<JwtResponseDTO> refreshToken(@RequestBody RefreshTokenRequestDTO dto) {
        return ResponseEntity.ok(authService.refreshToken(dto.getRefreshToken()));
    }

    @GetMapping("/.well-known/jwks.json")
    public ResponseEntity<Object> getJwks() {
        return ResponseEntity.ok(jwtService.getJwks());

    }

}
