package com.gestionLocative.gestionLocative.controller;

import com.gestionLocative.gestionLocative.exception.NotFoundException;
import com.gestionLocative.gestionLocative.dto.LocataireRequestDto;
import com.gestionLocative.gestionLocative.dto.Response;
import com.gestionLocative.gestionLocative.dto.ResponseDto;
import com.gestionLocative.gestionLocative.service.LocataireService;
import com.gestionLocative.gestionLocative.service.LocataireStatsService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;


/**
 * Contrôleur REST pour la gestion des locataires.
 */
@RestController
@RequestMapping("/locataires")
public class LocataireController {

    private final LocataireService locataireService;
    private final LocataireStatsService locataireStatsService;

    /**
     * Constructeur injectant le service des locataires et celui des statistiques de situation.
     *
     * @param locataireService le service des locataires
     * @param locataireStatsService le service des statistiques financières du locataire
     */
    public LocataireController(LocataireService locataireService, LocataireStatsService locataireStatsService) {
        this.locataireService = locataireService;
        this.locataireStatsService = locataireStatsService;
    }

    /**
     * Récupère une page de locataires.
     *
     * @param page l'index de la page souhaitée (0 par défaut)
     * @param taille le nombre d'éléments par page (10 par défaut, borné à 100)
     * @return Response<ResponseDto> La page des locataires encapsulée.
     */
    @GetMapping
    public Response<ResponseDto> getAll(@RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "10") int taille) {
        return new Response<>(locataireService.getAll(page, taille).getContent());
    }

    /**
     * Récupère le locataire à partir de son identifiant.
     *
     * @param id l'identifiant du locataire
     * @return le locataire trouvé
     * @throws NotFoundException si le locataire n'est pas trouvé
     */
    @GetMapping("/{id}")
    public Response<ResponseDto> getById(@PathVariable Long id) {
        return new Response<>(locataireService.getById(id));
    }

    /**
     * Crée un locataire.
     *
     * @param locataireRequest les données de la requête
     * @return le locataire créé
     */
    @PostMapping
    public Response<ResponseDto> create(@Valid  @RequestBody LocataireRequestDto locataireRequest) {
        return new Response<>(locataireService.create(locataireRequest));
    }

    /**
     * Modifie le locataire.
     *
     * @param id l'identifiant du locataire à modifier
     * @param locataireRequest les données de la requête
     * @return le locataire modifié
     */
    @PutMapping("/{id}")
    public Response<ResponseDto> update(@PathVariable Long id, @Valid @RequestBody LocataireRequestDto locataireRequest) {
        return new Response<>(locataireService.update(id, locataireRequest));
    }

    /**
     * Supprime le locataire à partir de son identifiant.
     *
     * @param id l'identifiant du locataire
     * @return le locataire supprimé
     * @throws NotFoundException si le locataire n'est pas trouvé
     */
    @DeleteMapping("/{id}")
    public Response<ResponseDto> delete(@PathVariable Long id) {
        return new Response<>(locataireService.delete(id));
    }

    /**
     * Récupère la situation financière agrégée d'un locataire
     * depuis la vue v_locataire_situation (baux, quittances, encaissé, reste du).
     *
     * @param id l'identifiant du locataire
     * @return la situation financière du locataire
     * @throws NotFoundException si le locataire n'est pas trouvé dans la vue
     */
    @GetMapping("/{id}/stats")
    public Response<ResponseDto> getStats(@PathVariable Long id) {
        return new Response<>(locataireStatsService.getLocataireStats(id));
    }
}
