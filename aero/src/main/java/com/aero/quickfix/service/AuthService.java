package com.aero.quickfix.service;

import com.aero.quickfix.dto.AuthResponse;
import com.aero.quickfix.dto.LoginRequest;
import com.aero.quickfix.model.User;
import com.aero.quickfix.repository.UserRepository;
import com.aero.quickfix.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service for handling authentication operations.
 */
@Service
public class AuthService {
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    /**
     * Authenticate user and return JWT token.
     */
    public AuthResponse login(LoginRequest request) {
        Optional<User> userOpt = userRepository.findByUsername(request.getUsername());

        if (userOpt.isEmpty()) {
            log.warn("Login attempt with non-existent username: {}", request.getUsername());
            return new AuthResponse(false, "Invalid username or password", null, null);
        }

        User user = userOpt.get();

        // For demo: accept plain text password (in production, use BCrypt)
        if (!user.getPassword().equals(request.getPassword())) {
            log.warn("Failed login attempt for user: {}", user.getUsername());
            return new AuthResponse(false, "Invalid username or password", null, null);
        }

        if (!user.isEnabled()) {
            log.warn("Login attempt for disabled user: {}", user.getUsername());
            return new AuthResponse(false, "User account is disabled", null, null);
        }

        String token = jwtUtil.generateToken(user.getUsername());
        log.info("User logged in successfully: {}", user.getUsername());

        return new AuthResponse(true, "Login successful", token, user.getUsername());
    }

    /**
     * Register new user.
     */
    public AuthResponse register(LoginRequest request) {
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            return new AuthResponse(false, "Username is required", null, null);
        }

        if (request.getPassword() == null || request.getPassword().length() < 6) {
            return new AuthResponse(false, "Password must be at least 6 characters", null, null);
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            log.warn("Registration attempt with existing username: {}", request.getUsername());
            return new AuthResponse(false, "Username already exists", null, null);
        }

        User newUser = new User(request.getUsername(), request.getUsername() + "@aero.com", request.getPassword());
        userRepository.save(newUser);

        String token = jwtUtil.generateToken(newUser.getUsername());
        log.info("New user registered: {}", newUser.getUsername());

        return new AuthResponse(true, "Registration successful", token, newUser.getUsername());
    }

    /**
     * Validate JWT token and extract username.
     */
    public Optional<String> validateAndGetUsername(String token) {
        if (token == null || !jwtUtil.validateToken(token)) {
            return Optional.empty();
        }

        String username = jwtUtil.extractUsername(token);
        if (username != null && !jwtUtil.isTokenExpired(token)) {
            return Optional.of(username);
        }

        return Optional.empty();
    }
}
