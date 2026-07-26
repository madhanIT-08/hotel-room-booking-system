package com.hotel.booking.controller;

import com.hotel.booking.entity.Booking;
import com.hotel.booking.service.BookingService;
import com.hotel.booking.service.CustomerService;
import com.hotel.booking.service.RoomService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final RoomService roomService;
    private final CustomerService customerService;

    public BookingController(BookingService bookingService, RoomService roomService, CustomerService customerService) {
        this.bookingService = bookingService;
        this.roomService = roomService;
        this.customerService = customerService;
    }

    // READ — list all bookings
    @GetMapping
    public String listBookings(Model model) {
        model.addAttribute("bookings", bookingService.getAllBookings());
        return "bookings/list";
    }

    // Show the "New Booking" form
    @GetMapping("/new")
    public String newBookingForm(Model model) {
        model.addAttribute("booking", new Booking());
        model.addAttribute("rooms", roomService.getAllRooms());
        model.addAttribute("customers", customerService.getAllCustomers());
        return "bookings/form";
    }

    // CREATE
    @PostMapping
    public String saveBooking(@Valid @ModelAttribute("booking") Booking booking, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("rooms", roomService.getAllRooms());
            model.addAttribute("customers", customerService.getAllCustomers());
            return "bookings/form";
        }
        bookingService.createBooking(booking);
        return "redirect:/bookings";
    }

    // Show the "Edit Booking" form
    @GetMapping("/edit/{id}")
    public String editBookingForm(@PathVariable Long id, Model model) {
        model.addAttribute("booking", bookingService.getBookingById(id));
        model.addAttribute("rooms", roomService.getAllRooms());
        model.addAttribute("customers", customerService.getAllCustomers());
        return "bookings/form";
    }

    // UPDATE
    @PostMapping("/update/{id}")
    public String updateBooking(@PathVariable Long id, @Valid @ModelAttribute("booking") Booking booking, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("rooms", roomService.getAllRooms());
            model.addAttribute("customers", customerService.getAllCustomers());
            return "bookings/form";
        }
        bookingService.updateBooking(id, booking);
        return "redirect:/bookings";
    }

    // DELETE
    @GetMapping("/delete/{id}")
    public String deleteBooking(@PathVariable Long id) {
        bookingService.deleteBooking(id);
        return "redirect:/bookings";
    }

    // CHECK-IN — one click, sets status to CHECKED_IN
    @GetMapping("/checkin/{id}")
    public String checkIn(@PathVariable Long id) {
        bookingService.updateStatus(id, Booking.BookingStatus.CHECKED_IN);
        return "redirect:/bookings";
    }

    // CHECK-OUT — one click, sets status to CHECKED_OUT and frees up the room
    @GetMapping("/checkout/{id}")
    public String checkOut(@PathVariable Long id) {
        bookingService.updateStatus(id, Booking.BookingStatus.CHECKED_OUT);
        return "redirect:/bookings";
    }
}
