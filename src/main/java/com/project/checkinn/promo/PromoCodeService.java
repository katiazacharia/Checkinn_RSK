package com.project.checkinn.promo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PromoCodeService {

    PromoCode create(PromoCode promoCode);

    PromoCode getById(Long id);

    PromoCode getByCode(String code);

    List<PromoCode> getAll();

    PromoCode deactivate(Long id);

    boolean isValid(String code);

    PromoCode update(PromoCode promoCode);

    void delete(Long id);

    List<PromoCode> getActive();

    Page<PromoCode> list(Boolean active, String code, Pageable pageable);

}
