package com.project.checkinn.payment;

import com.project.checkinn.common.PaymentMethod;
import com.project.checkinn.common.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepo extends JpaRepository<Payment, Long> {

    boolean existsByBooking_Id(Long bookingId);


    Optional<Payment> findByBooking_Id(Long bookingId);

    List<Payment>findByStatus(PaymentStatus status);
    List<Payment>findByMethod(PaymentMethod method);
    List<Payment> findByBooking_IdAndStatus(Long bookingId, PaymentStatus status);
}

