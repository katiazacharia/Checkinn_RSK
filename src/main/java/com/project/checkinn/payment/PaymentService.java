package com.project.checkinn.payment;

import com.project.checkinn.common.PaymentMethod;
import com.project.checkinn.common.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PaymentService {

    Payment create(Long bookingId, PaymentMethod method
    );

    Payment getById(Long id);

    Payment getByBookingId(Long bookingId);

    Page<Payment> search(
            Long bookingId,
            PaymentStatus status,
            PaymentMethod method,
            Pageable pageable
    );
    Payment updateStatus(Long id, PaymentStatus status);
    Payment refund(Long bookingId);
}