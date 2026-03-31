package com.project.checkinn.user.profile;

import org.springframework.security.core.Authentication;

import java.util.List;

public interface UserService {
    UserResponse updateMyProfile(UserCreateRequest request, Authentication authentication);

    UserResponse getMyProfile(Authentication authentication);
    List<UserResponse> getAll();

    UserResponse getById(Long id);
}
