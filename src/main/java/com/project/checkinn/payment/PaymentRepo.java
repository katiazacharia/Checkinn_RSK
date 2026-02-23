package com.project.checkinn.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface PaymentRepo extends JpaRepository<Payment, Long> , JpaSpecificationExecutor<Payment> {

    boolean existsByBooking_Id(Long bookingId);


    Optional<Payment> findByBooking_Id(Long bookingId);

}

