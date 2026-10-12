package com.gestionLocative.gestionLocative.controller;

import com.gestionLocative.gestionLocative.dto.ProprietaireRequestDto;
import com.gestionLocative.gestionLocative.dto.ResponseDto;
import com.gestionLocative.gestionLocative.service.ProprietaireService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST pour la gestion des proprietaires.
 */
@RestController
@RequestMapping("/proprietaires")
public class ProprietaireController {

    private final ProprietaireService proprietaireService;

    /**
     * Constructeur injectant le service des proprietaires.
     *
     * @param proprietaireService le service des proprietaires
     */
    public ProprietaireController(ProprietaireService proprietaireService) {
        this.proprietaireService = proprietaireService;
    }

    /**
     * Récupère la liste de tous les proprietaires.
     *
     * @return la liste des proprietaires
     */
    @GetMapping
    public List<ResponseDto> getAll() {
        return proprietaireService.getAll();
    }

    /**
     * Récupère le propriétaire à partir de son identifiant.
     *
     * @param id l'identifiant du propriétaire
     * @return le propriétaire trouvé
     * @throws Throwable si le propriétaire n'est pas trouvé
     */
    @GetMapping("/{id}")
    public ResponseDto getById(@PathVariable Long id) throws Throwable {
        return proprietaireService.getById(id);
    }

    /**
     * Crée un propriétaire.
     *
     * @param proprietaireRequest les données de la requête
     * @return le propriétaire créé
     */
    @PostMapping
    public ResponseDto create(@RequestBody ProprietaireRequestDto proprietaireRequest) {
        return proprietaireService.create(proprietaireRequest);
    }

    /**
     * Modifie le propriétaire.
     *
     * @param id l'identifiant du propriétaire à modifier
     * @param proprietaireRequest les données de la requête
     * @return le propriétaire modifié
     */
    @PutMapping("/{id}")
    public ResponseDto update(@PathVariable Long id, @RequestBody ProprietaireRequestDto proprietaireRequest) {
        return proprietaireService.update(id, proprietaireRequest);
    }

    /**
     * Supprime le propriétaire à partir de son identifiant.
     *
     * @param id l'identifiant du propriétaire
     * @return le propriétaire supprimé
     * @throws Throwable si le propriétaire n'est pas trouvé
     */
    @DeleteMapping("/{id}")
    public ResponseDto delete(@PathVariable Long id) throws Throwable {
        return proprietaireService.delete(id);
    }
}
