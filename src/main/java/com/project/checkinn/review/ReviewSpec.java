package com.project.checkinn.review;

import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class ReviewSpec {
    public ReviewSpec() {
    }
    public static Specification<Review> userId(Long userId) {
        return (root, query, cb) -> userId == null ? null : cb.equal(root.get("user").get("id"), userId);
    }
    public static Specification<Review> bookingId(Long bookingId) {
        return (root, query, cb) -> bookingId == null ? null : cb.equal(root.get("booking").get("id"), bookingId);
    }

    public static Specification<Review> rating(Integer rating) {
        return (root, query, cb) -> rating == null ? null : cb.equal(root.get("rating"), rating);
    }
    public static Specification<Review> ratingBetween(Integer min, Integer max) {
        return (root, query, cb) -> {
            if (min == null && max == null) return null;
            if (min != null && max != null) return cb.between(root.get("rating"), min, max);
            return min != null ? cb.greaterThanOrEqualTo(root.get("rating"), min)
                    : cb.lessThanOrEqualTo(root.get("rating"), max);
        };
    }
    public static Specification<Review> createdFrom(LocalDateTime from) {
        return (root, query, cb) -> from == null ? null : cb.greaterThanOrEqualTo(root.get("createdAt"), from);
    }

    public static Specification<Review> createdTo(LocalDateTime to) {
        return (root, query, cb) -> to == null ? null : cb.lessThanOrEqualTo(root.get("createdAt"), to);
    }

    public static Specification<Review> hasComment(Boolean hasComment) {
        return (root, query, cb) -> {
            if (hasComment == null) return null;
            if (hasComment) return cb.and(
                    cb.isNotNull(root.get("comment")),
                    cb.greaterThan(cb.length(cb.trim(root.get("comment"))), 0)
            );
            return cb.or(
                    cb.isNull(root.get("comment")),
                    cb.equal(cb.length(cb.trim(root.get("comment"))), 0)
            );
        };
    }
}
