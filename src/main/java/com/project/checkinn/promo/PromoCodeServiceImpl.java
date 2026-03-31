package com.project.checkinn.promo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class PromoCodeServiceImpl implements PromoCodeService {

    private final PromoCodeRepository promoCodeRepository;

    public PromoCodeServiceImpl(PromoCodeRepository promoCodeRepository) {
        this.promoCodeRepository = promoCodeRepository;
    }

    @Override
    public PromoCode create(PromoCode promoCode) {
        if (promoCode.getCode() == null || promoCode.getCode().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Promo code is required");
        }

        if (promoCodeRepository.existsByCodeIgnoreCase(promoCode.getCode())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Promo code already exists");
        }

        return promoCodeRepository.save(promoCode);
    }

    @Override
    public PromoCode getById(Long id) {
        return promoCodeRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Promo code not found"));
    }

    @Override
    public PromoCode getByCode(String code) {
        return promoCodeRepository.findByCodeIgnoreCase(code)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Promo code not found"));
    }

    @Override
    public List<PromoCode> getAll() {
        return promoCodeRepository.findAll();
    }

    @Override
    public PromoCode deactivate(Long id) {
        PromoCode promo = getById(id);
        promo.setActive(false);
        return promoCodeRepository.save(promo);
    }

    @Override
    public boolean isValid(String code) {
        PromoCode promo = getByCode(code);
        LocalDate today = LocalDate.now();

        return promo.isActive()
                && !today.isBefore(promo.getValidFrom())
                && !today.isAfter(promo.getValidTo());
    }

    @Override
    public PromoCode update(PromoCode promoCode) {

        if (promoCode.getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Promo id is required");
        }

        PromoCode existing = getById(promoCode.getId());

        if (!existing.getCode().equalsIgnoreCase(promoCode.getCode())
                && promoCodeRepository.existsByCodeIgnoreCase(promoCode.getCode())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Promo code already exists");
        }

        existing.setCode(promoCode.getCode());
        existing.setDiscountValue(promoCode.getDiscountValue());
        existing.setValidFrom(promoCode.getValidFrom());
        existing.setValidTo(promoCode.getValidTo());
        existing.setActive(promoCode.isActive());

        return promoCodeRepository.save(existing);
    }

    @Override
    public void delete(Long id) {

        if (id == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "id is required");

        PromoCode promo = getById(id);
        promoCodeRepository.delete(promo);
    }

    @Override
    public List<PromoCode> getActive() {
        return promoCodeRepository.findAll()
                .stream()
                .filter(PromoCode::isActive)
                .toList();
    }

    @Override
    public Page<PromoCode> list(
            Boolean active,
            String code,
            BigDecimal minDiscount,
            BigDecimal maxDiscount,
            LocalDate validFromStart,
            LocalDate validFromEnd,
            LocalDate validToStart,
            LocalDate validToEnd,
            Pageable pageable
    ) {
        Specification<PromoCode> spec = (root, q, cb) -> cb.conjunction();
        Specification<PromoCode> s;

        s = PromoCodeSpecs.active(active);
        if (s != null) spec = spec.and(s);

        s = PromoCodeSpecs.codeContains(code);
        if (s != null) spec = spec.and(s);

        s = PromoCodeSpecs.discountFrom(minDiscount);
        if (s != null) spec = spec.and(s);

        s = PromoCodeSpecs.discountTo(maxDiscount);
        if (s != null) spec = spec.and(s);

        s = PromoCodeSpecs.validFromFrom(validFromStart);
        if (s != null) spec = spec.and(s);

        s = PromoCodeSpecs.validFromTo(validFromEnd);
        if (s != null) spec = spec.and(s);

        s = PromoCodeSpecs.validToFrom(validToStart);
        if (s != null) spec = spec.and(s);

        s = PromoCodeSpecs.validToTo(validToEnd);
        if (s != null) spec = spec.and(s);

        return promoCodeRepository.findAll(spec, pageable);
    }




}
