package com.gestionLocative.gestionLocative.controller;

import com.gestionLocative.gestionLocative.dto.ImmeubleRequestDto;
import com.gestionLocative.gestionLocative.dto.Response;
import com.gestionLocative.gestionLocative.dto.ResponseDto;
import com.gestionLocative.gestionLocative.service.ImmeubleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Gestion des Immeubles", description = "API de gestion des immeubles")
@RestController
@RequestMapping("/immeubles")
public class ImmeubleController {

    private final ImmeubleService immeubleService;

    public ImmeubleController(ImmeubleService immeubleService) {
        this.immeubleService = immeubleService;
    }

    @Operation(summary = "Lister les immeubles", description = "Retourne la liste paginée des immeubles triée par adresse")
    @GetMapping
    public Response<ResponseDto> getAll(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "10") int size) {
        return new Response<>(immeubleService.getAll(page, size));
    }

    @Operation(summary = "Obtenir un immeuble par ID")
    @GetMapping("/{id}")
    public Response<ResponseDto> getById(@PathVariable Long id) {
        return new Response<>(immeubleService.getById(id));
    }

    @Operation(summary = "Créer un immeuble")
    @PostMapping
    public Response<ResponseDto> create(@Valid @RequestBody ImmeubleRequestDto request) {
        return new Response<>(immeubleService.create(request));
    }

    @Operation(summary = "Modifier un immeuble")
    @PutMapping("/{id}")
    public Response<ResponseDto> update(@PathVariable Long id, @Valid @RequestBody ImmeubleRequestDto request) {
        return new Response<>(immeubleService.update(id, request));
    }

    @Operation(summary = "Supprimer un immeuble")
    @DeleteMapping("/{id}")
    public Response<ResponseDto> delete(@PathVariable Long id) {
        return new Response<>(immeubleService.delete(id));
    }

    @Operation(summary = "Lister les appartements d'un immeuble")
    @GetMapping("/{id}/appartements")
    public Response<ResponseDto> getAppartements(@PathVariable Long id) {
        return new Response<>((java.util.List) immeubleService.getAppartementsByImmeuble(id));
    }
}