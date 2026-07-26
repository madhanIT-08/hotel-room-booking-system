package com.hotel.booking.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

/**
 * Two roles exist in this app:
 *   ROLE_ADMIN — the single hardcoded admin account (admin / admin123)
 *   ROLE_USER  — any guest who self-registers via /register
 *
 * CustomUserDetailsService.java decides which role a login belongs to.
 * This class decides which URLs each role is allowed to visit.
 */
@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // After a successful login, send Admins to the admin dashboard ("/")
    // and Guests to their own dashboard ("/user/dashboard").
    @Bean
    public AuthenticationSuccessHandler successHandler() {
        return (request, response, authentication) -> {
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            response.sendRedirect(isAdmin ? "/" : "/user/dashboard");
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Public — no login needed
                .requestMatchers("/css/**", "/js/**", "/h2-console/**", "/register", "/login").permitAll()

                // Admin-only areas: room inventory, full customer list, full booking management
                .requestMatchers("/rooms/**", "/customers/**", "/bookings/**").hasRole("ADMIN")
                .requestMatchers("/").hasRole("ADMIN")

                // Guest-only areas: browsing rooms, booking, viewing their own bookings
                .requestMatchers("/user/**").hasRole("USER")

                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .successHandler(successHandler())
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            )
            // Needed so the H2 console (which uses frames) still works with security enabled
            .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"))
            .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

        return http.build();
    }
}
