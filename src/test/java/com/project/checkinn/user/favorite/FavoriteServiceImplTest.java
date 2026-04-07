package com.project.checkinn.user.favorite;

import com.project.checkinn.security.CurrentUserService;
import com.project.checkinn.user.profile.User;
import com.project.checkinn.user.profile.UserRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FavoriteServiceImplTest {

    @Mock FavoriteRepo favoriteRepo;
    @Mock UserRepo userRepo;
    @Mock CurrentUserService currentUserService;
    @Mock Authentication authentication;

    @InjectMocks FavoriteServiceImpl service;

    @Test
    void add_shouldSaveFavorite_whenNotExists() {
        FavoriteRequest request = new FavoriteRequest();
        request.setItemId(10L);
        User user = new User();
        when(currentUserService.getCurrentUserId(authentication)).thenReturn(1L);
        when(favoriteRepo.existsByUser_IdAndItemId(1L, 10L)).thenReturn(false);
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(favoriteRepo.save(any(Favorite.class))).thenAnswer(inv -> inv.getArgument(0));

        FavoriteResponse response = service.add(request, authentication);

        assertEquals(10L, response.getItemId());
    }

    @Test
    void add_shouldThrowConflict_whenDuplicate() {
        FavoriteRequest request = new FavoriteRequest();
        request.setItemId(10L);
        when(currentUserService.getCurrentUserId(authentication)).thenReturn(1L);
        when(favoriteRepo.existsByUser_IdAndItemId(1L, 10L)).thenReturn(true);

        assertThrows(ResponseStatusException.class, () -> service.add(request, authentication));
    }

    @Test
    void deleteByUserAndItem_shouldThrow_whenNothingDeleted() {
        when(currentUserService.getCurrentUserId(authentication)).thenReturn(1L);
        when(favoriteRepo.deleteByUser_IdAndItemId(1L, 10L)).thenReturn(0L);

        assertThrows(ResponseStatusException.class, () -> service.deleteByUserAndItem(10L, authentication));
    }
}
