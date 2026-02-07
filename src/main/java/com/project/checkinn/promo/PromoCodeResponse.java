package com.project.checkinn.promo;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PromoCodeResponse {
    private Long id;
    private String code;
    private BigDecimal discountValue;
    private LocalDate validFrom;
    private LocalDate validTo;
    private boolean active;

    public PromoCodeResponse(PromoCode promo) {
        this.id = promo.getId();
        this.code = promo.getCode();
        this.discountValue = promo.getDiscountValue();
        this.validFrom = promo.getValidFrom();
        this.validTo = promo.getValidTo();
        this.active = promo.isActive();
    }

    public Long getId() { return id; }
    public String getCode() { return code; }
    public BigDecimal getDiscountValue() { return discountValue; }
    public LocalDate getValidFrom() { return validFrom; }
    public LocalDate getValidTo() { return validTo; }
    public boolean isActive() { return active; }
}
