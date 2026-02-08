package com.project.checkinn.catalog.room;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomRepo extends JpaRepository<Room, Long> {
    List<Room> findByHotelId(Long hotelId);
    boolean existsByHotelIdAndRoomNumber(Long hotelId, String roomNumber);
}