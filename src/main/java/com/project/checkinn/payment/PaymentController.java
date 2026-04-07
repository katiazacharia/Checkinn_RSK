package com.project.checkinn.payment;

import com.project.checkinn.common.PaymentMethod;
import com.project.checkinn.common.PaymentStatus;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@RestController
@RequestMapping("/payments")
public class PaymentController {



        private final PaymentService paymentService;

        public PaymentController(PaymentService paymentService) {
            this.paymentService = paymentService;
        }

        @PreAuthorize("(hasRole('CUSTOMER') and @authz.isBookingOwner(#request.bookingId, authentication)) or hasAnyRole('ADMIN','MANAGER')")
        @PostMapping
        @ResponseStatus(HttpStatus.CREATED)
        public PaymentResponse create(@Valid @RequestBody PaymentRequest request) {
            PaymentMethod method;
            try {
                method = PaymentMethod.valueOf(request.getMethod().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid payment method");
            }

            return paymentService.create(
                    request.getBookingId(),
                   method,
                    request.getPointsToRedeem()
            );        }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me")
    public Page<PaymentResponse> myPayments(
            @RequestParam(required = false) Long bookingId,
            @RequestParam(required = false) PaymentStatus status,
            @RequestParam(required = false) PaymentMethod method,
            @PageableDefault(size = 10) Pageable pageable,
            Authentication authentication
    ) {
        return paymentService.searchMy(bookingId, status, method, pageable, authentication)
                .map(PaymentMapper::toResponse);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me/{id}")
    public PaymentResponse myPayemntById(@PathVariable Long id, Authentication authentication) {
        return PaymentMapper.toResponse(paymentService.getMyPaymentById(id, authentication));   }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @GetMapping("/{id}")
    public PaymentResponse getById(@PathVariable Long id) {
        return PaymentMapper.toResponse(paymentService.getById(id));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me/bookings/{bookingId}")
    public PaymentResponse getByBookingId(@PathVariable Long bookingId, Authentication authentication) {
        return PaymentMapper.toResponse(paymentService.getMyPaymentByBookingId(bookingId, authentication));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
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

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
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

    @PreAuthorize("@authz.isBookingOwner(#bookingId, authentication) or hasAnyRole('ADMIN','MANAGER')")
    @PostMapping("/{bookingId}/refund")
    public PaymentResponse refund(@PathVariable Long bookingId) {

        Payment payment = paymentService.refund(bookingId);

        return PaymentMapper.toResponse(payment);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/me/{bookingId}/refund")
    public PaymentResponse refundMy(@PathVariable Long bookingId, Authentication authentication) {
        return PaymentMapper.toResponse(paymentService.refundMy(bookingId, authentication));
    }
    }
