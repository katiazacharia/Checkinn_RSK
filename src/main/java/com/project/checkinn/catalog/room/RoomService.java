package com.project.checkinn.catalog.room;

import com.project.checkinn.catalog.amenity.AmenityResponse;
import com.project.checkinn.catalog.hotel.Hotel;
import com.project.checkinn.catalog.hotel.HotelRepo;
import com.project.checkinn.common.CurrencyCode;
import com.project.checkinn.common.RoomStatus;
import com.project.checkinn.common.RoomType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


public interface RoomService {

    Page<RoomResponse> getAll(
            Long hotelId,
            RoomType type,
            RoomStatus status,
            Integer minCapacity,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Pageable pageable
    );

    RoomResponse getById(Long id, CurrencyCode currency);

    RoomResponse create(RoomRequest req);

    RoomResponse update(Long id, RoomRequest req);

    void delete(Long id);


    String uploadImage(Long roomId, MultipartFile file);
    Page<RoomSummaryResponse> getRoomsByHotelName(String hotelName, Pageable pageable);
    Page<AmenityResponse> getAmenitiesForRoom(Long roomId, Pageable pageable);

    Page<RoomSummaryResponse> getRoomsByHotelId(Long hotelId, Pageable pageable);

    RoomDetailsWithAmenitiesResponse getRoomDetailsByHotelName(String hotelName, Long roomId);

    RoomDetailsWithAmenitiesResponse getRoomDetailsByHotelId(Long hotelId, Long roomId);
}