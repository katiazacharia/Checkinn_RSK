package com.project.checkinn.promo;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PromoCodeRequest {
    private String code;
    private BigDecimal discountValue;
    private LocalDate validFrom;
    private LocalDate validTo;
    private boolean active;

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public BigDecimal getDiscountValue() { return discountValue; }
    public void setDiscountValue(BigDecimal discountValue) { this.discountValue = discountValue; }

    public LocalDate getValidFrom() { return validFrom; }
    public void setValidFrom(LocalDate validFrom) { this.validFrom = validFrom; }

    public LocalDate getValidTo() { return validTo; }
    public void setValidTo(LocalDate validTo) { this.validTo = validTo; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
