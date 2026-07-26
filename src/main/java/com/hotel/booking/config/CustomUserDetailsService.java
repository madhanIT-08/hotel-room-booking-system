package com.hotel.booking.config;

import com.hotel.booking.entity.Customer;
import com.hotel.booking.repository.CustomerRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Spring Security calls loadUserByUsername() every time someone tries to log in.
 * We check two possible sources here:
 *   1. The single hardcoded Admin account ("admin")
 *   2. Any self-registered Customer (guest) account, looked up from the database
 *
 * Whichever one matches determines the ROLE assigned — ROLE_ADMIN or ROLE_USER —
 * which is what the rest of SecurityConfig uses to decide what pages they can see.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final String ADMIN_USERNAME = "admin";

    private final CustomerRepository customerRepository;
    private final String encodedAdminPassword;

    public CustomUserDetailsService(CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        // "admin123" — encoded once, kept in memory. In a real app this would never be hardcoded.
        this.encodedAdminPassword = passwordEncoder.encode("admin123");
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (ADMIN_USERNAME.equals(username)) {
            return User.builder()
                    .username(ADMIN_USERNAME)
                    .password(encodedAdminPassword)
                    .roles("ADMIN")
                    .build();
        }

        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("No account found for username: " + username));

        return User.builder()
                .username(customer.getUsername())
                .password(customer.getPassword())
                .roles("USER")
                .build();
    }
}
