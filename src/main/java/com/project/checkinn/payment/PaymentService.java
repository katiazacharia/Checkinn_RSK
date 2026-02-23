package com.project.checkinn.payment;

import com.project.checkinn.common.PaymentMethod;
import com.project.checkinn.common.PaymentStatus;

import java.math.BigDecimal;
import java.util.List;

public interface PaymentService {

    Payment create(
            Long bookingId,
            BigDecimal amount,
            PaymentMethod method
    );

    Payment getById(Long id);

    List<Payment> getAll();

    Payment getByBookingId(Long bookingId);

    List<Payment> search(Long bookingId, PaymentStatus status, PaymentMethod method);

    Payment updateStatus(Long id, PaymentStatus status);
}