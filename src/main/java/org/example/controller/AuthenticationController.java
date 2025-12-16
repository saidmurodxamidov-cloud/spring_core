package org.example.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.request.LoginRequest;
import org.example.dto.response.TokenResponse;
import org.example.security.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        log.debug("Login attempt for user: {}", request.getUsername());

        String token = authService.login(request.getUsername(), request.getPassword());

        log.info("User {} logged in successfully", request.getUsername());

        return ResponseEntity.ok(new TokenResponse(token));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        authService.logout();
        log.debug("User logged out successfully");
        return ResponseEntity.ok().build();
    }
}