package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.request.AuthRequest;
import org.example.dto.response.TokenResponse;
import org.example.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Validated @RequestBody AuthRequest authRequest){
        String token = authService.login(authRequest.getUsername(), authRequest.getPassword());
        return ResponseEntity.status(HttpStatus.OK).body(new TokenResponse(token));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        authService.logout();
        return ResponseEntity.status(HttpStatus.OK).build();
    }
    @GetMapping
    public String hello(){
        return "Hello world";
    }
}
