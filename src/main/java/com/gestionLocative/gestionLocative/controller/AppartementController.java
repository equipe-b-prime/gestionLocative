package com.gestionLocative.gestionLocative.controller;

import com.gestionLocative.gestionLocative.dto.AppartementRequestDto;
import com.gestionLocative.gestionLocative.dto.Response;
import com.gestionLocative.gestionLocative.dto.ResponseDto;
import com.gestionLocative.gestionLocative.service.AppartementService;
import com.gestionLocative.gestionLocative.service.BailService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/appartements")
public class AppartementController {

    private final AppartementService appartementService;
    private final BailService bailService;

    public AppartementController(AppartementService appartementService, BailService bailService) {
        this.appartementService = appartementService;
        this.bailService = bailService;
    }

    @GetMapping
    public Response<ResponseDto> getAll(@RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "10") int taille,
                                       @RequestParam(required = false) Double surfaceMin,
                                       @RequestParam(required = false) Double surfaceMax,
                                       @RequestParam(required = false) Integer pieces) {

        if (surfaceMin != null && surfaceMax != null && surfaceMin > surfaceMax) {
            throw new IllegalArgumentException("La surface minimale ne peut pas être supérieure à la surface maximale.");
        }

        return new Response<>(appartementService.findBySearchCriteria(page, taille, surfaceMin, surfaceMax, pieces).getContent());
    }

    @GetMapping("/{id}")
    public Response<ResponseDto> getById(@PathVariable Long id) {
        return new Response<>(appartementService.getById(id));
    }

    @PostMapping
    public Response<ResponseDto> create(@Valid @RequestBody AppartementRequestDto appartementRequest) {
        return new Response<>(appartementService.create(appartementRequest));
    }

    @PutMapping("/{id}")
    public Response<ResponseDto> update(@PathVariable Long id,
                                        @Valid @RequestBody AppartementRequestDto appartementRequest) {
        return new Response<>(appartementService.update(id, appartementRequest));
    }

    @DeleteMapping("/{id}")
    public Response<ResponseDto> delete(@PathVariable Long id) {
        return new Response<>(appartementService.delete(id));
    }

    @GetMapping("/{id}/baux")
    public Response<ResponseDto> getBaux(@PathVariable Long id) {
        return new Response<>(appartementService.getBauxByAppartement(id));
    }

    /**
     * F17. Appartements libres à une date donnée.
     */
    @GetMapping("/libres")
    public Response<ResponseDto> getLibres(@RequestParam(required = false) LocalDate date) {
        return new Response<>(appartementService.getLibres(date));
    }

    /**
     * F18. Reconstituer l'occupation d'un appartement.
     */
    @GetMapping("/{id}/occupation")
    public Response<?> getOccupation(@PathVariable Long id) {
        return new Response<>(bailService.getOccupationAppartement(id));
    }
}