package com.project.checkinn.user.profile;


import com.project.checkinn.security.AppUserRepository;
import com.project.checkinn.loyalty.account.LoyaltyAccount;
import com.project.checkinn.loyalty.account.LoyaltyAccountRepo;
import com.project.checkinn.security.CurrentUserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;
    private final LoyaltyAccountRepo loyaltyAccountRepo;
    private final CurrentUserService currentUserService;

    public UserServiceImpl(UserRepo userRepo, LoyaltyAccountRepo loyaltyAccountRepo, AppUserRepository appuserRepo, CurrentUserService currentUserService) {
        this.userRepo = userRepo;
        this.loyaltyAccountRepo = loyaltyAccountRepo;
        this.currentUserService = currentUserService;
    }

    @Override
    public UserResponse updateMyProfile(UserCreateRequest request,Authentication authentication) {

        if (request == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "request is required");

        Long currentUserId = currentUserService.getCurrentUserId(authentication);


        User user = userRepo.findById(currentUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User profile not found"));

        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            boolean emailTaken = userRepo.existsByEmail(request.getEmail()) &&
                    (user.getEmail() == null || !user.getEmail().equals(request.getEmail()));

            if (emailTaken) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
            }

            user.setEmail(request.getEmail());
        }
        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }

        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }

        User savedUser = userRepo.save(user);

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
    public UserResponse getMyProfile(Authentication authentication) {
        Long currentUserId = currentUserService.getCurrentUserId(authentication);

        User user = userRepo.findById(currentUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        return new UserResponse(user);
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
