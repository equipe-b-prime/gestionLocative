package com.gestionLocative.gestionLocative.controller;

import com.gestionLocative.gestionLocative.dto.LocataireRequestDto;
import com.gestionLocative.gestionLocative.dto.Response;
import com.gestionLocative.gestionLocative.dto.ResponseDto;
import com.gestionLocative.gestionLocative.service.LocataireService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Gestion des Locataires", description = "API de gestion des locataires")
@RestController
@RequestMapping("/locataires")
public class LocataireController {

    private final LocataireService locataireService;

    public LocataireController(LocataireService locataireService) {
        this.locataireService = locataireService;
    }

    @Operation(summary = "Lister les locataires", description = "Retourne la liste paginée des locataires triée par nom")
    @GetMapping
    public Response<ResponseDto> getAll(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "10") int size) {
        return new Response<>(locataireService.getAll(page, size));
    }

    @Operation(summary = "Obtenir un locataire par ID")
    @GetMapping("/{id}")
    public Response<ResponseDto> getById(@PathVariable Long id) {
        return new Response<>(locataireService.getById(id));
    }

    @Operation(summary = "Créer un locataire")
    @PostMapping
    public Response<ResponseDto> create(@Valid @RequestBody LocataireRequestDto request) {
        return new Response<>(locataireService.create(request));
    }

    @Operation(summary = "Modifier un locataire")
    @PutMapping("/{id}")
    public Response<ResponseDto> update(@PathVariable Long id, @Valid @RequestBody LocataireRequestDto request) {
        return new Response<>(locataireService.update(id, request));
    }

    @Operation(summary = "Supprimer un locataire")
    @DeleteMapping("/{id}")
    public Response<ResponseDto> delete(@PathVariable Long id) {
        return new Response<>(locataireService.delete(id));
    }
}