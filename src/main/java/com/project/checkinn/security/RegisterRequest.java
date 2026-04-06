package com.project.checkinn.security;

import jakarta.validation.constraints.Size;

import java.util.Set;

public record RegisterRequest(
        String username,
        String password,
        String fullName,
        String email,
        @Size(min = 10, max = 10, message = "Phone must be 10 digits")
        String phone,
        String role

) {}
