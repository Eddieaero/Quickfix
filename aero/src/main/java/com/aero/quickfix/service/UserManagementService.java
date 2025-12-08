package com.aero.quickfix.service;

import com.aero.quickfix.dto.ChangePasswordRequest;
import com.aero.quickfix.dto.UpdateUserRequest;
import com.aero.quickfix.dto.UserDto;
import com.aero.quickfix.model.User;
import com.aero.quickfix.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for managing user operations.
 */
@Service
public class UserManagementService {
    private static final Logger logger = LoggerFactory.getLogger(UserManagementService.class);

    private final UserRepository userRepository;

    public UserManagementService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Get all users (without passwords).
     */
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toUserDto)
                .collect(Collectors.toList());
    }

    /**
     * Get a specific user by username.
     */
    public Optional<UserDto> getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(this::toUserDto);
    }

    /**
     * Get a specific user by user ID.
     */
    public Optional<UserDto> getUserById(String userId) {
        return userRepository.findByUserId(userId)
                .map(this::toUserDto);
    }

    /**
     * Update user information.
     */
    public Optional<UserDto> updateUser(String username, UpdateUserRequest request) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            logger.warn("User not found for update: {}", username);
            return Optional.empty();
        }

        User user = userOpt.get();

        // Check if new email is already in use by another user
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                logger.warn("Email already in use: {}", request.getEmail());
                throw new IllegalArgumentException("Email already in use");
            }
            user.setEmail(request.getEmail());
        }

        user.setEnabled(request.isEnabled());
        userRepository.save(user);
        logger.info("User updated: {}", username);
        return Optional.of(toUserDto(user));
    }

    /**
     * Change user password.
     */
    public boolean changePassword(ChangePasswordRequest request) {
        Optional<User> userOpt = userRepository.findByUsername(request.getUsername());
        if (userOpt.isEmpty()) {
            logger.warn("User not found for password change: {}", request.getUsername());
            return false;
        }

        User user = userOpt.get();

        // Verify old password (plain text for demo - use BCrypt in production)
        if (!user.getPassword().equals(request.getOldPassword())) {
            logger.warn("Invalid old password for user: {}", request.getUsername());
            return false;
        }

        user.setPassword(request.getNewPassword());
        userRepository.save(user);
        logger.info("Password changed for user: {}", request.getUsername());
        return true;
    }

    /**
     * Delete a user by username.
     */
    public boolean deleteUser(String username) {
        // Prevent deleting default admin user
        if ("admin".equals(username)) {
            logger.warn("Cannot delete admin user");
            throw new IllegalArgumentException("Cannot delete admin user");
        }

        if (!userRepository.existsByUsername(username)) {
            logger.warn("User not found for deletion: {}", username);
            return false;
        }

        userRepository.deleteByUsername(username);
        logger.info("User deleted: {}", username);
        return true;
    }

    /**
     * Enable or disable a user.
     */
    public Optional<UserDto> toggleUserStatus(String username, boolean enabled) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            logger.warn("User not found for status toggle: {}", username);
            return Optional.empty();
        }

        // Prevent disabling default admin user
        if ("admin".equals(username) && !enabled) {
            logger.warn("Cannot disable admin user");
            throw new IllegalArgumentException("Cannot disable admin user");
        }

        User user = userOpt.get();
        user.setEnabled(enabled);
        userRepository.save(user);
        logger.info("User status toggled: {} - enabled: {}", username, enabled);
        return Optional.of(toUserDto(user));
    }

    /**
     * Get total user count.
     */
    public long getUserCount() {
        return userRepository.count();
    }

    /**
     * Convert User entity to UserDto (without password).
     */
    private UserDto toUserDto(User user) {
        return new UserDto(
                user.getUserId(),
                user.getUsername(),
                user.getEmail(),
                user.isEnabled(),
                user.getCreatedAt()
        );
    }
}
