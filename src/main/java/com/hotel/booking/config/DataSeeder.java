package com.hotel.booking.config;

import com.hotel.booking.entity.Booking;
import com.hotel.booking.entity.Customer;
import com.hotel.booking.entity.Room;
import com.hotel.booking.repository.BookingRepository;
import com.hotel.booking.repository.CustomerRepository;
import com.hotel.booking.repository.RoomRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataSeeder implements CommandLineRunner {

    private final RoomRepository roomRepository;
    private final CustomerRepository customerRepository;
    private final BookingRepository bookingRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(RoomRepository roomRepository, CustomerRepository customerRepository,
                       BookingRepository bookingRepository, PasswordEncoder passwordEncoder) {
        this.roomRepository = roomRepository;
        this.customerRepository = customerRepository;
        this.bookingRepository = bookingRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // --- Rooms ---
        Room r1 = roomRepository.save(new Room("101", "Single", 1500.0, true));
        Room r2 = roomRepository.save(new Room("102", "Double", 2500.0, true));
        Room r3 = roomRepository.save(new Room("201", "Deluxe", 4000.0, true));
        Room r4 = roomRepository.save(new Room("301", "Suite", 7000.0, true));
        roomRepository.save(new Room("202", "Double", 2500.0, true));

        // --- Customers (with login access — username: arjun / priya, password: guest123) ---
        Customer c1 = new Customer("Arjun Mehta", "9876543210", "arjun.mehta@example.com", "Chennai, TN");
        c1.setUsername("arjun");
        c1.setPassword(passwordEncoder.encode("guest123"));
        c1 = customerRepository.save(c1);

        Customer c2 = new Customer("Priya Nair", "9123456780", "priya.nair@example.com", "Coimbatore, TN");
        c2.setUsername("priya");
        c2.setPassword(passwordEncoder.encode("guest123"));
        c2 = customerRepository.save(c2);

        // --- Bookings ---
        Booking b1 = new Booking();
        b1.setCustomer(c1);
        b1.setRoom(r2);
        b1.setCheckInDate(LocalDate.now());
        b1.setCheckOutDate(LocalDate.now().plusDays(3));
        b1.setStatus(Booking.BookingStatus.CONFIRMED);
        r2.setAvailable(false);
        roomRepository.save(r2);
        bookingRepository.save(b1);

        Booking b2 = new Booking();
        b2.setCustomer(c2);
        b2.setRoom(r4);
        b2.setCheckInDate(LocalDate.now().minusDays(1));
        b2.setCheckOutDate(LocalDate.now().plusDays(2));
        b2.setStatus(Booking.BookingStatus.CHECKED_IN);
        r4.setAvailable(false);
        roomRepository.save(r4);
        bookingRepository.save(b2);
    }
}
