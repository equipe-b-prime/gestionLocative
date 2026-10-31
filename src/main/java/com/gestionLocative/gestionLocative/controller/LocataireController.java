package com.gestionLocative.gestionLocative.controller;

import com.gestionLocative.gestionLocative.dto.LocataireRequestDto;
import com.gestionLocative.gestionLocative.dto.Response;
import com.gestionLocative.gestionLocative.dto.ResponseDto;
import com.gestionLocative.gestionLocative.service.LocataireService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur REST pour la gestion des locataires.
 * Assure les opérations de création, modification, consultation et suppression des locataires (F4).
 *
 * Toute la logique métier et les contrôles d'unicité sont délégués à {@link LocataireService}.
 */
@Tag(name = "Gestion des Locataires", description = "API de gestion des locataires (F4)")
@RestController
@RequestMapping("/locataires")
public class LocataireController {

    private final LocataireService locataireService;

    /**
     * Constructeur injectant le service métier des locataires.
     *
     * @param locataireService service des locataires
     */
    public LocataireController(LocataireService locataireService) {
        this.locataireService = locataireService;
    }

    /**
     * F4 - Liste paginée des locataires triée par nom de famille.
     *
     * @param page numéro de page (0 par défaut)
     * @param size nombre d'éléments par page (10 par défaut)
     * @return la page des locataires
     */
    @Operation(summary = "Lister les locataires", description = "Retourne la liste paginée des locataires triée par nom")
    @GetMapping
    public Response<ResponseDto> getAll(
            @Parameter(description = "Numéro de page", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de page", example = "10")
            @RequestParam(defaultValue = "10") int size) {
        return new Response<>(locataireService.getAll(page, size));
    }

    /**
     * F4 - Obtient un locataire par son identifiant unique.
     *
     * @param id identifiant du locataire
     * @return le locataire correspondant
     */
    @Operation(summary = "Obtenir un locataire par ID", description = "Retourne les informations complètes d'un locataire")
    @GetMapping("/{id}")
    public Response<ResponseDto> getById(
            @Parameter(description = "Identifiant du locataire", example = "1")
            @PathVariable Long id) {
        return new Response<>(locataireService.getById(id));
    }

    /**
     * F4 - Crée un nouveau locataire avec contrôle d'unicité de l'email.
     *
     * @param request données du locataire (nom, prenom, numero, email)
     * @return le locataire créé
     */
    @Operation(summary = "F4 - Créer un locataire", description = "Enregistre un nouveau locataire après contrôle d'unicité de l'email")
    @PostMapping
    public Response<ResponseDto> create(@Valid @RequestBody LocataireRequestDto request) {
        return new Response<>(locataireService.create(request));
    }

    /**
     * F4 - Met à jour les informations d'un locataire existant.
     *
     * @param id identifiant du locataire à modifier
     * @param request nouvelles informations
     * @return le locataire mis à jour
     */
    @Operation(summary = "Modifier un locataire", description = "Met à jour les informations d'un locataire existant")
    @PutMapping("/{id}")
    public Response<ResponseDto> update(
            @Parameter(description = "Identifiant du locataire", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody LocataireRequestDto request) {
        return new Response<>(locataireService.update(id, request));
    }

    /**
     * F4 - Supprime un locataire s'il n'a aucun bail actif ou passé rattaché.
     *
     * @param id identifiant du locataire à supprimer
     * @return le locataire supprimé
     */
    @Operation(summary = "Supprimer un locataire", description = "Supprime un locataire s'il n'est rattaché à aucun contrat de bail")
    @DeleteMapping("/{id}")
    public Response<ResponseDto> delete(
            @Parameter(description = "Identifiant du locataire", example = "1")
            @PathVariable Long id) {
        return new Response<>(locataireService.delete(id));
    }
}
