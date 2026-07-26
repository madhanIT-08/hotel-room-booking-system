package com.hotel.booking.repository;

import com.hotel.booking.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * JpaRepository already gives us save(), findAll(), findById(), deleteById() for free.
 * We don't write any SQL here — Spring Data JPA generates the implementation at runtime.
 */
public interface RoomRepository extends JpaRepository<Room, Long> {

    boolean existsByRoomNumber(String roomNumber);
}
