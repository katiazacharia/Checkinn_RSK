package com.project.checkinn.user.favorite;

import jakarta.validation.constraints.NotNull;

public class FavoriteRequest {
    @NotNull
    private Long userId;

    @NotNull
    private Long itemId;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getItemId() { return itemId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }
}
