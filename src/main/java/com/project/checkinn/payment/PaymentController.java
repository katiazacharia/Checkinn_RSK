package com.project.checkinn.payment;

import com.project.checkinn.common.PaymentMethod;
import com.project.checkinn.common.PaymentStatus;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
    @GetMapping
    public List<PaymentResponse> search(
            @RequestParam(required = false) Long bookingId,
            @RequestParam(required = false) PaymentStatus status,
            @RequestParam(required = false) PaymentMethod method
    ) {
        return paymentService.search(bookingId, status, method)
                .stream()
                .map(PaymentMapper::toResponse)
                .toList();
    }
    @PatchMapping("/{id}/status")
    public PaymentResponse updateStatus(
            @PathVariable Long id,
            @RequestParam PaymentStatus status
    ) {
        return PaymentMapper.toResponse(paymentService.updateStatus(id, status));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public void delete(@PathVariable Long id) {
        throw new ResponseStatusException(
                HttpStatus.METHOD_NOT_ALLOWED,
                "Deleting payments is not allowed"
        );
    }

    }
