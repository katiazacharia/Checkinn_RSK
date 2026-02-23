package com.project.checkinn.user.profile;


import com.project.checkinn.security.Role;
import com.project.checkinn.loyalty.account.LoyaltyAccount;
import com.project.checkinn.loyalty.account.LoyaltyAccountRepo;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;
    private final LoyaltyAccountRepo loyaltyAccountRepo;

    public UserServiceImpl(UserRepo userRepo, LoyaltyAccountRepo loyaltyAccountRepo) {
        this.userRepo = userRepo;
        this.loyaltyAccountRepo = loyaltyAccountRepo;
    }

    @Override
    public UserResponse create(UserCreateRequest request) {

        if (request == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "request is required");

        if (request.getEmail() == null || request.getEmail().isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "email is required");

        if (userRepo.existsByEmail(request.getEmail()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setRole(Role.CUSTOMER);

        User savedUser = userRepo.save(user);

        // Create loyalty account automatically for this user
        if (!loyaltyAccountRepo.existsByUser_Id(savedUser.getId())) {
            LoyaltyAccount acc = new LoyaltyAccount();
            acc.setUser(savedUser);
            acc.setPoints(0);
            acc.setUpdatedAt(LocalDateTime.now());
            loyaltyAccountRepo.save(acc);
        }

        return new UserResponse(savedUser);
    }
    @Override
    public List<UserResponse> getAll() {
        return userRepo.findAll()
                .stream()
                .map(UserResponse::new)
                .toList();
    }
    @Override
    public UserResponse getById(Long id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return new UserResponse(user);
    }
}
