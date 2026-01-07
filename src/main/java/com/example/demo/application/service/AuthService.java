package com.example.demo.application.service;

import com.example.demo.application.port.in.AuthUseCase;
import com.example.demo.domain.port.out.UserRepositoryPort;
import com.example.demo.infrastructure.controller.dto.AuthResponse;
import com.example.demo.infrastructure.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

public class AuthService implements AuthUseCase {

    private final UserRepositoryPort userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepositoryPort userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public AuthResponse register(String username, String password) {
        UUID userId = UUID.randomUUID();
        String hashedPassword = passwordEncoder.encode(password);
        String token = jwtService.generateToken(userId);
        return new AuthResponse("User registered: " + username, token);
    }

    @Override
    public AuthResponse login(String username, String password) {
        UUID userId = UUID.randomUUID(); // mock temporal
        String token = jwtService.generateToken(userId);
        return new AuthResponse("Login successful", token);
    }
}
