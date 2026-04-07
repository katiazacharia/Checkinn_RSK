package com.project.checkinn.promo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PromoCodeServiceImplTest {

    @Mock PromoCodeRepository promoCodeRepository;
    @InjectMocks PromoCodeServiceImpl service;

    @Test
    void create_shouldSave_whenUniqueCode() {
        PromoCode promo = new PromoCode();
        promo.setCode("WELCOME10");
        when(promoCodeRepository.existsByCodeIgnoreCase("WELCOME10")).thenReturn(false);
        when(promoCodeRepository.save(promo)).thenReturn(promo);

        PromoCode saved = service.create(promo);

        assertEquals("WELCOME10", saved.getCode());
    }

    @Test
    void create_shouldThrowConflict_whenDuplicateCode() {
        PromoCode promo = new PromoCode();
        promo.setCode("WELCOME10");
        when(promoCodeRepository.existsByCodeIgnoreCase("WELCOME10")).thenReturn(true);

        assertThrows(ResponseStatusException.class, () -> service.create(promo));
    }

    @Test
    void isValid_shouldReturnTrue_whenActiveAndWithinDateRange() {
        PromoCode promo = new PromoCode();
        promo.setCode("VIP25");
        promo.setActive(true);
        promo.setValidFrom(LocalDate.now().minusDays(1));
        promo.setValidTo(LocalDate.now().plusDays(1));
        when(promoCodeRepository.findByCodeIgnoreCase("VIP25")).thenReturn(Optional.of(promo));

        assertTrue(service.isValid("VIP25"));
    }

    @Test
    void deactivate_shouldUpdateFlag() {
        PromoCode promo = new PromoCode();
        promo.setActive(true);
        when(promoCodeRepository.findById(1L)).thenReturn(Optional.of(promo));
        when(promoCodeRepository.save(any(PromoCode.class))).thenAnswer(inv -> inv.getArgument(0));

        PromoCode result = service.deactivate(1L);
        assertFalse(result.isActive());
    }
}
