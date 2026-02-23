package com.project.checkinn.payment;

import com.project.checkinn.common.PaymentMethod;
import com.project.checkinn.common.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface PaymentService {

    Payment create(
            Long bookingId,
            BigDecimal amount,
            PaymentMethod method
    );

    Payment getById(Long id);



    Page<Payment> search(
            Long bookingId,
            PaymentStatus status,
            PaymentMethod method,
            Pageable pageable
    );
    Payment updateStatus(Long id, PaymentStatus status);
}