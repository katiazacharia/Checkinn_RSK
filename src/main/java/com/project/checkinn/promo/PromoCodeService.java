package com.project.checkinn.promo;

import java.util.List;

public interface PromoCodeService {

    PromoCode create(PromoCode promoCode);

    PromoCode getById(Long id);

    PromoCode getByCode(String code);

    List<PromoCode> getAll();

    PromoCode deactivate(Long id);

    boolean isValid(String code);

    PromoCode update(PromoCode promoCode);

}
