package com.project.checkinn.user.profile;

import java.util.List;

public interface UserService {
    UserResponse create(UserCreateRequest request);

    List<UserResponse> getAll();

    UserResponse getById(Long id);
}
