package com.project.checkinn.user.profile;

import com.project.checkinn.common.Role;
import org.springframework.stereotype.Service;
import com.project.checkinn.common.Role;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Override
    public UserResponse getById(Long id) {
        return null;
    }

    private final UserRepo userRepo;

    public UserServiceImpl(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserResponse create(UserCreateRequest request) {

        if (userRepo.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setRole(Role.GUEST);

        return new UserResponse(userRepo.save(user));
    }

    @Override
    public List<UserResponse> getAll() {
        return userRepo.findAll()
                .stream()
                .map(UserResponse::new)
                .toList();
    }}
