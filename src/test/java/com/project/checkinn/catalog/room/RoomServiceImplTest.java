package com.project.checkinn.catalog.room;

import com.project.checkinn.ImageStorageService;
import com.project.checkinn.catalog.amenity.Amenity;
import com.project.checkinn.catalog.amenity.AmenityRepo;
import com.project.checkinn.catalog.hotel.Hotel;
import com.project.checkinn.catalog.hotel.HotelRepo;
import com.project.checkinn.common.RoomStatus;
import com.project.checkinn.common.RoomType;
import com.project.checkinn.exchangerate.ExchangeRateConfig;
import com.project.checkinn.exchangerate.ExchangeRateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomServiceImplTest {

    @Mock RoomRepo roomRepo;
    @Mock HotelRepo hotelRepo;
    @Mock ExchangeRateService exchangeRateService;
    @Mock ExchangeRateConfig exchangeRateConfig;
    @Mock ImageStorageService imageStorageService;
    @Mock AmenityRepo amenityRepo;

    @InjectMocks RoomServiceImpl service;

    private RoomRequest request;

    @BeforeEach
    void setUp() {
        request = new RoomRequest();
        request.setHotelId(1L);
        request.setRoomNumber("101");
        request.setType(RoomType.SINGLE);
        request.setPricePerNight(BigDecimal.valueOf(100));
        request.setCapacity(2);
        request.setStatus(RoomStatus.AVAILABLE);
    }

    @Test
    void create_shouldSaveRoom_whenValid() {
        Hotel hotel = new Hotel();
        when(hotelRepo.findById(1L)).thenReturn(Optional.of(hotel));
        when(roomRepo.existsByHotelIdAndRoomNumber(1L, "101")).thenReturn(false);
        when(roomRepo.save(any(Room.class))).thenAnswer(inv -> inv.getArgument(0));

        RoomResponse response = service.create(request);

        assertEquals("101", response.getRoomNumber());
        verify(roomRepo).save(any(Room.class));
    }

    @Test
    void create_shouldThrowConflict_whenDuplicateRoomNumber() {
        Hotel hotel = new Hotel();
        when(hotelRepo.findById(1L)).thenReturn(Optional.of(hotel));
        when(roomRepo.existsByHotelIdAndRoomNumber(1L, "101")).thenReturn(true);

        assertThrows(ResponseStatusException.class, () -> service.create(request));
    }

    @Test
    void delete_shouldThrow_whenRoomNotFound() {
        when(roomRepo.existsById(50L)).thenReturn(false);
        assertThrows(ResponseStatusException.class, () -> service.delete(50L));
    }

    @Test
    void getRoomsByHotelId_shouldThrow_whenHotelMissing() {
        when(hotelRepo.existsById(1L)).thenReturn(false);
        assertThrows(ResponseStatusException.class,
                () -> service.getRoomsByHotelId(1L, PageRequest.of(0, 10)));
    }

    @Test
    void uploadImage_shouldStoreAndAppendPath() {
        Room room = new Room();
        MultipartFile file = mock(MultipartFile.class);
        when(roomRepo.findById(1L)).thenReturn(Optional.of(room));
        when(imageStorageService.saveRoomImage(file)).thenReturn("/Uploads/rooms/r1.jpg");

        String path = service.uploadImage(1L, file);

        assertEquals("/Uploads/rooms/r1.jpg", path);
        assertTrue(room.getImageUrls().contains(path));
    }
}
