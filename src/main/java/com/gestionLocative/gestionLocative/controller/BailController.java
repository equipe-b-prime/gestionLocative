package com.gestionLocative.gestionLocative.controller;

import com.gestionLocative.gestionLocative.dto.BailRequestDto;
import com.gestionLocative.gestionLocative.dto.Response;
import com.gestionLocative.gestionLocative.dto.ResponseDto;
import com.gestionLocative.gestionLocative.service.BailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Contrôleur REST pour la gestion des baux de location.
 * Assure les opérations de création, modification, suppression, pagination,
 * résiliation anticipée, renouvellement, révision de loyer et clôture automatique.
 *
 * Toutes les règles métier et validations sont déléguées à {@link BailService}.
 */
@Tag(name = "Gestion des Baux", description = "API de gestion des contrats de bail locatif (F5, F8, F10 à F16)")
@RestController
@RequestMapping("/baux")
public class BailController {

    private final BailService bailService;

    /**
     * Constructeur injectant le service de gestion des baux.
     *
     * @param bailService le service métier des baux
     */
    public BailController(BailService bailService) {
        this.bailService = bailService;
    }

    /**
     * F10 - Liste paginée des baux avec tri paramétrable.
     *
     * @param page numéro de page (0 par défaut)
     * @param size taille de la page (20 par défaut)
     * @param sort critère de tri au format 'champ,direction' (ex: dateDebut,desc ou montantLoyer,desc)
     * @return la page de baux
     */
    @Operation(summary = "F10 - Lister les baux",
            description = "Retourne la liste paginée des baux avec tri paramétrable (ex: sort=dateDebut,desc ou sort=montantLoyer,desc)")
    @GetMapping
    public Response<ResponseDto> getAll(
            @Parameter(description = "Numéro de page (commençant à 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Nombre d'éléments par page", example = "20")
            @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Critère de tri 'champ,asc|desc'", example = "dateDebut,desc")
            @RequestParam(defaultValue = "dateDebut,desc") String sort) {
        return new Response<>(bailService.getAll(page, size, sort));
    }

    /**
     * Consulte un bail par son identifiant unique.
     *
     * @param id identifiant du bail
     * @return le bail correspondant
     */
    @Operation(summary = "Obtenir un bail par ID", description = "Retourne les détails complets d'un bail")
    @GetMapping("/{id}")
    public Response<ResponseDto> getById(
            @Parameter(description = "Identifiant du bail", example = "1")
            @PathVariable Long id) {
        return new Response<>(bailService.getById(id));
    }

    /**
     * F5 / F11 / F12 - Crée un nouveau contrat de bail.
     *
     * @param request données du bail (dateDebut, dateFin, montantLoyer, appartementId, locataireId)
     * @return le bail créé
     */
    @Operation(summary = "F5 - Créer un bail",
            description = "Enregistre un nouveau bail après validation de non-chevauchement (F11), durée minimale d'un mois et cohérence des dates (F12)")
    @PostMapping
    public Response<ResponseDto> create(@Valid @RequestBody BailRequestDto request) {
        return new Response<>(bailService.create(request));
    }

    /**
     * Modifie les informations d'un bail existant.
     *
     * @param id identifiant du bail à modifier
     * @param request nouvelles informations du bail
     * @return le bail mis à jour
     */
    @Operation(summary = "Modifier un bail", description = "Met à jour les informations d'un bail existant")
    @PutMapping("/{id}")
    public Response<ResponseDto> update(
            @Parameter(description = "Identifiant du bail", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody BailRequestDto request) {
        return new Response<>(bailService.update(id, request));
    }

    /**
     * Supprime un bail existant.
     *
     * @param id identifiant du bail à supprimer
     * @return le bail supprimé
     */
    @Operation(summary = "Supprimer un bail", description = "Supprime un bail s'il n'a pas de quittances rattachées")
    @DeleteMapping("/{id}")
    public Response<ResponseDto> delete(
            @Parameter(description = "Identifiant du bail", example = "1")
            @PathVariable Long id) {
        return new Response<>(bailService.delete(id));
    }

    /**
     * F13 - Résilie un bail par anticipation en ramenant sa date de fin à la date d'effet.
     *
     * @param id identifiant du bail
     * @param body map contenant la 'dateEffet' au format yyyy-MM-dd
     * @return le bail résilié
     */
    @Operation(summary = "F13 - Résilier un bail par anticipation",
            description = "Ramène la date de fin du bail à la date d'effet fournie au format ISO yyyy-MM-dd (ex: 2027-06-01)",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Date d'effet de la résiliation au format yyyy-MM-dd",
                    required = true,
                    content = @Content(
                            schema = @Schema(type = "object", example = "{\"dateEffet\": \"2027-06-01\"}"),
                            examples = @ExampleObject(value = "{\"dateEffet\": \"2027-06-01\"}")
                    )
            ))
    @PostMapping("/{id}/resiliation")
    public Response<ResponseDto> resiliation(
            @Parameter(description = "Identifiant du bail", example = "1")
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        return new Response<>(bailService.resiliation(id, body.get("dateEffet")));
    }

    /**
     * F14 - Renouvelle un bail en prolongeant sa date de fin.
     *
     * @param id identifiant du bail
     * @param body map contenant la 'nouvelleDateFin' au format yyyy-MM-dd
     * @return le bail renouvelé avec historique conservé
     */
    @Operation(summary = "F14 - Renouveler un bail",
            description = "Prolonge la date de fin du bail avec enregistrement de l'historique",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Nouvelle date de fin au format yyyy-MM-dd",
                    required = true,
                    content = @Content(
                            schema = @Schema(type = "object", example = "{\"nouvelleDateFin\": \"2028-11-30\"}"),
                            examples = @ExampleObject(value = "{\"nouvelleDateFin\": \"2028-11-30\"}")
                    )
            ))
    @PostMapping("/{id}/renouvellement")
    public Response<ResponseDto> renouvellement(
            @Parameter(description = "Identifiant du bail", example = "1")
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        return new Response<>(bailService.renouvellement(id, body.get("nouvelleDateFin")));
    }

    /**
     * F15 - Révise le montant du loyer d'un bail à une date d'effet donnée.
     *
     * @param id identifiant du bail
     * @param body map contenant 'nouveauMontant' et 'dateEffet' (yyyy-MM-dd)
     * @return le bail révisé
     */
    @Operation(summary = "F15 - Réviser le loyer d'un bail",
            description = "Modifie le loyer en conservant l'historique de chaque révision",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Nouveau montant et date d'effet (format yyyy-MM-dd)",
                    required = true,
                    content = @Content(
                            schema = @Schema(type = "object", example = "{\"nouveauMontant\": 320000, \"dateEffet\": \"2027-07-01\"}"),
                            examples = @ExampleObject(value = "{\"nouveauMontant\": 320000, \"dateEffet\": \"2027-07-01\"}")
                    )
            ))
    @PostMapping("/{id}/revision")
    public Response<ResponseDto> revision(
            @Parameter(description = "Identifiant du bail", example = "1")
            @PathVariable Long id,
            @RequestBody Map<String, Object> body) {
        return new Response<>(bailService.revision(id, body));
    }

    /**
     * F16 - Déclenche manuellement la clôture des baux arrivés à terme.
     *
     * @return map contenant le nombre de baux clôturés ('bauxClotures')
     */
    @Operation(summary = "F16 - Déclencher manuellement la clôture des baux arrivés à terme",
            description = "Passe au statut TERMINE tous les baux dont la date de fin est antérieure à aujourd'hui")
    @PostMapping("/cloture")
    public Map<String, Integer> cloturer() {
        return Map.of("bauxClotures", bailService.cloturerBauxTermines());
    }
}
