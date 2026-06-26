package com.leonardoalvarenga.scoutly.auth;

import com.leonardoalvarenga.scoutly.auth.dtos.AuthResponseDTO;
import com.leonardoalvarenga.scoutly.auth.dtos.LoginRequestDTO;
import com.leonardoalvarenga.scoutly.auth.dtos.RegisterRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO data) {
        try {
            return ResponseEntity.ok(authService.login(data));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body("E-mail ou senha incorretos");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDTO data) {
        try {
            authService.register(data);
            return ResponseEntity.status(201).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/guest")
    public ResponseEntity<AuthResponseDTO> guestLogin() {
        return ResponseEntity.ok(authService.guestLogin());
    }
}
