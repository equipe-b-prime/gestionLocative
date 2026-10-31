package com.gestionLocative.gestionLocative.controller;

import com.gestionLocative.gestionLocative.dto.ImmeubleRequestDto;
import com.gestionLocative.gestionLocative.dto.Response;
import com.gestionLocative.gestionLocative.dto.ResponseDto;
import com.gestionLocative.gestionLocative.service.ImmeubleService;
import com.gestionLocative.gestionLocative.service.OccupationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST pour la gestion des immeubles.
 * Assure les opérations de CRUD, la consultation des appartements rattachés (F6)
 * et le calcul financier du revenu locatif mensuel au prorata temporis (F19).
 *
 * Toute la logique métier est déléguée aux services.
 */
@Tag(name = "Gestion des Immeubles", description = "API de gestion des immeubles (F2, F6, F19)")
@RestController
@RequestMapping("/immeubles")
public class ImmeubleController {

    private final ImmeubleService immeubleService;
    private final OccupationService occupationService;

    /**
     * Constructeur injectant les services nécessaires.
     *
     * @param immeubleService service de gestion des immeubles
     * @param occupationService service de calcul d'occupation et revenus
     */
    public ImmeubleController(ImmeubleService immeubleService, OccupationService occupationService) {
        this.immeubleService = immeubleService;
        this.occupationService = occupationService;
    }

    /**
     * Liste paginée des immeubles triée par adresse.
     *
     * @param page numéro de page
     * @param size nombre d'éléments par page
     * @return la page d'immeubles
     */
    @Operation(summary = "Lister les immeubles", description = "Retourne la liste paginée des immeubles triée par adresse")
    @GetMapping
    public Response<ResponseDto> getAll(
            @Parameter(description = "Numéro de page", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de page", example = "10")
            @RequestParam(defaultValue = "10") int size) {
        return new Response<>(immeubleService.getAll(page, size));
    }

    /**
     * Obtient un immeuble par son identifiant unique.
     *
     * @param id identifiant de l'immeuble
     * @return les informations de l'immeuble avec son nombre d'appartements
     */
    @Operation(summary = "Obtenir un immeuble par ID", description = "Retourne les détails d'un immeuble avec son nombre d'appartements")
    @GetMapping("/{id}")
    public Response<ResponseDto> getById(
            @Parameter(description = "Identifiant de l'immeuble", example = "1")
            @PathVariable Long id) {
        return new Response<>(immeubleService.getById(id));
    }

    /**
     * F2 - Crée un nouvel immeuble.
     *
     * @param request données de l'immeuble (nom, adresse, nbEtages, proprietaireId)
     * @return l'immeuble créé
     */
    @Operation(summary = "F2 - Créer un immeuble", description = "Enregistre un immeuble après validation d'unicité nom/adresse et nbEtages positif")
    @PostMapping
    public Response<ResponseDto> create(@Valid @RequestBody ImmeubleRequestDto request) {
        return new Response<>(immeubleService.create(request));
    }

    /**
     * Modifie un immeuble existant.
     *
     * @param id identifiant de l'immeuble
     * @param request nouvelles informations
     * @return l'immeuble mis à jour
     */
    @Operation(summary = "Modifier un immeuble", description = "Met à jour les informations d'un immeuble")
    @PutMapping("/{id}")
    public Response<ResponseDto> update(
            @Parameter(description = "Identifiant de l'immeuble", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody ImmeubleRequestDto request) {
        return new Response<>(immeubleService.update(id, request));
    }

    /**
     * Supprime un immeuble s'il ne contient aucun appartement.
     *
     * @param id identifiant de l'immeuble
     * @return l'immeuble supprimé
     */
    @Operation(summary = "Supprimer un immeuble", description = "Supprime un immeuble s'il ne contient aucun appartement")
    @DeleteMapping("/{id}")
    public Response<ResponseDto> delete(
            @Parameter(description = "Identifiant de l'immeuble", example = "1")
            @PathVariable Long id) {
        return new Response<>(immeubleService.delete(id));
    }

    /**
     * F6 - Liste tous les appartements d'un immeuble triés par étage.
     *
     * @param id identifiant de l'immeuble
     * @return liste des appartements triés par étage croissant avec état d'occupation
     */
    @Operation(summary = "F6 - Lister les appartements d'un immeuble",
            description = "Retourne les appartements d'un immeuble triés par étage croissant avec état d'occupation")
    @GetMapping("/{id}/appartements")
    public Response<ResponseDto> getAppartements(
            @Parameter(description = "Identifiant de l'immeuble", example = "1")
            @PathVariable Long id) {
        return new Response<>(immeubleService.getAppartementsByImmeuble(id));
    }

    /**
     * F19 - Calcule le revenu locatif mensuel au prorata temporis et le taux d'occupation d'un immeuble.
     *
     * @param id identifiant de l'immeuble
     * @param mois mois au format strict ISO yyyy-MM (ex: 2026-11)
     * @return synthèse des revenus et détails par appartement
     */
    @Operation(summary = "F19 - Calculer le revenu locatif d'un immeuble sur un mois",
            description = "Loyers des baux actifs au prorata des jours, détail par appartement et taux d'occupation (mois au format strict yyyy-MM)")
    @GetMapping("/{id}/revenu")
    public Response<ResponseDto> revenu(
            @Parameter(description = "Identifiant de l'immeuble", example = "1")
            @PathVariable Long id,
            @Parameter(description = "Mois d'analyse au format strict yyyy-MM (ex: 2026-11)", example = "2026-11")
            @RequestParam String mois) {
        return new Response<>(occupationService.revenu(id, mois));
    }
}
