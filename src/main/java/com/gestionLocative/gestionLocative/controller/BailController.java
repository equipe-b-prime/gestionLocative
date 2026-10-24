package com.gestionLocative.gestionLocative.controller;

import com.gestionLocative.gestionLocative.exception.NotFoundException;
import com.gestionLocative.gestionLocative.dto.BailRequestDto;
import com.gestionLocative.gestionLocative.dto.Response;
import com.gestionLocative.gestionLocative.dto.ResponseDto;
import com.gestionLocative.gestionLocative.service.BailService;
import com.gestionLocative.gestionLocative.service.BailStatsService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import com.gestionLocative.gestionLocative.dto.RevenuImmeubleDto;

/**
 * Contrôleur REST pour la gestion des baux.
 */
@RestController
@RequestMapping("/baux")
public class BailController {

    private final BailService bailService;
    private final BailStatsService bailStatsService;

    /**
     * Constructeur injectant le service des baux et celui des statistiques financières.
     *
     * @param bailService le service des baux
     * @param bailStatsService le service des statistiques financières du bail
     */
    public BailController(BailService bailService, BailStatsService bailStatsService) {
        this.bailService = bailService;
        this.bailStatsService = bailStatsService;
    }

    /**
     * Récupère une page de baux, triés selon un critère donné.
     *
     * @param page l'index de la page souhaitée (0 par défaut)
     * @param taille le nombre d'éléments par page (10 par défaut, borné à 100)
     * @param sort le tri sous la forme "propriete,direction" (ex: dateDebut,desc)
     * @return Response<ResponseDto> La page des baux encapsulée.
     */
    @GetMapping
    public Response<ResponseDto> getAll(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "10") int taille,
                                        @RequestParam(required = false) String sort) {
        return new Response<>(bailService.getAll(page, taille, sort).getContent());
    }

    /**
     * Récupère le bail à partir de son identifiant.
     *
     * @param id l'identifiant du bail
     * @return le bail trouvé
     * @throws NotFoundException si le bail n'est pas trouvé
     */
    @GetMapping("/{id}")
    public Response<ResponseDto>  getById(@PathVariable Long id) {
        return new Response<>(bailService.getById(id));
    }

    /**
     * Crée un bail.
     *
     * @param bailRequest les données de la requête
     * @return le bail créé
     */
    @PostMapping
    public Response<ResponseDto>  create(@Valid  @RequestBody BailRequestDto bailRequest) {
        return new Response<>(bailService.create(bailRequest));
    }

    /**
     * Modifie le bail.
     *
     * @param id l'identifiant du bail à modifier
     * @param bailRequest les données de la requête
     * @return le bail modifié
     */
    @PutMapping("/{id}")
    public Response<ResponseDto>  update(@PathVariable Long id, @Valid @RequestBody BailRequestDto bailRequest) {
        return new Response<>(bailService.update(id, bailRequest));
    }

    /**
     * Supprime le bail à partir de son identifiant.
     *
     * @param id l'identifiant du bail
     * @return le bail supprimé
     * @throws NotFoundException si le bail n'est pas trouvé
     */
    @DeleteMapping("/{id}")
    public Response<ResponseDto>  delete(@PathVariable Long id) {
        return new Response<>(bailService.delete(id));
    }

    /**
     * Récupère la situation financière agrégée d'un bail
     * depuis la vue v_bail_financier (facturé, encaissé, reste du, quittances non payées).
     *
     * @param id l'identifiant du bail
     * @return la situation financière du bail
     * @throws NotFoundException si le bail n'est pas trouvé dans la vue
     */
    @GetMapping("/{id}/stats")
    public Response<ResponseDto> getStats(@PathVariable Long id) {
        return new Response<>(bailStatsService.getBailStats(id));
    }

    /**
     * F13: Résilie un bail par anticipation avec une date d'effet.
     */
    @PostMapping("/{id}/resiliation")
    public Response<ResponseDto> resiliation(@PathVariable Long id,
                                             @RequestBody java.util.Map<String, String> body) {
        return new Response<>(bailService.resiliation(id, body.get("dateEffet")));
    }

    /**
     * F14: Renouvelle un bail en prolongeant sa date de fin.
     */
    @PostMapping("/{id}/renouvellement")
    public Response<ResponseDto> renouvellement(@PathVariable Long id,
                                                @RequestBody java.util.Map<String, String> body) {
        return new Response<>(bailService.renouvellement(id, body.get("nouvelleDateFin")));
    }

    /**
     * F15: Révise le loyer d'un bail.
     */
    @PostMapping("/{id}/revision")
    public Response<ResponseDto> revision(@PathVariable Long id,
                                          @RequestBody java.util.Map<String, String> body) {
        return new Response<>(bailService.revision(id,
                new java.math.BigDecimal(body.get("nouveauMontant")),
                body.get("dateEffet")));
    }

    /**
     * F19: Calcule le revenu locatif d'un immeuble sur un mois.
     *
     * @param id L'identifiant de limmeuble
     * @param mois Le mois au format yyyy-MM (null = mois courant)
     * @return Le revenu locatif avec dhail et taux dhbergement
     */
    @PostMapping("/{id}/revenu")
    public Response<RevenuImmeubleDto> revenu(@PathVariable Long id,
                                              @RequestParam(required = false) String mois) {
        return new Response<>(bailService.getRevenuImmeuble(id, mois));
    }
}
