package com.hotel.booking.controller;

import com.hotel.booking.entity.Room;
import com.hotel.booking.service.RoomService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;

@Controller
@RequestMapping("/rooms")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    // READ — list all rooms
    @GetMapping
    public String listRooms(Model model) {
        model.addAttribute("rooms", roomService.getAllRooms());
        return "rooms/list";
    }

    // Show the "Add Room" form
    @GetMapping("/new")
    public String newRoomForm(Model model) {
        model.addAttribute("room", new Room());
        return "rooms/form";
    }

    // CREATE — save a new room
    @PostMapping
    public String saveRoom(@Valid @ModelAttribute("room") Room room, BindingResult result) {
        if (result.hasErrors()) {
            return "rooms/form";
        }
        roomService.addRoom(room);
        return "redirect:/rooms";
    }

    // Show the "Edit Room" form, pre-filled with existing data
    @GetMapping("/edit/{id}")
    public String editRoomForm(@PathVariable Long id, Model model) {
        model.addAttribute("room", roomService.getRoomById(id));
        return "rooms/form";
    }

    // UPDATE — save changes to an existing room
    @PostMapping("/update/{id}")
    public String updateRoom(@PathVariable Long id, @Valid @ModelAttribute("room") Room room, BindingResult result) {
        if (result.hasErrors()) {
            return "rooms/form";
        }
        roomService.updateRoom(id, room);
        return "redirect:/rooms";
    }

    // DELETE
    @GetMapping("/delete/{id}")
    public String deleteRoom(@PathVariable Long id) {
        roomService.deleteRoom(id);
        return "redirect:/rooms";
    }
}
