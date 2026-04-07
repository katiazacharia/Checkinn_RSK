package com.project.checkinn.catalog.amenity;

import com.project.checkinn.catalog.hotel.Hotel;
import com.project.checkinn.catalog.hotel.HotelRepo;
import com.project.checkinn.catalog.room.Room;
import com.project.checkinn.catalog.room.RoomRepo;
import com.project.checkinn.common.AmenityType;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AmenityServiceImplTest {

    @Mock AmenityRepo amenityRepo;
    @Mock EntityManager entityManager;
    @Mock HotelRepo hotelRepo;
    @Mock RoomRepo roomRepo;

    @InjectMocks AmenityServiceImpl service;

    private AmenityRequest request;

    @BeforeEach
    void setup() {
        request = new AmenityRequest();
        request.setName("WiFi");
        request.setIcon("wifi");
        request.setDescription("desc");
        request.setType(AmenityType.HOTEL);
    }

    @Test
    void create_shouldSaveAmenity_whenValid() {
        when(amenityRepo.existsByNameIgnoreCase("WiFi")).thenReturn(false);
        Amenity saved = new Amenity();
        saved.setName("WiFi");
        saved.setIcon("wifi");
        saved.setDescription("desc");
        saved.setType(AmenityType.HOTEL);
        when(amenityRepo.save(any(Amenity.class))).thenReturn(saved);

        AmenityResponse response = service.create(request);

        assertEquals("WiFi", response.getName());
        verify(amenityRepo).save(any(Amenity.class));
    }

    @Test
    void create_shouldThrowConflict_whenNameExists() {
        when(amenityRepo.existsByNameIgnoreCase("WiFi")).thenReturn(true);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.create(request));
        assertEquals(409, ex.getStatusCode().value());
    }

    @Test
    void getById_shouldThrow_whenNotFound() {
        when(amenityRepo.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> service.getById(99L));
    }

    @Test
    void getAll_shouldFilterByName() {
        Amenity a = new Amenity();
        a.setName("WiFi");
        a.setType(AmenityType.HOTEL);
        when(amenityRepo.findByNameContainingIgnoreCase(eq("wifi"), any()))
                .thenReturn(new PageImpl<>(List.of(a)));

        var result = service.getAll("wifi", PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void addAmenityToHotel_shouldLinkBothSides() {
        Hotel hotel = new Hotel();
        Amenity amenity = new Amenity();
        hotel.setAmenities(new java.util.HashSet<>());
        amenity.setHotels(new java.util.HashSet<>());
        when(entityManager.find(Hotel.class, 1L)).thenReturn(hotel);
        when(amenityRepo.findById(2L)).thenReturn(Optional.of(amenity));

        service.addAmenityToHotel(1L, 2L);

        assertTrue(hotel.getAmenities().contains(amenity));
        assertTrue(amenity.getHotels().contains(hotel));
    }
}
