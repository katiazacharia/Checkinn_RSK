package com.project.checkinn.payment;

import com.project.checkinn.common.PaymentMethod;
import com.project.checkinn.common.PaymentStatus;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<PaymentResponse> create(
            @Valid @RequestBody PaymentRequest request,
            UriComponentsBuilder uriBuilder
    ) {
        Payment created = paymentService.create(
                request.getBookingId(),
                request.getAmount(),
                request.getMethod()
        );

        URI location = uriBuilder.path("/payments/{id}")
                .buildAndExpand(created.getId())
                .toUri();

        return ResponseEntity.created(location).body(PaymentMapper.toResponse(created));
    }

    @GetMapping("/{id}")
    public PaymentResponse getById(@PathVariable Long id) {
        return PaymentMapper.toResponse(paymentService.getById(id));
    }

    @GetMapping("/by-booking/{bookingId}")
    public PaymentResponse getByBooking(@PathVariable Long bookingId) {
        return PaymentMapper.toResponse(paymentService.getByBookingId(bookingId));
    }


    @GetMapping
    public Page<PaymentResponse> search(
            @RequestParam(required = false) Long bookingId,
            @RequestParam(required = false) PaymentStatus status,
            @RequestParam(required = false) PaymentMethod method,


            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime after,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime before,

            @PageableDefault(size = 20) Pageable pageable
    ) {
        if (after != null && before != null && after.isAfter(before)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "after must be <= before");
        }

        return paymentService.search(bookingId, status, method, after, before, pageable)
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
        throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, "Deleting payments is not allowed");
    }
}
