package com.project.checkinn.catalog.room;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RoomRepo extends JpaRepository<Room, Long>, JpaSpecificationExecutor<Room> {

    boolean existsByHotelIdAndRoomNumber(Long hotelId, String roomNumber);
}