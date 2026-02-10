package com.project.checkinn.payment;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payments")
public class PaymentController {



        private final PaymentService paymentService;

        public PaymentController(PaymentService paymentService) {
            this.paymentService = paymentService;
        }


        @PostMapping
        @ResponseStatus(HttpStatus.CREATED)
        public PaymentResponse create(@RequestBody PaymentRequest request) {

            Payment payment = paymentService.create(
                    request.getBookingId(),
                    request.getAmount(),
                    request.getMethod()
            );

            return PaymentMapper.toResponse(payment);
        }

        @GetMapping("/{id}")
        public PaymentResponse getById(@PathVariable Long id) {
            return PaymentMapper.toResponse(paymentService.getById(id));
        }


        @GetMapping
        public List<PaymentResponse> getAll() {
            return paymentService.getAll()
                    .stream()
                    .map(PaymentMapper::toResponse)
                    .toList();
        }


    }
