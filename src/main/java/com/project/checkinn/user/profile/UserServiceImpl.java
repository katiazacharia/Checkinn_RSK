package com.project.checkinn.user.profile;

import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import static org.springframework.http.HttpStatus.*;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepo userRepo;

    public UserServiceImpl(UserRepo userRepo) {
        this.userRepo = userRepo;
    }
    @Override
    public UserResponse create(UserCreateRequest request) {

        if (userRepo.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(CONFLICT, "Email already exists");
        }
        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());

        User saved = userRepo.save(user);

        return mapToResponse(saved);
    }
    @Override
    public List<UserResponse> getAll() {
        return userRepo.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }
    @Override
    public UserResponse getById(Long id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "User not found"));

        return mapToResponse(user);
    }

    private UserResponse mapToResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getPhone()
        );
    }
    }
