package com.project.checkinn.user.profile;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
    @RequestMapping("/users")
    public class ProfileController {

        private final UserService userService;

        public ProfileController(UserService userService) {
            this.userService = userService;
        }

        @PutMapping("/me")
        @ResponseStatus(HttpStatus.CREATED)
        public UserResponse updateMyProfile(@RequestBody UserCreateRequest request, Authentication authentication) {
            return userService.create(request,authentication);
        }
        @GetMapping
        public List<UserResponse> getAll() {
            return userService.getAll();
        }
        @GetMapping("/{id}")
        public UserResponse getById(@PathVariable Long id) {
            return userService.getById(id);
        }
    }

