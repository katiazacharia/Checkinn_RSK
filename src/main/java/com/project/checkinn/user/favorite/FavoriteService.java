package com.project.checkinn.user.favorite;

import java.util.List;

public interface FavoriteService {
    FavoriteResponse add(Long userId, Long itemId);

    List<FavoriteResponse> getByUser(Long userId);
}
