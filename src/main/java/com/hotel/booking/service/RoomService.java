package com.hotel.booking.service;

import com.hotel.booking.entity.Room;
import com.hotel.booking.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service layer sits between the Controller and the Repository.
 * Controllers should never talk to repositories directly — this keeps
 * business rules (like "room number must be unique") in one place.
 */
@Service
public class RoomService {

    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    // CREATE
    public Room addRoom(Room room) {
        return roomRepository.save(room);
    }

    // READ (all)
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    // READ (only rooms currently free to book) — used on the guest-facing browse page
    public List<Room> getAvailableRooms() {
        return roomRepository.findAll().stream()
                .filter(Room::isAvailable)
                .toList();
    }

    // READ (one)
    public Room getRoomById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Room not found with id: " + id));
    }

    // UPDATE
    public Room updateRoom(Long id, Room updatedRoom) {
        Room existingRoom = getRoomById(id);
        existingRoom.setRoomNumber(updatedRoom.getRoomNumber());
        existingRoom.setRoomType(updatedRoom.getRoomType());
        existingRoom.setPricePerNight(updatedRoom.getPricePerNight());
        existingRoom.setAvailable(updatedRoom.isAvailable());
        return roomRepository.save(existingRoom);
    }

    // DELETE
    public void deleteRoom(Long id) {
        roomRepository.deleteById(id);
    }
}
