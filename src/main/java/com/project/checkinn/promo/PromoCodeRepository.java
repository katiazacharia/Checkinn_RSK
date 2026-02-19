package com.project.checkinn.promo;

import org.springframework.boot.data.autoconfigure.web.DataWebProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PromoCodeRepository extends JpaRepository<PromoCode, Long> {
    Optional<PromoCode> findByCodeIgnoreCase(String code);
    boolean existsByCodeIgnoreCase(String code);
    Page<PromoCode> findAll(Pageable pageable);
    Page<PromoCode> findByActive(boolean active, Pageable pageable);
    Page<PromoCode> findByCodeContainingIgnoreCase(String code, Pageable pageable);
    Page<PromoCode> findByActiveAndCodeContainingIgnoreCase(boolean active, String code, Pageable pageable);


}
