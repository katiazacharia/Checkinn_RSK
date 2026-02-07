package com.project.checkinn.promo;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "promo_codes")
public class PromoCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String code;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal discountValue;

    @Column(nullable = false)
    private LocalDate validFrom;

    @Column(nullable = false)
    private LocalDate validTo;

    @Column(nullable = false)
    private boolean active;

    public PromoCode() {
    }

    public PromoCode(String code, BigDecimal discountValue, LocalDate validFrom, LocalDate validTo, boolean active) {
        this.code = code;
        this.discountValue = discountValue;
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.active = active;
    }

    // ===== Getters =====
    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public BigDecimal getDiscountValue() {
        return discountValue;
    }

    public LocalDate getValidFrom() {
        return validFrom;
    }

    public LocalDate getValidTo() {
        return validTo;
    }

    public boolean isActive() {
        return active;
    }

    // ===== Setters =====
    public void setId(Long id) {
        this.id = id;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setDiscountValue(BigDecimal discountValue) {
        this.discountValue = discountValue;
    }

    public void setValidFrom(LocalDate validFrom) {
        this.validFrom = validFrom;
    }

    public void setValidTo(LocalDate validTo) {
        this.validTo = validTo;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    // (اختياري) مفيد للتصحيح واللوجز
    @Override
    public String toString() {
        return "PromoCode{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", discountValue=" + discountValue +
                ", validFrom=" + validFrom +
                ", validTo=" + validTo +
                ", active=" + active +
                '}';
    }
}
