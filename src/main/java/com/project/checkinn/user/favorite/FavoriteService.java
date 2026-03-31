package com.project.checkinn.user.favorite;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface FavoriteService {
    FavoriteResponse add(FavoriteRequest request, Authentication authentication);

    void deleteById(Long id);

    void deleteByUserAndItem(Long itemId,Authentication authentication);

    FavoriteResponse getById(Long id);

    boolean exists(Long itemId, Authentication authentication);

    Page<FavoriteResponse> search(Long itemId, Pageable pageable, Authentication authentication);
    List<FavoriteResponse> getByUser(Long userId);
}
