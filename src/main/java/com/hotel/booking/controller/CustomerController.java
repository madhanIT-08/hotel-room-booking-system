package com.hotel.booking.controller;

import com.hotel.booking.entity.Customer;
import com.hotel.booking.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    // READ — list all customers, or search results if "name" query param is present
    @GetMapping
    public String listCustomers(@RequestParam(required = false) String name, Model model) {
        if (name != null && !name.isBlank()) {
            model.addAttribute("customers", customerService.searchByName(name));
            model.addAttribute("searchTerm", name);
        } else {
            model.addAttribute("customers", customerService.getAllCustomers());
        }
        return "customers/list";
    }

    // Show "Register Customer" form
    @GetMapping("/new")
    public String newCustomerForm(Model model) {
        model.addAttribute("customer", new Customer());
        return "customers/form";
    }

    // CREATE
    @PostMapping
    public String saveCustomer(@Valid @ModelAttribute("customer") Customer customer, BindingResult result) {
        if (result.hasErrors()) {
            return "customers/form";
        }
        customerService.registerCustomer(customer);
        return "redirect:/customers";
    }

    // Show "Edit Customer" form
    @GetMapping("/edit/{id}")
    public String editCustomerForm(@PathVariable Long id, Model model) {
        model.addAttribute("customer", customerService.getCustomerById(id));
        return "customers/form";
    }

    // UPDATE
    @PostMapping("/update/{id}")
    public String updateCustomer(@PathVariable Long id, @Valid @ModelAttribute("customer") Customer customer, BindingResult result) {
        if (result.hasErrors()) {
            return "customers/form";
        }
        customerService.updateCustomer(id, customer);
        return "redirect:/customers";
    }

    // DELETE
    @GetMapping("/delete/{id}")
    public String deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return "redirect:/customers";
    }
}
