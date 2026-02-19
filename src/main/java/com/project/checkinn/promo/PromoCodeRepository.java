package com.project.checkinn.promo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


import java.util.Optional;

public interface PromoCodeRepository extends JpaRepository<PromoCode, Long> ,
        JpaSpecificationExecutor<PromoCode> {
    Optional<PromoCode> findByCodeIgnoreCase(String code);
    boolean existsByCodeIgnoreCase(String code);


}
