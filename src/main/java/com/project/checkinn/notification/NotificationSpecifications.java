package com.project.checkinn.notification;

import com.project.checkinn.common.NotificationStatus;
import com.project.checkinn.common.NotificationType;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class NotificationSpecifications {
    private NotificationSpecifications() {
    }

    public static Specification<Notification> userId(Long userId) {
        return (root, q, cb) -> userId == null ? null : cb.equal(root.get("user").get("id"), userId);
    }

    public static Specification<Notification> bookingId(Long bookingId) {
        return (root, q, cb) -> bookingId == null ? null : cb.equal(root.get("booking").get("id"), bookingId);
    }

    public static Specification<Notification> status(NotificationStatus status) {
        return (root, q, cb) -> status == null ? null : cb.equal(root.get("status"), status);
    }

    public static Specification<Notification> type(NotificationType type) {
        return (root, q, cb) -> type == null ? null : cb.equal(root.get("type"), type);
    }

    public static Specification<Notification> sentFrom(LocalDateTime from) {
        return (root, q, cb) -> from == null ? null : cb.greaterThanOrEqualTo(root.get("sentAt"), from);
    }

    public static Specification<Notification> sentTo(LocalDateTime to) {
        return (root, q, cb) -> to == null ? null : cb.lessThanOrEqualTo(root.get("sentAt"), to);
    }

    public static Specification<Notification> q(String text) {
        return (root, q, cb) -> {
            if (text == null || text.isBlank()) return null;
            String like = "%" + text.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("title")), like),
                    cb.like(cb.lower(root.get("message")), like)
            );
        };
    }
}
