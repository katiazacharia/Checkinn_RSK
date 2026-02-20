package com.project.checkinn.user.favorite;

import org.springframework.data.jpa.domain.Specification;

public class FavoriteSpec {
    private FavoriteSpec() {}

    public static Specification<Favorite> userId(Long userId) {
        return (root, query, cb) -> userId == null ? cb.conjunction() : cb.equal(root.get("user").get("id"), userId);
    }

    public static Specification<Favorite> itemId(Long itemId) {
        return (root, query, cb) -> itemId == null ? cb.conjunction() : cb.equal(root.get("itemId"), itemId);
    }
}
