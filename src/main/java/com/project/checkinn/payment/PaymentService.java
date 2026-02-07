package com.project.checkinn.payment;

import com.project.checkinn.common.PaymentMethod;
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
}