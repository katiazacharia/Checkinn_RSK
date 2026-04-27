package com.project.checkinn.payment;

import com.project.checkinn.common.PaymentMethod;
import com.project.checkinn.common.PaymentStatus;
import org.springframework.data.jpa.domain.Specification;

public class PaymentSpecification {


    private PaymentSpecification() {
    }

    public static Specification<Payment> hasBookingId(Long bookingId) {
        return (root, query, cb) ->
                bookingId == null ? null : cb.equal(root.get("booking").get("id"), bookingId);
    }

    public static Specification<Payment> hasStatus(PaymentStatus status) {
        return (root, query, cb) ->
                status == null ? null : cb.equal(root.get("status"), status);
    }

    public static Specification<Payment> hasMethod(PaymentMethod method) {
        return (root, query, cb) ->
                method == null ? null : cb.equal(root.get("method"), method);
    }

    public static Specification<Payment> hasUserId(Long userId) {
        return (root, query, cb) ->
                userId == null ? null : cb.equal(root.get("booking").get("user").get("id"), userId);
    }

    public static Specification<Payment> hasPaymentId(Long id) {
        return (root, query, cb) ->
                id == null ? null : cb.equal(root.get("id"), id);
    }

    public static Specification<Payment> hasManagerId(Long managerId) {
        return (root, query, cb) -> {
            if (managerId == null) return null;

            return cb.equal(
                    root.join("booking")
                            .join("room")
                            .join("hotel")
                            .join("manager")
                            .get("id"),
                    managerId
            );
        };
    }
}
