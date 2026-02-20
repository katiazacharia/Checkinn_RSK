package com.project.checkinn.user.favorite;


import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }
    @PostMapping //add favorite
    @ResponseStatus(HttpStatus.CREATED)
    public FavoriteResponse add(@Valid @RequestBody FavoriteRequest request) {
        return favoriteService.add(request);
    }

    @GetMapping("/{id}") //get favorite by id
    public FavoriteResponse getById(@PathVariable Long id) {
        return favoriteService.getById(id);
    }

    @DeleteMapping("/{id}") //delete favorite by id
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        favoriteService.deleteById(id);
    }

    @DeleteMapping //delete favorite by userId and itemId
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteByUserAndItem(@RequestParam Long userId, @RequestParam Long itemId) {
        favoriteService.deleteByUserAndItem(userId, itemId);
    }

    @GetMapping("/exists") //check if favorite exists by userId and itemId
    public boolean exists(@RequestParam Long userId, @RequestParam Long itemId) {
        return favoriteService.exists(userId, itemId);
    }


    // GET /favorites?userId=1&page=0&size=10&sort=id,desc
    // GET /favorites?itemId=10&page=0&size=10
    // GET /favorites?userId=1&itemId=10
    @GetMapping("/search") //search favorites by userId and/or itemId with pagination
    public Page<FavoriteResponse> search(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long itemId,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return favoriteService.search(userId, itemId, pageable);
    }
}

