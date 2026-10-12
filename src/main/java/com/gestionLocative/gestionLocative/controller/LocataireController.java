package com.gestionLocative.gestionLocative.controller;

import com.gestionLocative.gestionLocative.dto.LocataireRequestDto;
import com.gestionLocative.gestionLocative.dto.ResponseDto;
import com.gestionLocative.gestionLocative.service.LocataireService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST pour la gestion des locataires.
 */
@RestController
@RequestMapping("/locataires")
public class LocataireController {

    private final LocataireService locataireService;

    /**
     * Constructeur injectant le service des locataires.
     *
     * @param locataireService le service des locataires
     */
    public LocataireController(LocataireService locataireService) {
        this.locataireService = locataireService;
    }

    /**
     * Récupère la liste de tous les locataires.
     *
     * @return la liste des locataires
     */
    @GetMapping
    public List<ResponseDto> getAll() {
        return locataireService.getAll();
    }

    /**
     * Récupère le locataire à partir de son identifiant.
     *
     * @param id l'identifiant du locataire
     * @return le locataire trouvé
     * @throws Throwable si le locataire n'est pas trouvé
     */
    @GetMapping("/{id}")
    public ResponseDto getById(@PathVariable Long id) throws Throwable {
        return locataireService.getById(id);
    }

    /**
     * Crée un locataire.
     *
     * @param locataireRequest les données de la requête
     * @return le locataire créé
     */
    @PostMapping
    public ResponseDto create(@RequestBody LocataireRequestDto locataireRequest) {
        return locataireService.create(locataireRequest);
    }

    /**
     * Modifie le locataire.
     *
     * @param id l'identifiant du locataire à modifier
     * @param locataireRequest les données de la requête
     * @return le locataire modifié
     */
    @PutMapping("/{id}")
    public ResponseDto update(@PathVariable Long id, @RequestBody LocataireRequestDto locataireRequest) {
        return locataireService.update(id, locataireRequest);
    }

    /**
     * Supprime le locataire à partir de son identifiant.
     *
     * @param id l'identifiant du locataire
     * @return le locataire supprimé
     * @throws Throwable si le locataire n'est pas trouvé
     */
    @DeleteMapping("/{id}")
    public ResponseDto delete(@PathVariable Long id) throws Throwable {
        return locataireService.delete(id);
    }
}
