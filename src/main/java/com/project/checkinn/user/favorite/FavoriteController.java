package com.project.checkinn.user.favorite;


import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }


    @PreAuthorize("isAuthenticated()")
    @PostMapping //add favorite
    @ResponseStatus(HttpStatus.CREATED)
    public FavoriteResponse add(@Valid @RequestBody FavoriteRequest request, Authentication authentication) {
        return favoriteService.add(request,authentication);
    }

    @PreAuthorize("@authz.isFavoriteOwner(#id, authentication) or hasAnyRole('ADMIN','MANAGER')")
    @GetMapping("/{id}") //get favorite by id
    public FavoriteResponse getById(@PathVariable Long id) {
        return favoriteService.getById(id);
    }

    @PreAuthorize("@authz.isFavoriteOwner(#id, authentication) or hasAnyRole('ADMIN','MANAGER')")
    @DeleteMapping("/{id}") //delete favorite by id
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        favoriteService.deleteById(id);
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/me") //delete my favorite by itemId
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteByUserAndItem(@RequestParam Long itemId, Authentication authentication) {
        favoriteService.deleteByUserAndItem(itemId,authentication);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me/exists")
    public boolean exists(@RequestParam Long itemId, Authentication authentication) {
        return favoriteService.exists(itemId, authentication);
    }


    // GET /favorites?userId=1&page=0&size=10&sort=id,desc
    // GET /favorites?itemId=10&page=0&size=10
    // GET /favorites?userId=1&itemId=10
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me") //get my favorites with optional itemId filter
    public Page<FavoriteResponse> myFavorite(
            @RequestParam(required = false) Long itemId,
            @PageableDefault(size = 10) Pageable pageable,
            Authentication authentication
    ) {
        return favoriteService.search(itemId, pageable,authentication);
    }
}

