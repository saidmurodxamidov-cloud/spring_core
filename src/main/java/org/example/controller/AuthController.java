package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.request.AuthRequest;
import org.example.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Validated @RequestBody AuthRequest authRequest){
        String token  = authService.login(authRequest.getUsername(),authRequest.getPassword());
        return ResponseEntity.status(HttpStatus.OK).body(token);
    }
}
