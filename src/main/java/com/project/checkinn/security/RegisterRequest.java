package com.project.checkinn.security;

import java.util.Set;

public record RegisterRequest(
        Long userId,
        String username,
        String password,
        Set<Role> roles
) {}
