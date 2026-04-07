package com.project.checkinn.catalog.hotel;

import com.project.checkinn.ImageStorageService;
import com.project.checkinn.catalog.amenity.Amenity;
import com.project.checkinn.catalog.amenity.AmenityRepo;
import com.project.checkinn.security.CurrentUserService;
import com.project.checkinn.security.Role;
import com.project.checkinn.user.profile.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HotelServiceImplTest {

    @Mock HotelRepo hotelRepo;
    @Mock AmenityRepo amenityRepo;
    @Mock ImageStorageService imageStorageService;
    @Mock CurrentUserService currentUserService;
    @Mock Authentication authentication;

    @InjectMocks HotelServiceImpl service;

    private HotelRequest request;
    private User manager;

    @BeforeEach
    void setUp() {
        request = new HotelRequest();
        request.setName("CheckInn Grand Istanbul");
        request.setCity("Istanbul");

        manager = new User();
        manager.setRole(Role.MANAGER);
        manager.setFullName("Manager");
    }

    @Test
    void create_shouldAssignCurrentUserAsManager() {
        when(currentUserService.getCurrentUser(authentication)).thenReturn(manager);
        when(hotelRepo.save(any(Hotel.class))).thenAnswer(inv -> inv.getArgument(0));

        HotelDetailsResponse response = service.create(request, authentication);

        assertEquals("CheckInn Grand Istanbul", response.getName());
        verify(hotelRepo).save(any(Hotel.class));
    }

    @Test
    void getById_shouldThrow_whenNotFound() {
        when(hotelRepo.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> service.getById(1L));
    }

    @Test
    void uploadImage_shouldAppendPath() {
        Hotel hotel = new Hotel();
        hotel.setImageUrls(new java.util.ArrayList<>());
        MultipartFile file = mock(MultipartFile.class);
        when(hotelRepo.findById(1L)).thenReturn(Optional.of(hotel));
        when(imageStorageService.saveHotelImage(file)).thenReturn("/Uploads/hotels/h1.jpg");

        String result = service.uploadImage(1L, file);

        assertEquals("/Uploads/hotels/h1.jpg", result);
        assertEquals(1, hotel.getImageUrls().size());
    }

    @Test
    void getAll_shouldReturnMappedPage() {
        Hotel hotel = new Hotel();
        hotel.setName("H");
        when(hotelRepo.findAll(any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(hotel)));

        var page = service.getAll(PageRequest.of(0, 10));
        assertEquals(1, page.getTotalElements());
    }
}
