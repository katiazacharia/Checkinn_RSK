package com.project.checkinn;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Long, String> users = new HashMap<>();

    @Operation(summary = "Get all users", description = "Fetches a list of all users.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved users")
    })
    @GetMapping
    public ResponseEntity<List<String>> getAllUsers() {
        return ResponseEntity.ok(new ArrayList<>(users.values()));
    }

    @Operation(summary = "Get a user by ID", description = "Fetches a specific user by their ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<String> getUser(@PathVariable Long id) {
        if (!users.containsKey(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(users.get(id));
    }

    @Operation(summary = "Create a new user", description = "Adds a new user to the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created")
    })
    @PostMapping
    public ResponseEntity<String> createUser(@RequestBody String username) {
        long id = users.size() + 1;
        users.put(id, username);
        return ResponseEntity.status(201).body("User created with ID: " + id);
    }

    @Operation(summary = "Delete a user", description = "Removes a user from the system by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (!users.containsKey(id)) {
            return ResponseEntity.notFound().build();
        }
        users.remove(id);
        return ResponseEntity.noContent().build();
    }
}