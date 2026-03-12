package com.project.checkinn.security;

import java.util.Set;

public record RegisterResponse(
        Long id,
        String username,
        String roles
) {}
