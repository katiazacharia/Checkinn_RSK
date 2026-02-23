package com.project.checkinn.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final AppUserRepository userRepo;

    public CustomUserDetailsService(AppUserRepository appUserRepository) {
        this.userRepo = appUserRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser appUser = userRepo.findByUsername(username).orElseThrow(()
                -> new UsernameNotFoundException("User not found: " + username));

        List<GrantedAuthority> authorities = List.of((GrantedAuthority)  () -> "ROLE_" + appUser.getRole().name());

        return new User(appUser.getUsername(),appUser.getPasswordHash(),authorities);
    }
}
