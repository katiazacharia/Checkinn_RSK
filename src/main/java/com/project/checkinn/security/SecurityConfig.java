package com.project.checkinn.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collections;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public SecurityConfig(CustomUserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/", "/login", "/error",
                                "/auth/**",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/actuator/health",
                                "/api-docs/**"
                        ).permitAll()

                        // Hotels
                        .requestMatchers(HttpMethod.GET, "/hotels/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/hotels/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/hotels/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.DELETE, "/hotels/**").hasRole("ADMIN")

                        // Rooms
                        .requestMatchers(HttpMethod.GET, "/rooms/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/rooms/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/rooms/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.DELETE, "/rooms/**").hasRole("ADMIN")

                        // Bookings
                        .requestMatchers(HttpMethod.GET, "/bookings/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/bookings/**").hasRole("CUSTOMER")
                        .requestMatchers(HttpMethod.DELETE, "/bookings/**").hasAnyRole("CUSTOMER", "ADMIN")

                        // Payments
                        .requestMatchers(HttpMethod.POST, "/payments/**").hasRole("CUSTOMER")

                        // Amenities
                        .requestMatchers(HttpMethod.GET, "/amenities/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/amenities/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/amenities/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.DELETE, "/amenities/**").hasRole("ADMIN")

                        // Loyalty
                        .requestMatchers(HttpMethod.GET, "/loyalty/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/loyalty/**").hasAnyRole("CUSTOMER", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/loyalty/**").hasAnyRole("ADMIN", "MANAGER")

                        // Notifications
                        .requestMatchers(HttpMethod.GET, "/notifications/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/notifications/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/notifications/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/notifications/**").hasAnyRole("ADMIN", "MANAGER")

                        // Promo Codes
                        .requestMatchers(HttpMethod.GET, "/promoCodes/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/promoCodes/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/promoCodes/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.PATCH, "/promoCodes/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.DELETE, "/promoCodes/**").hasRole("ADMIN")

                        // Reviews
                        .requestMatchers(HttpMethod.GET, "/reviews/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/reviews/**").hasRole("CUSTOMER")
                        .requestMatchers(HttpMethod.PUT, "/reviews/**").hasAnyRole("CUSTOMER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/reviews/**").hasAnyRole("CUSTOMER", "ADMIN")

                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth ->
                        oauth.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter()))
                );

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(Collections.singletonList(authenticationProvider()));
    }

    @Bean
    public Converter<Jwt, ? extends AbstractAuthenticationToken> jwtAuthConverter() {
        JwtGrantedAuthoritiesConverter conv = new JwtGrantedAuthoritiesConverter();
        conv.setAuthoritiesClaimName("roles");
        conv.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
        jwtConverter.setJwtGrantedAuthoritiesConverter(conv);

        return jwtConverter;
    }
}