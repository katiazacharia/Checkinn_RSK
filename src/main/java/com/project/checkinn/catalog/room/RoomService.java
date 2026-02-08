package com.project.checkinn.catalog.room;

import com.project.checkinn.catalog.hotel.Hotel;
import com.project.checkinn.catalog.hotel.HotelRepo;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoomService {

    private final RoomRepo roomRepo;
    private final HotelRepo hotelRepo;

    public RoomService(RoomRepo roomRepo, HotelRepo hotelRepo) {
        this.roomRepo = roomRepo;
        this.hotelRepo = hotelRepo;
    }

    public List<RoomResponse> getAll() {
        return roomRepo.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<RoomResponse> getByHotel(Long hotelId) {
        return roomRepo.findByHotelId(hotelId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public RoomResponse getById(Long id) {
        Room r = roomRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found"));
        return toResponse(r);
    }

    public RoomResponse create(RoomCreateRequest req) {
        if (req.getHotelId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "hotelId is required");
        }
        if (req.getRoomNumber() == null || req.getRoomNumber().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "roomNumber is required");
        }
        if (req.getType() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "type is required");
        }
        if (req.getPricePerNight() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "pricePerNight is required");
        }
        if (req.getStatus() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "status is required");
        }

        Hotel hotel = hotelRepo.findById(req.getHotelId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Hotel not found"));

        if (roomRepo.existsByHotelIdAndRoomNumber(req.getHotelId(), req.getRoomNumber())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Room number already exists in this hotel");
        }

        Room r = new Room();
        r.setHotel(hotel);
        r.setRoomNumber(req.getRoomNumber().trim());
        r.setType(req.getType());
        r.setPricePerNight(req.getPricePerNight());
        r.setCapacity(req.getCapacity());
        r.setStatus(req.getStatus());

        return toResponse(roomRepo.save(r));
    }

    public RoomResponse update(Long id, RoomCreateRequest req) {
        Room r = roomRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found"));

        // if user wants to change hotel
        if (req.getHotelId() != null && !req.getHotelId().equals(r.getHotel().getId())) {
            Hotel newHotel = hotelRepo.findById(req.getHotelId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Hotel not found"));
            r.setHotel(newHotel);
        }

        if (req.getRoomNumber() != null && !req.getRoomNumber().isBlank()) {
            String newNum = req.getRoomNumber().trim();
            Long hotelId = r.getHotel().getId();
            if (!newNum.equalsIgnoreCase(r.getRoomNumber())
                    && roomRepo.existsByHotelIdAndRoomNumber(hotelId, newNum)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Room number already exists in this hotel");
            }
            r.setRoomNumber(newNum);
        }

        if (req.getType() != null) r.setType(req.getType());
        if (req.getPricePerNight() != null) r.setPricePerNight(req.getPricePerNight());
        if (req.getCapacity() > 0) r.setCapacity(req.getCapacity());
        if (req.getStatus() != null) r.setStatus(req.getStatus());

        return toResponse(roomRepo.save(r));
    }

    public void delete(Long id) {
        if (!roomRepo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found");
        }
        roomRepo.deleteById(id);
    }

    private RoomResponse toResponse(Room r) {
        return new RoomResponse(
                r.getId(),
                r.getHotel().getId(),
                r.getRoomNumber(),
                r.getType(),
                r.getPricePerNight(),
                r.getCapacity(),
                r.getStatus()
        );
    }
}