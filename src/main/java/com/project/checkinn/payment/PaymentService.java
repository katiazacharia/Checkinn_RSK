package com.project.checkinn.payment;

import com.project.checkinn.common.PaymentMethod;
import com.project.checkinn.common.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface PaymentService {

    Payment create(Long bookingId, java.math.BigDecimal amount, PaymentMethod method);

    Payment getById(Long id);

    Payment getByBookingId(Long bookingId);

    Page<Payment> search(
            Long bookingId,
            PaymentStatus status,
            PaymentMethod method,
            LocalDateTime after,
            LocalDateTime before,
            Pageable pageable
    );

    Payment updateStatus(Long id, PaymentStatus status);
}