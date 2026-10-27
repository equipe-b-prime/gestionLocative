package com.gestionLocative.gestionLocative.controller;

import com.gestionLocative.gestionLocative.dto.BailRequestDto;
import com.gestionLocative.gestionLocative.dto.Response;
import com.gestionLocative.gestionLocative.dto.ResponseDto;
import com.gestionLocative.gestionLocative.service.BailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Gestion des Baux", description = "API de gestion des baux")
@RestController
@RequestMapping("/baux")
public class BailController {

    private final BailService bailService;

    public BailController(BailService bailService) {
        this.bailService = bailService;
    }

    @Operation(summary = "Lister les baux", description = "Retourne la liste paginée des baux triée par date de début décroissante")
    @GetMapping
    public Response<ResponseDto> getAll(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "10") int size) {
        return new Response<>(bailService.getAll(page, size, Sort.by(Sort.Order.desc("dateDebut"))));
    }

    @Operation(summary = "Obtenir un bail par ID")
    @GetMapping("/{id}")
    public Response<ResponseDto> getById(@PathVariable Long id) {
        return new Response<>(bailService.getById(id));
    }

    @Operation(summary = "Créer un bail")
    @PostMapping
    public Response<ResponseDto> create(@Valid @RequestBody BailRequestDto request) {
        return new Response<>(bailService.create(request));
    }

    @Operation(summary = "Modifier un bail")
    @PutMapping("/{id}")
    public Response<ResponseDto> update(@PathVariable Long id, @Valid @RequestBody BailRequestDto request) {
        return new Response<>(bailService.update(id, request));
    }

    @Operation(summary = "Supprimer un bail")
    @DeleteMapping("/{id}")
    public Response<ResponseDto> delete(@PathVariable Long id) {
        return new Response<>(bailService.delete(id));
    }
}