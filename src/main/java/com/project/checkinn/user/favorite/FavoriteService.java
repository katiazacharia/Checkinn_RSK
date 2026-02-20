package com.project.checkinn.user.favorite;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FavoriteService {
    FavoriteResponse add(FavoriteRequest request);

    void deleteById(Long id);

    void deleteByUserAndItem(Long userId, Long itemId);

    FavoriteResponse getById(Long id);

    boolean exists(Long userId, Long itemId);

    Page<FavoriteResponse> search(Long userId, Long itemId, Pageable pageable);
    List<FavoriteResponse> getByUser(Long userId);
}
