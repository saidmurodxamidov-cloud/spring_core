package org.example.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.dto.request.ChangePasswordRequest;
import org.example.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @RequestBody @Valid ChangePasswordRequest request) {
        try {
            boolean success = userService.changePassword(
                    request.getUsername(),
                    request.getOldPassword(),
                    request.getNewPassword()
            );
            if (success) {
                return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "User not found"));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    @PutMapping("/toggle-active/{username}")
    public ResponseEntity<String> toggleUserActiveStatus(@PathVariable String username) {
        try {
            boolean isActive = userService.toggleUserActiveStatus(username);
            String status = isActive ? "active" : "inactive";
            return ResponseEntity.ok("User " + username + " is now " + status);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

}
