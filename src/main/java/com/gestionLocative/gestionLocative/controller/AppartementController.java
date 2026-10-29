package com.gestionLocative.gestionLocative.controller;

import com.gestionLocative.gestionLocative.dto.AppartementRequestDto;
import com.gestionLocative.gestionLocative.dto.Response;
import com.gestionLocative.gestionLocative.dto.ResponseDto;
import com.gestionLocative.gestionLocative.service.AppartementService;
import com.gestionLocative.gestionLocative.service.OccupationService;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Gestion des Appartements", description = "API de gestion des appartements")
@RestController
@RequestMapping("/appartements")
public class AppartementController {

    private final AppartementService appartementService;
    private final OccupationService occupationService;

    public AppartementController(AppartementService appartementService, OccupationService occupationService) {
        this.appartementService = appartementService;
        this.occupationService = occupationService;
    }

    @Operation(summary = "Lister les appartements", description = "Retourne la liste paginée des appartements avec possibilité de filtrer par surface min/max et nombre de pièces")
    @GetMapping
    public Response<ResponseDto> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Surface minimale en m²")
            @RequestParam(required = false) Double surfaceMin,
            @Parameter(description = "Surface maximale en m²")
            @RequestParam(required = false) Double surfaceMax,
            @Parameter(description = "Nombre de pièces")
            @RequestParam(required = false) Integer pieces) {
        if (surfaceMin != null && surfaceMax != null && surfaceMin > surfaceMax) {
            throw new IllegalArgumentException("La surface minimale ne peut pas être supérieure à la surface maximale.");
        }
        return new Response<>(appartementService.findBySearchCriteria(page, size, surfaceMin, surfaceMax, pieces));
    }

    @Operation(summary = "Obtenir un appartement par ID")
    @GetMapping("/{id}")
    public Response<ResponseDto> getById(@PathVariable Long id) {
        return new Response<>(appartementService.getById(id));
    }

    @Operation(summary = "Créer un appartement")
    @PostMapping
    public Response<ResponseDto> create(@Valid @RequestBody AppartementRequestDto request) {
        return new Response<>(appartementService.create(request));
    }

    @Operation(summary = "Modifier un appartement")
    @PutMapping("/{id}")
    public Response<ResponseDto> update(@PathVariable Long id, @Valid @RequestBody AppartementRequestDto request) {
        return new Response<>(appartementService.update(id, request));
    }

    @Operation(summary = "Supprimer un appartement")
    @DeleteMapping("/{id}")
    public Response<ResponseDto> delete(@PathVariable Long id) {
        return new Response<>(appartementService.delete(id));
    }

    @Operation(summary = "Consulter l'historique des baux d'un appartement")
    @GetMapping("/{id}/baux")
    public Response<ResponseDto> getBauxByAppartement(@PathVariable Long id) {
        return new Response<>(appartementService.getBauxByAppartement(id));
    }

    @Operation(summary = "F17 - Lister les appartements libres à une date",
            description = "Appartements qu'aucun bail ne couvre à la date (date du jour par défaut), groupés par immeuble, avec surface et dernier loyer connu")
    @GetMapping("/libres")
    public Response<ResponseDto> libres(
            @Parameter(description = "Date au format yyyy-MM-dd (défaut : aujourd'hui)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return new Response<>(occupationService.appartementsLibres(date));
    }

    @Operation(summary = "F18 - Reconstituer l'occupation d'un appartement",
            description = "Périodes chronologiques (locataire et loyer applicable d'après les révisions), vacances explicites")
    @GetMapping("/{id}/occupation")
    public Response<ResponseDto> occupation(@PathVariable Long id) {
        return new Response<>(occupationService.occupation(id));
    }
}
