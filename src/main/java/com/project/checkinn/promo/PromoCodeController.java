package com.project.checkinn.promo;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/promoCodes")
public class PromoCodeController {

     private final PromoCodeService promoCodeService;

    public PromoCodeController(PromoCodeService promoCodeService) {
        this.promoCodeService = promoCodeService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PromoCodeResponse create(@RequestBody PromoCodeRequest request) {
        PromoCode entity = PromoCodeMapper.toEntity(request);
        PromoCode created = promoCodeService.create(entity);
        return PromoCodeMapper.toResponse(created);
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
        return PromoCodeMapper.toResponse(promoCodeService.getById(id));
    }

    @GetMapping("/code/{code}")
    public PromoCodeResponse getByCode(@PathVariable String code) {
        return PromoCodeMapper.toResponse(promoCodeService.getByCode(code));
    }

    @PutMapping("/{id}/deactivate")
    public PromoCodeResponse deactivate(@PathVariable Long id) {
        return PromoCodeMapper.toResponse(promoCodeService.deactivate(id));
    }

    @GetMapping("/validate/{code}")
    public boolean validate(@PathVariable String code) {
        return promoCodeService.isValid(code);
    }



}
