package com.project.checkinn.user.favorite;

import com.project.checkinn.user.profile.User;
import com.project.checkinn.user.profile.UserRepo;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
    public FavoriteResponse add(FavoriteRequest request) {

        if (request == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "request is required");

        if (request.getUserId() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId is required");

        if (request.getItemId() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "itemId is required");

        if (favoriteRepo.existsByUser_IdAndItemId(request.getUserId(), request.getItemId()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Favorite already exists");

        User user = userRepo.findById(request.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Favorite favorite = new Favorite(user, request.getItemId());
        return new FavoriteResponse(favoriteRepo.save(favorite));
    }
    @Override
    public void deleteById(Long id) {
        if (id == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "id is required");

        if (!favoriteRepo.existsById(id))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Favorite not found");

        favoriteRepo.deleteById(id);
    }
    @Override
    @Transactional
    public void deleteByUserAndItem(Long userId, Long itemId) {
        if (userId == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId is required");
        if (itemId == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "itemId is required");

        long deleted = favoriteRepo.deleteByUser_IdAndItemId(userId, itemId);
        if (deleted == 0)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Favorite not found");
    }
    @Override
    public FavoriteResponse getById(Long id) {
        Favorite fav = favoriteRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Favorite not found"));
        return new FavoriteResponse(fav);
    }
    @Override
    public boolean exists(Long userId, Long itemId) {
        if (userId == null || itemId == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId and itemId are required");
        return favoriteRepo.existsByUser_IdAndItemId(userId, itemId);
    }
    @Override
    public Page<FavoriteResponse> search(Long userId, Long itemId, Pageable pageable) {
        Specification<Favorite> spec = Specification
                .where(FavoriteSpec.userId(userId))
                .and(FavoriteSpec.itemId(itemId));

        return favoriteRepo.findAll(spec, pageable).map(FavoriteResponse::new);
    }

    @Override
    public List<FavoriteResponse> getByUser(Long userId) {
        return List.of();
    }
}


