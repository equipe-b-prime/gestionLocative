package com.gestionLocative.gestionLocative.controller;

import com.gestionLocative.gestionLocative.dto.ProprietaireRequestDto;
import com.gestionLocative.gestionLocative.dto.Response;
import com.gestionLocative.gestionLocative.dto.ResponseDto;
import com.gestionLocative.gestionLocative.service.ProprietaireService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur REST pour la gestion des propriétaires immobiliers.
 * Assure les opérations de création, modification, consultation, suppression (F1)
 * et consultation de la liste des immeubles possédés (F7).
 *
 * Aucune logique métier dans le contrôleur : toutes les règles sont déléguées à {@link ProprietaireService}.
 */
@Tag(name = "Gestion des Propriétaires", description = "API de gestion des propriétaires (F1, F7)")
@RestController
@RequestMapping("/proprietaires")
public class ProprietaireController {

    private final ProprietaireService proprietaireService;

    /**
     * Constructeur injectant le service métier des propriétaires.
     *
     * @param proprietaireService service des propriétaires
     */
    public ProprietaireController(ProprietaireService proprietaireService) {
        this.proprietaireService = proprietaireService;
    }

    /**
     * F1 - Liste paginée des propriétaires triée par nom de famille.
     *
     * @param page numéro de page (0 par défaut)
     * @param size nombre d'éléments par page (10 par défaut)
     * @return la page des propriétaires
     */
    @Operation(summary = "Lister les propriétaires", description = "Retourne la liste paginée des propriétaires triée par nom")
    @GetMapping
    public Response<ResponseDto> getAll(
            @Parameter(description = "Numéro de page", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page", example = "10")
            @RequestParam(defaultValue = "10") int size) {
        return new Response<>(proprietaireService.getAll(page, size, Sort.by("nom")));
    }

    /**
     * F1 - Obtient un propriétaire par son identifiant unique.
     *
     * @param id identifiant du propriétaire
     * @return le propriétaire correspondant
     */
    @Operation(summary = "Obtenir un propriétaire par ID", description = "Retourne les détails complets d'un propriétaire")
    @GetMapping("/{id}")
    public Response<ResponseDto> getById(
            @Parameter(description = "Identifiant du propriétaire", example = "1")
            @PathVariable Long id) {
        return new Response<>(proprietaireService.getById(id));
    }

    /**
     * F1 - Crée un nouveau propriétaire.
     *
     * @param request données du propriétaire (nom, prenom, numero, email, adresse)
     * @return le propriétaire créé
     */
    @Operation(summary = "F1 - Créer un propriétaire", description = "Enregistre un nouveau propriétaire après contrôle d'unicité de l'email")
    @PostMapping
    public Response<ResponseDto> create(@Valid @RequestBody ProprietaireRequestDto request) {
        return new Response<>(proprietaireService.create(request));
    }

    /**
     * F1 - Modifie un propriétaire existant.
     *
     * @param id identifiant du propriétaire à modifier
     * @param request nouvelles données
     * @return le propriétaire mis à jour
     */
    @Operation(summary = "Modifier un propriétaire", description = "Met à jour les informations d'un propriétaire")
    @PutMapping("/{id}")
    public Response<ResponseDto> update(
            @Parameter(description = "Identifiant du propriétaire", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody ProprietaireRequestDto request) {
        return new Response<>(proprietaireService.update(id, request));
    }

    /**
     * F1 - Supprime un propriétaire s'il ne possède aucun immeuble ou appartement.
     *
     * @param id identifiant du propriétaire
     * @return le propriétaire supprimé
     */
    @Operation(summary = "Supprimer un propriétaire", description = "Supprime un propriétaire s'il ne possède aucun immeuble")
    @DeleteMapping("/{id}")
    public Response<ResponseDto> delete(
            @Parameter(description = "Identifiant du propriétaire", example = "1")
            @PathVariable Long id) {
        return new Response<>(proprietaireService.delete(id));
    }

    /**
     * F7 - Liste tous les immeubles appartenant à un propriétaire donné.
     *
     * @param id identifiant du propriétaire
     * @return liste des immeubles du propriétaire
     */
    @Operation(summary = "F7 - Lister les immeubles d'un propriétaire",
            description = "Retourne tous les immeubles d'un propriétaire avec le nombre d'appartements et de baux actifs")
    @GetMapping("/{id}/immeubles")
    public Response<ResponseDto> getImmeublesByProprietaire(
            @Parameter(description = "Identifiant du propriétaire", example = "1")
            @PathVariable Long id) {
        return new Response<>(proprietaireService.getImmeublesByProprietaire(id));
    }
}
