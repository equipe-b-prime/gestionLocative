package com.gestionLocative.gestionLocative.controller;

import com.gestionLocative.gestionLocative.dto.ProprietaireRequestDto;
import com.gestionLocative.gestionLocative.dto.Response;
import com.gestionLocative.gestionLocative.dto.ResponseDto;
import com.gestionLocative.gestionLocative.service.ProprietaireService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Gestion des Propriétaires", description = "API de gestion des propriétaires")
@RestController
@RequestMapping("/proprietaires")
public class ProprietaireController {

    private final ProprietaireService proprietaireService;

    public ProprietaireController(ProprietaireService proprietaireService) {
        this.proprietaireService = proprietaireService;
    }

    @Operation(summary = "Lister les propriétaires", description = "Retourne la liste paginée des propriétaires triée par nom")
    @GetMapping
    public Response<ResponseDto> getAll(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "10") int size) {
        return new Response<>(proprietaireService.getAll(page, size, org.springframework.data.domain.Sort.by("nom")));
    }

    @Operation(summary = "Obtenir un propriétaire par ID")
    @GetMapping("/{id}")
    public Response<ResponseDto> getById(@PathVariable Long id) {
        return new Response<>(proprietaireService.getById(id));
    }

    @Operation(summary = "Créer un propriétaire")
    @PostMapping
    public Response<ResponseDto> create(@Valid @RequestBody ProprietaireRequestDto request) {
        return new Response<>(proprietaireService.create(request));
    }

    @Operation(summary = "Modifier un propriétaire")
    @PutMapping("/{id}")
    public Response<ResponseDto> update(@PathVariable Long id, @Valid @RequestBody ProprietaireRequestDto request) {
        return new Response<>(proprietaireService.update(id, request));
    }

    @Operation(summary = "Supprimer un propriétaire")
    @DeleteMapping("/{id}")
    public Response<ResponseDto> delete(@PathVariable Long id) {
        return new Response<>(proprietaireService.delete(id));
    }

    @Operation(summary = "Lister les immeubles d'un propriétaire")
    @GetMapping("/{id}/immeubles")
    public Response<ResponseDto> getImmeublesByProprietaire(@PathVariable Long id) {
        return new Response<>(proprietaireService.getImmeublesByProprietaire(id));
    }
}