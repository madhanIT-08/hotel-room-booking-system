package com.hotel.booking.controller;

import com.hotel.booking.entity.Customer;
import com.hotel.booking.repository.CustomerRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegistrationController {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    public RegistrationController(CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("customer", new Customer());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute Customer customer, Model model) {
        if (customer.getUsername() == null || customer.getUsername().isBlank()) {
            model.addAttribute("error", "Username is required.");
            return "register";
        }
        if (customerRepository.existsByUsername(customer.getUsername())) {
            model.addAttribute("error", "That username is already taken. Please choose another.");
            return "register";
        }

        // Never store the raw password — always hash it before saving
        customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        customerRepository.save(customer);

        return "redirect:/login?registered";
    }
}
