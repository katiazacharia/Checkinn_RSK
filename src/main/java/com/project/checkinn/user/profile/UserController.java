package com.project.checkinn.user.profile;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
    @RequestMapping("/users")
    public class UserController {

        private final UserService userService;

        public UserController(UserService userService) {
            this.userService = userService;
        }
        @PostMapping
        @ResponseStatus(HttpStatus.CREATED)
        public UserResponse create(@RequestBody UserCreateRequest request) {
            return userService.create(request);
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

