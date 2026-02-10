package com.project.checkinn.user.favorite;


import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }
    @PostMapping
    public FavoriteResponse add(
            @RequestParam Long userId,
            @RequestParam Long itemId
    ) {
        return favoriteService.add(userId, itemId);
    }
    @GetMapping("/user/{userId}")
    public List<FavoriteResponse> getByUser(@PathVariable Long userId) {
        return favoriteService.getByUser(userId);
    }
}
