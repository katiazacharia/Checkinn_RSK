package com.project.checkinn.promo;


import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/promoCodes")
public class PromoCodeController {

     private final PromoCodeService promoCodeService;


    public PromoCodeController(PromoCodeService promoCodeService) {
        this.promoCodeService = promoCodeService;
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PostMapping
    public ResponseEntity<PromoCodeResponse> create(
            @Valid @RequestBody PromoCodeRequest request,
            UriComponentsBuilder uriBuilder) {

        PromoCode entity = PromoCodeMapper.toEntity(request);
        PromoCode created = promoCodeService.create(entity);

        URI location = uriBuilder.path("/promoCodes/{id}")
                .buildAndExpand(created.getId())
                .toUri();

        return ResponseEntity.created(location)
                .body(PromoCodeMapper.toResponse(created));
    }


    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @GetMapping
    public Page<PromoCodeResponse> getAll(
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) String code,

            @RequestParam(required = false) BigDecimal minDiscount,
            @RequestParam(required = false) BigDecimal maxDiscount,

            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate validFromStart,

            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate validFromEnd,

            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate validToStart,

            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate validToEnd,

            @PageableDefault(page = 0, size = 10) Pageable pageable
    ) {
        return promoCodeService
                .list(active, code, minDiscount, maxDiscount,
                        validFromStart, validFromEnd, validToStart, validToEnd, pageable)
                .map(PromoCodeMapper::toResponse);
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @GetMapping("/{id}")
    public PromoCodeResponse getById(@PathVariable Long id) {
        if (id == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "id is required");

        return PromoCodeMapper.toResponse(promoCodeService.getById(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @GetMapping("/code/{code}")
    public PromoCodeResponse getByCode(@PathVariable String code) {
        if (code == null || code.isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "code is required");

        return PromoCodeMapper.toResponse(promoCodeService.getByCode(code));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PatchMapping("/{id}/deactivate")
    public PromoCodeResponse deactivate(@PathVariable Long id) {
        if (id == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "id is required");

        return PromoCodeMapper.toResponse(promoCodeService.deactivate(id));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/validate/{code}")
    public boolean validate(@PathVariable String code) {
        if (code == null || code.isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "code is required");

       return promoCodeService.isValid(code);

    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PutMapping("/{id}")
    public PromoCodeResponse update(@PathVariable Long id, @Valid @RequestBody PromoCodeRequest request) {
        if (id == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "id is required");
        if (request == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "request is required");

        PromoCode existing = promoCodeService.getById(id);

        existing.setCode(request.getCode());
        existing.setDiscountValue(request.getDiscountValue());
        existing.setValidFrom(request.getValidFrom());
        existing.setValidTo(request.getValidTo());
        existing.setActive(request.isActive());


        PromoCode saved = promoCodeService.update(existing);
        return PromoCodeMapper.toResponse(saved);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/active")
    public List<PromoCodeResponse> getActive() {
        return promoCodeService.getActive()
                .stream()
                .map(PromoCodeMapper::toResponse)
                .toList();
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {

        if (id == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "id is required");

        promoCodeService.delete(id);
    }


}
