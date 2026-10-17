package com.example.myapp.dto;

import com.example.myapp.entities.User;

public record UserResponse(Long id, String username, String email, String phone, boolean admin) {

    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getUsername(), user.getEmail(), user.getPhone(), user.isAdmin());
    }
}
