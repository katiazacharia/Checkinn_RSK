package com.project.checkinn.promo;


import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/promoCodes")
public class PromoCodeController {

     private final PromoCodeService promoCodeService;

    public PromoCodeController(PromoCodeService promoCodeService) {
        this.promoCodeService = promoCodeService;
    }

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

    @GetMapping
    public List<PromoCodeResponse> getAll() {
        return promoCodeService.getAll()
                .stream()
                .map(PromoCodeMapper::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public PromoCodeResponse getById(@PathVariable Long id) {
        if (id == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "id is required");

        return PromoCodeMapper.toResponse(promoCodeService.getById(id));
    }

    @GetMapping("/code/{code}")
    public PromoCodeResponse getByCode(@PathVariable String code) {
        if (code == null || code.isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "code is required");

        return PromoCodeMapper.toResponse(promoCodeService.getByCode(code));
    }

    @PatchMapping("/{id}/deactivate")
    public PromoCodeResponse deactivate(@PathVariable Long id) {
        if (id == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "id is required");

        return PromoCodeMapper.toResponse(promoCodeService.deactivate(id));
    }

    @GetMapping("/validate/{code}")
    public boolean validate(@PathVariable String code) {
        if (code == null || code.isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "code is required");

       return promoCodeService.isValid(code);

    }

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

}
