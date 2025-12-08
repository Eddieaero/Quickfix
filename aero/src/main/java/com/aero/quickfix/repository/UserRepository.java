package com.aero.quickfix.repository;

import com.aero.quickfix.model.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Repository for managing User entities in memory.
 */
@Repository
public class UserRepository {
    private final ConcurrentHashMap<String, User> users = new ConcurrentHashMap<>();

    public UserRepository() {
        // Initialize with default user for testing
        User defaultUser = new User("admin", "admin@aero.com", "admin123");
        defaultUser.setUserId("USER_DEFAULT");
        users.put(defaultUser.getUsername(), defaultUser);
    }

    public User save(User user) {
        if (user.getUserId() == null) {
            user.setUserId("USER_" + System.currentTimeMillis());
        }
        users.put(user.getUsername(), user);
        return user;
    }

    public Optional<User> findByUsername(String username) {
        return Optional.ofNullable(users.get(username));
    }

    public Optional<User> findByUserId(String userId) {
        return users.values().stream()
                .filter(u -> u.getUserId().equals(userId))
                .findFirst();
    }

    public Optional<User> findByEmail(String email) {
        return users.values().stream()
                .filter(u -> u.getEmail().equals(email))
                .findFirst();
    }

    public boolean existsByUsername(String username) {
        return users.containsKey(username);
    }

    public boolean existsByEmail(String email) {
        return users.values().stream()
                .anyMatch(u -> u.getEmail().equals(email));
    }

    public java.util.List<User> findAll() {
        return new java.util.ArrayList<>(users.values());
    }

    public void deleteByUsername(String username) {
        users.remove(username);
    }

    public void deleteByUserId(String userId) {
        users.values().removeIf(u -> u.getUserId().equals(userId));
    }

    public long count() {
        return users.size();
    }
}
