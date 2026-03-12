package com.project.checkinn.payment;

import com.project.checkinn.common.PaymentMethod;
import com.project.checkinn.common.PaymentStatus;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@RestController
@RequestMapping("/payments")
public class PaymentController {



        private final PaymentService paymentService;

        public PaymentController(PaymentService paymentService) {
            this.paymentService = paymentService;
        }


        @PostMapping
        @ResponseStatus(HttpStatus.CREATED)
        public PaymentResponse create(@Valid @RequestBody PaymentRequest request) {

            Payment payment = paymentService.create(
                    request.getBookingId(),
                    request.getMethod()
            );

            return PaymentMapper.toResponse(payment);
        }

        @GetMapping("/{id}")
        public PaymentResponse getById(@PathVariable Long id) {
            return PaymentMapper.toResponse(paymentService.getById(id));
        }

    @GetMapping("/booking/{bookingId}")
    public PaymentResponse getByBookingId(@PathVariable Long bookingId) {
        return PaymentMapper.toResponse(paymentService.getByBookingId(bookingId));
    }

    @GetMapping
    public Page<PaymentResponse> search(
            @RequestParam(required = false) Long bookingId,
            @RequestParam(required = false) PaymentStatus status,
            @RequestParam(required = false) PaymentMethod method,
            @PageableDefault(size = 10) Pageable pageable

    ) {
        return paymentService.search(bookingId, status, method,pageable)
                .map(PaymentMapper::toResponse);
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
