package com.project.checkinn.user.favorite;

import com.project.checkinn.security.CurrentUserService;
import com.project.checkinn.user.profile.User;
import com.project.checkinn.user.profile.UserRepo;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
@Service
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteRepo favoriteRepo;
    private final UserRepo userRepo;
    private final CurrentUserService currentUserService;

    public FavoriteServiceImpl(FavoriteRepo favoriteRepo, UserRepo userRepo, CurrentUserService currentUserService) {
        this.favoriteRepo = favoriteRepo;
        this.userRepo = userRepo;
        this.currentUserService = currentUserService;
    }

    @Override
    public FavoriteResponse add(FavoriteRequest request, Authentication authentication) {

        Long currentUserId = currentUserService.getCurrentUserId(authentication);

        if (request == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "request is required");

        if (request.getItemId() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "itemId is required");

        if (favoriteRepo.existsByUser_IdAndItemId(currentUserId, request.getItemId()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Favorite already exists");


        User user = userRepo.findById(currentUserId)
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
    public void deleteByUserAndItem(Long itemId, Authentication authentication) {
        if (itemId == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "itemId is required");
        Long currentUserId = currentUserService.getCurrentUserId(authentication);

        long deleted = favoriteRepo.deleteByUser_IdAndItemId(currentUserId, itemId);
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
    public boolean exists(Long itemId,Authentication authentication) {
        if (itemId == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "itemId is required");

        Long currentUserId = currentUserService.getCurrentUserId(authentication);
        return favoriteRepo.existsByUser_IdAndItemId(currentUserId, itemId);
    }

    @Override
    public Page<FavoriteResponse> search(Long itemId, Pageable pageable, Authentication authentication) {

        Long currentUserId = currentUserService.getCurrentUserId(authentication);

        Specification<Favorite> spec = Specification
                .where(FavoriteSpec.userId(currentUserId))
                .and(FavoriteSpec.itemId(itemId));

        return favoriteRepo.findAll(spec, pageable)
                .map(FavoriteResponse::new);
    }

    @Override
    public List<FavoriteResponse> getByUser(Long userId) {
        return favoriteRepo.findByUser_Id(userId)
                .stream()
                .map(FavoriteResponse::new)
                .toList();
    }
}


