package com.project.checkinn.promo;

public class PromoCodeMapper {

    private PromoCodeMapper() {
    }

    public static PromoCodeResponse toResponse(PromoCode promo) {
        if (promo == null) return null;

        return new PromoCodeResponse(promo);
    }

    public static PromoCode toEntity(PromoCodeRequest request) {
        if (request == null) return null;

        PromoCode promo = new PromoCode();
        promo.setCode(request.getCode());
        promo.setDiscountValue(request.getDiscountValue());
        promo.setValidFrom(request.getValidFrom());
        promo.setValidTo(request.getValidTo());
        promo.setActive(request.isActive());

        return promo;
    }
}
