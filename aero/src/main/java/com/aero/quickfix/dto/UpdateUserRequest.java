package com.aero.quickfix.dto;

/**
 * DTO for updating user information.
 */
public class UpdateUserRequest {
    private String email;
    private boolean enabled;

    public UpdateUserRequest() {}

    public UpdateUserRequest(String email, boolean enabled) {
        this.email = email;
        this.enabled = enabled;
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
}
