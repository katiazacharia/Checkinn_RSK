package com.project.checkinn.payment;

import com.project.checkinn.common.PaymentMethod;
import com.project.checkinn.common.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

public interface PaymentService {

    PaymentResponse create(Long bookingId, PaymentMethod method, Integer pointsToRedeem);

    Payment getById(Long id );




    Payment getByBookingId(Long bookingId);

    Page<Payment> search(
            Long bookingId,
            PaymentStatus status,
            PaymentMethod method,
            Pageable pageable,
            Authentication authentication

    );

    Payment getMyPaymentById(Long id, Authentication authentication);

    Payment getMyPaymentByBookingId(Long bookingId, Authentication authentication);

    Page<Payment> searchMy(
            Long bookingId,
            PaymentStatus status,
            PaymentMethod method,
            Pageable pageable,
            Authentication authentication
    );
    Payment updateStatus(Long id, PaymentStatus status);
    Payment refund(Long bookingId);
    Payment refundMy(Long bookingId, Authentication authentication);


}