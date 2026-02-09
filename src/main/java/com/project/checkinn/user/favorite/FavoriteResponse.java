package com.project.checkinn.user.favorite;

public class FavoriteResponse {
    private Long id;
    private Long userId;
    private Long itemId;

    public FavoriteResponse(Favorite favorite) {
        this.id = favorite.getId();
        this.userId = favorite.getUser().getId();
        this.itemId = favorite.getItemId();
    }
    public Long getId() {
        return id;
    }
    public Long getUserId() {
        return userId;
    }
    public Long getItemId() {
        return itemId;
    }
}
