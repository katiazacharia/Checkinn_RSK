package com.project.checkinn.security;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class UserSeeder {

    @Bean
    CommandLineRunner seedUsers(AppUserRepository repo, PasswordEncoder encoder) {
        return args -> {

            seedIfMissing(repo, encoder, "admin", "Admin@123", Role.ADMIN);
            seedIfMissing(repo, encoder, "hr", "Hr@123", Role.MANAGER);
            seedIfMissing(repo, encoder, "employee", "Emp@123", Role.CUSTOMER);
        };
    }

    private void seedIfMissing(AppUserRepository repo, PasswordEncoder encoder,
                               String username, String rawPassword, Role role) {
        if (!repo.existsByUsername(username)) {
            AppUser user = new AppUser();
            user.setUsername(username);
            user.setPasswordHash(encoder.encode(rawPassword));
            user.setRole(role);
            user.setEnabled(true);
            repo.save(user);
        }
    }
}