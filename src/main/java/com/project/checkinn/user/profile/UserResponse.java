package com.project.checkinn.user.profile;

import com.project.checkinn.common.Role;

public class UserResponse {
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private Role role;


    public UserResponse(User user) {
         this.id = user.getId();
         this.fullName = user.getFullName();
         this.email = user.getEmail();
         this.phone = user.getPhone();
         this.role = user.getRole();
    }

    public Long getId() {

        return id;
    }

    public String getFullName()
    {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {

        return phone;
    }
    public String getRole() {
        return role.name();
    }
}
