package com.hotel.booking.controller;

import com.hotel.booking.entity.Booking;
import com.hotel.booking.entity.Customer;
import com.hotel.booking.entity.Room;
import com.hotel.booking.repository.CustomerRepository;
import com.hotel.booking.service.BookingService;
import com.hotel.booking.service.RoomService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * Everything a logged-in Guest (ROLE_USER) can do: see their dashboard,
 * browse rooms that are currently available, book one for themselves,
 * and view only their own bookings.
 *
 * Notice how every method here first looks up the logged-in Customer
 * via the Authentication object — this is how we know "who is asking"
 * without them having to type their own name every time.
 */
@Controller
@RequestMapping("/user")
public class UserController {

    private final RoomService roomService;
    private final BookingService bookingService;
    private final CustomerRepository customerRepository;

    public UserController(RoomService roomService, BookingService bookingService, CustomerRepository customerRepository) {
        this.roomService = roomService;
        this.bookingService = bookingService;
        this.customerRepository = customerRepository;
    }

    // Helper: find the Customer record that matches whoever is currently logged in
    private Customer currentCustomer(Authentication authentication) {
        String username = authentication.getName();
        return customerRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Logged-in user has no matching customer record"));
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        model.addAttribute("customer", currentCustomer(authentication));
        return "user/dashboard";
    }

    // Browse — only rooms that are currently free
    @GetMapping("/rooms")
    public String browseRooms(Model model) {
        model.addAttribute("rooms", roomService.getAvailableRooms());
        return "user/available-rooms";
    }

    // Show the booking form for one specific room
    @GetMapping("/book/{roomId}")
    public String showBookingForm(@PathVariable Long roomId, Model model) {
        Room room = roomService.getRoomById(roomId);
        model.addAttribute("room", room);
        return "user/book-form";
    }

    // Submit the booking — customer is taken from the logged-in session, not a form field
    @PostMapping("/book/{roomId}")
    public String submitBooking(@PathVariable Long roomId,
                                 @RequestParam LocalDate checkInDate,
                                 @RequestParam LocalDate checkOutDate,
                                 Authentication authentication) {
        Room room = roomService.getRoomById(roomId);

        Booking booking = new Booking();
        booking.setCustomer(currentCustomer(authentication));
        booking.setRoom(room);
        booking.setCheckInDate(checkInDate);
        booking.setCheckOutDate(checkOutDate);
        booking.setStatus(Booking.BookingStatus.CONFIRMED);

        bookingService.createBooking(booking);
        return "redirect:/user/my-bookings";
    }

    // Only this guest's own bookings — never anyone else's
    @GetMapping("/my-bookings")
    public String myBookings(Authentication authentication, Model model) {
        Customer customer = currentCustomer(authentication);
        model.addAttribute("bookings", bookingService.getBookingsForCustomer(customer.getId()));
        return "user/my-bookings";
    }
}
