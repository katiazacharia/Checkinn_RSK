package com.project.checkinn.promo;

import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PromoCodeSpecs {
    public static Specification<PromoCode> active(Boolean active) {
        if (active == null) return null;
        return (root, q, cb) -> cb.equal(root.get("active"), active);
    }

    public static Specification<PromoCode> codeContains(String code) {
        if (code == null || code.isBlank()) return null;
        return (root, q, cb) -> cb.like(cb.lower(root.get("code")), "%" + code.toLowerCase() + "%");
    }


    public static Specification<PromoCode> validFromFrom(LocalDate from) {
        if (from == null) return null;
        return (root, q, cb) -> cb.greaterThanOrEqualTo(root.get("validFrom"), from);
    }

    public static Specification<PromoCode> validFromTo(LocalDate to) {
        if (to == null) return null;
        return (root, q, cb) -> cb.lessThanOrEqualTo(root.get("validFrom"), to);
    }

    public static Specification<PromoCode> validToFrom(LocalDate from) {
        if (from == null) return null;
        return (root, q, cb) -> cb.greaterThanOrEqualTo(root.get("validTo"), from);
    }

    public static Specification<PromoCode> validToTo(LocalDate to) {
        if (to == null) return null;
        return (root, q, cb) -> cb.lessThanOrEqualTo(root.get("validTo"), to);
    }

    public static Specification<PromoCode> discountFrom(BigDecimal min) {
        if (min == null) return null;
        return (root, q, cb) -> cb.greaterThanOrEqualTo(root.get("discountValue"), min);
    }

    public static Specification<PromoCode> discountTo(BigDecimal max) {
        if (max == null) return null;
        return (root, q, cb) -> cb.lessThanOrEqualTo(root.get("discountValue"), max);
    }
}
