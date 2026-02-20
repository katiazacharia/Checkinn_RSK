package com.project.checkinn.loyalty.transaction;

import com.project.checkinn.common.LoyaltyTransactionType;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class LoyaltyTransactionSpecs {

    public static Specification<LoyaltyTransaction> byUserId(Long userId) {
        return (root, query, cb) -> cb.equal(root.get("user").get("id"), userId);
    }
    public static Specification<LoyaltyTransaction> type(LoyaltyTransactionType type) {
        if (type == null) return null;
        return (root, query, cb) -> cb.equal(root.get("type"), type);
    }
    public static Specification<LoyaltyTransaction> createdFrom(LocalDateTime from) {
        if (from == null) return null;
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("createdAt"), from);
    }
    public static Specification<LoyaltyTransaction> createdTo(LocalDateTime to) {
        if (to == null) return null;
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("createdAt"), to);
    }
    public static Specification<LoyaltyTransaction> noteContains(String q) {
        if (q == null || q.isBlank()) return null;
        return (root, query, cb) -> cb.like(cb.lower(root.get("note")), "%" + q.toLowerCase() + "%");
    }
    public static Specification<LoyaltyTransaction> pointsBetween(Integer min, Integer max) {
        if (min == null && max == null) return null;
        return (root, query, cb) -> {
            if (min != null && max != null) return cb.between(root.get("points"), min, max);
            if (min != null) return cb.greaterThanOrEqualTo(root.get("points"), min);
            return cb.lessThanOrEqualTo(root.get("points"), max);
        };
    }
}
