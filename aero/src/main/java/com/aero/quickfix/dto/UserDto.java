package com.aero.quickfix.dto;

/**
 * DTO for User information (without password).
 */
public class UserDto {
    private String userId;
    private String username;
    private String email;
    private boolean enabled;
    private long createdAt;

    public UserDto(String userId, String username, String email, boolean enabled, long createdAt) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.enabled = enabled;
        this.createdAt = createdAt;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}
