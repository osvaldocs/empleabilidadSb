package com.example.demo.application.service;

import com.example.demo.application.port.in.AuthUseCase;
import com.example.demo.domain.model.User;
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
        // Verificar si el usuario ya existe
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        // Crear nuevo usuario con contraseña hasheada
        UUID userId = UUID.randomUUID();
        String hashedPassword = passwordEncoder.encode(password);
        User user = new User(userId, username, hashedPassword);

        // Guardar usuario en la base de datos
        userRepository.save(user);

        // Generar token JWT
        String token = jwtService.generateToken(userId);
        return new AuthResponse("User registered: " + username, token);
    }

    @Override
    public AuthResponse login(String username, String password) {
        // Buscar usuario por username
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        // Validar contraseña
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        // Generar token JWT con el userId real del usuario
        String token = jwtService.generateToken(user.getId());
        return new AuthResponse("Login successful", token);
    }
}
