package com.aero.quickfix.controller;

import com.aero.quickfix.dto.AuthResponse;
import com.aero.quickfix.dto.LoginRequest;
import com.aero.quickfix.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication REST Controller.
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * User login endpoint.
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        log.info("Login attempt for user: {}", request.getUsername());
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * User registration endpoint.
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody LoginRequest request) {
        log.info("Registration attempt for user: {}", request.getUsername());
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Verify token endpoint.
     */
    @PostMapping("/verify")
    public ResponseEntity<AuthResponse> verify(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.ok(new AuthResponse(false, "No token provided", null, null));
        }

        String token = authHeader.substring(7);
        var usernameOpt = authService.validateAndGetUsername(token);

        if (usernameOpt.isPresent()) {
            return ResponseEntity.ok(new AuthResponse(true, "Token is valid", token, usernameOpt.get()));
        } else {
            return ResponseEntity.ok(new AuthResponse(false, "Invalid or expired token", null, null));
        }
    }

    /**
     * Health check for auth service.
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Auth service is running");
    }
}
