package com.project.checkinn.security;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(requests -> requests


                        .requestMatchers("/swagger-ui/*", "/v3/api-docs/*").permitAll()


                        .requestMatchers(HttpMethod.GET, "/hotels/*", "/rooms/*").permitAll()
                        .requestMatchers(HttpMethod.POST, "/hotels/*", "/rooms/*").hasAnyRole("MANAGER", "ADMIN")
                        .requestMatchers(HttpMethod.PUT,  "/hotels/*", "/rooms/*").hasAnyRole("MANAGER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE,"/hotels/*","/rooms/*").hasAnyRole("MANAGER", "ADMIN")


                        .requestMatchers(HttpMethod.POST, "/bookings/**").hasRole("CUSTOMER")

                        .requestMatchers(HttpMethod.GET, "/bookings/**").authenticated()

                        .requestMatchers(HttpMethod.PUT, "/bookings/**").hasAnyRole("MANAGER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/bookings/**").hasAnyRole("MANAGER", "ADMIN")


                        .requestMatchers(HttpMethod.POST, "/payments/**").hasRole("CUSTOMER")
                        .requestMatchers(HttpMethod.GET, "/payments/**").hasRole("ADMIN")

                        .requestMatchers("/notifications/**").authenticated()


                        .requestMatchers(HttpMethod.POST, "/users").permitAll()

                        .requestMatchers("/users/**").hasRole("ADMIN")


                        .requestMatchers("/aboutUs").permitAll()


                        .anyRequest().authenticated()
                )

                .httpBasic(withDefaults());



        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager() {
        return new ProviderManager(List.of(authenticationProvider()));
    }

    @Bean
    DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }
}
