package com.example.myapp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
        @NotBlank @Size(min = 3, max = 50) String username,
        @Size(min = 6, max = 100) String password,
        @NotBlank @Email String email,
        @Size(max = 20) String phone,
        boolean admin
) {}
