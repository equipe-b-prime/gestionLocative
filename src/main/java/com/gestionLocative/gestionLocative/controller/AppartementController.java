package com.gestionLocative.gestionLocative.controller;

import com.gestionLocative.gestionLocative.dto.AppartementRequestDto;
import com.gestionLocative.gestionLocative.dto.Response;
import com.gestionLocative.gestionLocative.dto.ResponseDto;
import com.gestionLocative.gestionLocative.service.AppartementService;
import com.gestionLocative.gestionLocative.service.OccupationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * Contrôleur REST pour la gestion des appartements.
 * Assure les opérations de CRUD, recherche multicritères (F9), consultation de l'historique
 * des baux (F8), appartements vacants à une date (F17) et reconstitution d'occupation (F18).
 *
 * Toute la logique métier et les contrôles sont délégués aux services.
 */
@Tag(name = "Gestion des Appartements", description = "API de gestion des appartements (F3, F8, F9, F17, F18)")
@RestController
@RequestMapping("/appartements")
public class AppartementController {

    private final AppartementService appartementService;
    private final OccupationService occupationService;

    /**
     * Constructeur injectant les services nécessaires.
     *
     * @param appartementService service métier des appartements
     * @param occupationService service de calcul d'occupation et restitution
     */
    public AppartementController(AppartementService appartementService, OccupationService occupationService) {
        this.appartementService = appartementService;
        this.occupationService = occupationService;
    }

    /**
     * F9 - Lister et rechercher les appartements selon des critères optionnels.
     *
     * @param page numéro de page
     * @param size nombre d'éléments par page
     * @param surfaceMin surface minimale en m²
     * @param surfaceMax surface maximale en m²
     * @param pieces nombre de pièces exact
     * @return page de résultats
     */
    @Operation(summary = "F9 - Rechercher des appartements par critères",
            description = "Retourne la liste paginée des appartements avec possibilité de filtrer par surface min/max et nombre de pièces")
    @GetMapping
    public Response<ResponseDto> getAll(
            @Parameter(description = "Numéro de page", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Surface minimale en m²", example = "50")
            @RequestParam(required = false) Double surfaceMin,
            @Parameter(description = "Surface maximale en m²", example = "120")
            @RequestParam(required = false) Double surfaceMax,
            @Parameter(description = "Nombre exact de pièces", example = "3")
            @RequestParam(required = false) Integer pieces) {
        return new Response<>(appartementService.findBySearchCriteria(page, size, surfaceMin, surfaceMax, pieces));
    }

    /**
     * Obtient un appartement par son identifiant unique.
     *
     * @param id identifiant de l'appartement
     * @return l'appartement correspondant
     */
    @Operation(summary = "Obtenir un appartement par ID", description = "Retourne les détails d'un appartement")
    @GetMapping("/{id}")
    public Response<ResponseDto> getById(
            @Parameter(description = "Identifiant de l'appartement", example = "1")
            @PathVariable Long id) {
        return new Response<>(appartementService.getById(id));
    }

    /**
     * F3 - Crée un nouvel appartement dans un immeuble.
     *
     * @param request données de l'appartement
     * @return l'appartement créé
     */
    @Operation(summary = "F3 - Créer un appartement", description = "Enregistre un appartement après contrôle de l'étage et de l'immeuble")
    @PostMapping
    public Response<ResponseDto> create(@Valid @RequestBody AppartementRequestDto request) {
        return new Response<>(appartementService.create(request));
    }

    /**
     * Modifie un appartement existant.
     *
     * @param id identifiant de l'appartement
     * @param request nouvelles données
     * @return l'appartement mis à jour
     */
    @Operation(summary = "Modifier un appartement", description = "Met à jour les informations d'un appartement")
    @PutMapping("/{id}")
    public Response<ResponseDto> update(
            @Parameter(description = "Identifiant de l'appartement", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody AppartementRequestDto request) {
        return new Response<>(appartementService.update(id, request));
    }

    /**
     * Supprime un appartement existant.
     *
     * @param id identifiant de l'appartement
     * @return l'appartement supprimé
     */
    @Operation(summary = "Supprimer un appartement", description = "Supprime un appartement s'il n'est rattaché à aucun bail")
    @DeleteMapping("/{id}")
    public Response<ResponseDto> delete(
            @Parameter(description = "Identifiant de l'appartement", example = "1")
            @PathVariable Long id) {
        return new Response<>(appartementService.delete(id));
    }

    /**
     * F8 - Consulte l'historique complet des baux d'un appartement.
     *
     * @param id identifiant de l'appartement
     * @return liste des baux triés du plus récent au plus ancien
     */
    @Operation(summary = "F8 - Consulter l'historique des baux d'un appartement",
            description = "Retourne la liste chronologique décroissante des baux passés et actuels d'un appartement")
    @GetMapping("/{id}/baux")
    public Response<ResponseDto> getBauxByAppartement(
            @Parameter(description = "Identifiant de l'appartement", example = "1")
            @PathVariable Long id) {
        return new Response<>(appartementService.getBauxByAppartement(id));
    }

    /**
     * F17 - Liste tous les appartements libres à une date donnée.
     *
     * @param date date d'observation au format ISO yyyy-MM-dd (aujourd'hui par défaut)
     * @return appartements libres groupés par immeuble
     */
    @Operation(summary = "F17 - Lister les appartements libres à une date",
            description = "Appartements qu'aucun bail ne couvre à la date (date du jour par défaut), groupés par immeuble avec surface et dernier loyer connu")
    @GetMapping("/libres")
    public Response<ResponseDto> libres(
            @Parameter(description = "Date d'observation au format ISO yyyy-MM-dd (ex: 2026-12-31). Si omise, la date du jour est utilisée.", example = "2026-12-31")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return new Response<>(occupationService.appartementsLibres(date));
    }

    /**
     * F18 - Reconstitue l'historique chronologique d'occupation d'un appartement.
     *
     * @param id identifiant de l'appartement
     * @return liste chronologique alternant périodes occupées et périodes de vacance
     */
    @Operation(summary = "F18 - Reconstituer l'occupation d'un appartement",
            description = "Frise chronologique alternant périodes occupées (locataire, loyer effectif révisé) et périodes de vacance explicites")
    @GetMapping("/{id}/occupation")
    public Response<ResponseDto> occupation(
            @Parameter(description = "Identifiant de l'appartement", example = "1")
            @PathVariable Long id) {
        return new Response<>(occupationService.occupation(id));
    }
}
