package com.project.checkinn.user.profile;

import com.project.checkinn.security.AppUser;
import com.project.checkinn.security.Role;
import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    private Long id;

    private String fullName;


    @Column(unique = true )
    private String email;

    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.CUSTOMER;

    @OneToOne(optional = false)
    @MapsId
    @JoinColumn(name = "id")
    private AppUser appUser;

    public User() {
    }

    public Long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public AppUser getAppUser() {
        return appUser;
    }

    public void setAppUser(AppUser appUser) {
        this.appUser = appUser;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
       this.role = role;
    }
}