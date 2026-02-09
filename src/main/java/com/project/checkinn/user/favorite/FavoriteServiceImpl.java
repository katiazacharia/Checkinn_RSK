package com.project.checkinn.user.favorite;

import com.project.checkinn.user.profile.User;
import com.project.checkinn.user.profile.UserRepo;

import java.util.List;

public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteRepo favoriteRepo;
    private final UserRepo userRepo;

    public FavoriteServiceImpl(FavoriteRepo favoriteRepo, UserRepo userRepo) {
        this.favoriteRepo = favoriteRepo;
        this.userRepo = userRepo;
    }

    @Override
    public FavoriteResponse add(Long userId, Long itemId) {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setItemId(itemId);

        return new FavoriteResponse(favoriteRepo.save(favorite));
}

    @Override
    public List<FavoriteResponse> getByUser(Long userId) {
        return favoriteRepo.findByUserId(userId)
                .stream()
                .map(FavoriteResponse::new)
                .toList();
    }
    }


