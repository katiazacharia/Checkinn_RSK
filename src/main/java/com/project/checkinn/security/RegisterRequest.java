package com.project.checkinn.security;

import java.util.Set;

public record RegisterRequest(
        String username,
        String password,
        String fullName,
        String email,
        String phone
) {}
