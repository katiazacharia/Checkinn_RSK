package com.project.checkinn.user.favorite;

import jakarta.validation.constraints.NotNull;

public class FavoriteRequest {

    @NotNull
    private Long itemId;


    public Long getItemId() { return itemId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }
}
