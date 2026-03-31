package com.project.checkinn.user.profile;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
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
        @ResponseStatus(HttpStatus.OK)
        @PreAuthorize("isAuthenticated()")
        public UserResponse updateMyProfile(@RequestBody UserCreateRequest request, Authentication authentication) {
            return userService.updateMyProfile(request,authentication);
        }

        @PreAuthorize("isAuthenticated()")
        @GetMapping("/me")
        public UserResponse getMyProfile(Authentication authentication) {
            return userService.getMyProfile(authentication);
        }

        @PreAuthorize("hasRole('ADMIN')")
        @GetMapping
        public List<UserResponse> getAll() {

            return userService.getAll();
        }


        @GetMapping("/{id}")
        @PreAuthorize("hasRole('ADMIN')")
        public UserResponse getById(@PathVariable Long id) {

            return userService.getById(id);
        }
    }

