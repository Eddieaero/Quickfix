package com.aero.quickfix.controller;

import com.aero.quickfix.dto.ChangePasswordRequest;
import com.aero.quickfix.dto.UpdateUserRequest;
import com.aero.quickfix.dto.UserDto;
import com.aero.quickfix.service.UserManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for user management operations.
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserManagementController {
    private static final Logger logger = LoggerFactory.getLogger(UserManagementController.class);

    private final UserManagementService userManagementService;

    public UserManagementController(UserManagementService userManagementService) {
        this.userManagementService = userManagementService;
    }

    /**
     * Get all users.
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllUsers() {
        try {
            List<UserDto> users = userManagementService.getAllUsers();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("users", users);
            response.put("total", users.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error fetching users", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error fetching users"));
        }
    }

    /**
     * Get a specific user by username.
     */
    @GetMapping("/{username}")
    public ResponseEntity<Map<String, Object>> getUser(@PathVariable String username) {
        try {
            var userOpt = userManagementService.getUserByUsername(username);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("success", false, "message", "User not found"));
            }
            return ResponseEntity.ok(Map.of("success", true, "user", userOpt.get()));
        } catch (Exception e) {
            logger.error("Error fetching user: {}", username, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error fetching user"));
        }
    }

    /**
     * Update user information.
     */
    @PutMapping("/{username}")
    public ResponseEntity<Map<String, Object>> updateUser(
            @PathVariable String username,
            @RequestBody UpdateUserRequest request) {
        try {
            var userOpt = userManagementService.updateUser(username, request);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("success", false, "message", "User not found"));
            }
            return ResponseEntity.ok(Map.of("success", true, "message", "User updated", "user", userOpt.get()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error updating user: {}", username, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error updating user"));
        }
    }

    /**
     * Change user password.
     */
    @PostMapping("/change-password")
    public ResponseEntity<Map<String, Object>> changePassword(@RequestBody ChangePasswordRequest request) {
        try {
            boolean success = userManagementService.changePassword(request);
            if (!success) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("success", false, "message", "Invalid username or password"));
            }
            return ResponseEntity.ok(Map.of("success", true, "message", "Password changed successfully"));
        } catch (Exception e) {
            logger.error("Error changing password", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error changing password"));
        }
    }

    /**
     * Delete a user.
     */
    @DeleteMapping("/{username}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable String username) {
        try {
            boolean success = userManagementService.deleteUser(username);
            if (!success) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("success", false, "message", "User not found"));
            }
            return ResponseEntity.ok(Map.of("success", true, "message", "User deleted"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error deleting user: {}", username, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error deleting user"));
        }
    }

    /**
     * Toggle user enabled status.
     */
    @PatchMapping("/{username}/toggle-status")
    public ResponseEntity<Map<String, Object>> toggleUserStatus(
            @PathVariable String username,
            @RequestParam boolean enabled) {
        try {
            var userOpt = userManagementService.toggleUserStatus(username, enabled);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("success", false, "message", "User not found"));
            }
            return ResponseEntity.ok(Map.of("success", true, "message", "User status updated", "user", userOpt.get()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error toggling user status: {}", username, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error updating user status"));
        }
    }

    /**
     * Get user count.
     */
    @GetMapping("/stats/count")
    public ResponseEntity<Map<String, Object>> getUserCount() {
        try {
            long count = userManagementService.getUserCount();
            return ResponseEntity.ok(Map.of("success", true, "totalUsers", count));
        } catch (Exception e) {
            logger.error("Error getting user count", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error getting user count"));
        }
    }
}
