package com.hotel.booking.service;

import com.hotel.booking.entity.Booking;
import com.hotel.booking.entity.Room;
import com.hotel.booking.repository.BookingRepository;
import com.hotel.booking.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;

    public BookingService(BookingRepository bookingRepository, RoomRepository roomRepository) {
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
    }

    // CREATE
    public Booking createBooking(Booking booking) {
        // Mark the room as unavailable once it's booked
        Room room = roomRepository.findById(booking.getRoom().getId())
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));
        room.setAvailable(false);
        roomRepository.save(room);

        return bookingRepository.save(booking);
    }

    // READ (all)
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    // READ (only bookings belonging to one customer) — used on the guest-facing "My Bookings" page
    public List<Booking> getBookingsForCustomer(Long customerId) {
        return bookingRepository.findByCustomerId(customerId);
    }

    // READ (one)
    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with id: " + id));
    }

    // UPDATE
    public Booking updateBooking(Long id, Booking updatedBooking) {
        Booking existingBooking = getBookingById(id);
        existingBooking.setGuestName(updatedBooking.getGuestName());
        existingBooking.setGuestPhone(updatedBooking.getGuestPhone());
        existingBooking.setRoom(updatedBooking.getRoom());
        existingBooking.setCheckInDate(updatedBooking.getCheckInDate());
        existingBooking.setCheckOutDate(updatedBooking.getCheckOutDate());
        existingBooking.setStatus(updatedBooking.getStatus());
        return bookingRepository.save(existingBooking);
    }

    // Dedicated status change — used by the one-click Check-In / Check-Out buttons
    public Booking updateStatus(Long id, Booking.BookingStatus newStatus) {
        Booking booking = getBookingById(id);
        booking.setStatus(newStatus);

        // Once a guest checks out, free up the room for future bookings
        if (newStatus == Booking.BookingStatus.CHECKED_OUT) {
            Room room = booking.getRoom();
            room.setAvailable(true);
            roomRepository.save(room);
        }

        return bookingRepository.save(booking);
    }

    // DELETE
    public void deleteBooking(Long id) {
        Booking booking = getBookingById(id);
        // Free up the room again when a booking is removed
        Room room = booking.getRoom();
        room.setAvailable(true);
        roomRepository.save(room);

        bookingRepository.deleteById(id);
    }
}
