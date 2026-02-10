package com.project.checkinn.user.favorite;

import com.project.checkinn.user.profile.User;
import com.project.checkinn.user.profile.UserRepo;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
@Service
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteRepo favoriteRepo;
    private final UserRepo userRepo;

    public FavoriteServiceImpl(FavoriteRepo favoriteRepo, UserRepo userRepo) {
        this.favoriteRepo = favoriteRepo;
        this.userRepo = userRepo;
    }

    @Override
    public FavoriteResponse add(Long userId, Long itemId) {

        if (userId == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId is required");

        if (itemId == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "itemId is required");

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setItemId(itemId);

        return new FavoriteResponse(favoriteRepo.save(favorite));
}

    @Override
    public List<FavoriteResponse> getByUser(Long userId) {

        if (userId == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId is required");

        return favoriteRepo.findByUser_Id(userId)
                .stream()
                .map(FavoriteResponse::new)
                .toList();
    }
    }


