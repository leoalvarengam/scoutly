package com.leonardoalvarenga.scoutly.auth;

import com.leonardoalvarenga.scoutly.auth.dtos.AuthResponseDTO;
import com.leonardoalvarenga.scoutly.auth.dtos.LoginRequestDTO;
import com.leonardoalvarenga.scoutly.auth.dtos.RegisterRequestDTO;
import com.leonardoalvarenga.scoutly.user.User;
import com.leonardoalvarenga.scoutly.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;

    public AuthResponseDTO login(LoginRequestDTO data) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.password());
        var auth = this.authenticationManager.authenticate(usernamePassword);

        var token = tokenService.generateToken((User) auth.getPrincipal());
        return new AuthResponseDTO(token);
    }

    public void register(RegisterRequestDTO data) {
        if (this.userRepository.findByEmail(data.email()).isPresent()) {
            throw new IllegalArgumentException("E-mail já cadastrado");
        }

        String encryptedPassword = passwordEncoder.encode(data.password());

        User newUser = new User();
        newUser.setName(data.name());
        newUser.setEmail(data.email());
        newUser.setPassword(encryptedPassword);
        newUser.setGuest(false);

        this.userRepository.save(newUser);
    }

    public AuthResponseDTO guestLogin() {
        String guestId = UUID.randomUUID().toString().substring(0, 8);
        String guestEmail = "guest_" + guestId + "@scoutly.local";

        User guestUser = new User();
        guestUser.setName("Visitante");
        guestUser.setEmail(guestEmail);
        guestUser.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
        guestUser.setGuest(true);

        this.userRepository.save(guestUser);

        var token = tokenService.generateToken(guestUser);
        return new AuthResponseDTO(token);
    }
}
