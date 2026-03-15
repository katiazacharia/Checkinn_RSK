package com.project.checkinn.security;

import com.project.checkinn.user.profile.User;
import jakarta.persistence.*;

@Entity
@Table(name = "app_users")
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 120)
    private String username;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private boolean enabled = true;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Role role;

    @OneToOne(mappedBy = "appUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private User user;

    public AppUser() {}

    public AppUser(String username, String passwordHash, boolean enabled, Role role) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.enabled = enabled;
        this.role = role;
    }

    public Long getId() { return id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
}