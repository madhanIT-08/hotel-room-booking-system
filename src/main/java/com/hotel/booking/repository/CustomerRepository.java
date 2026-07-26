package com.hotel.booking.repository;

import com.hotel.booking.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    // Used for "Search Customer" — matches partial name, case-insensitive
    List<Customer> findByNameContainingIgnoreCase(String name);

    // Used by login (to find a self-registered customer's account) and by registration (to check for duplicates)
    java.util.Optional<Customer> findByUsername(String username);

    boolean existsByUsername(String username);
}
