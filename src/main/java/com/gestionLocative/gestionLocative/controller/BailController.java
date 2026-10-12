package com.gestionLocative.gestionLocative.controller;

import com.gestionLocative.gestionLocative.dto.BailRequestDto;
import com.gestionLocative.gestionLocative.dto.ResponseDto;
import com.gestionLocative.gestionLocative.service.BailService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST pour la gestion des baux.
 */
@RestController
@RequestMapping("/baux")
public class BailController {

    private final BailService bailService;

    /**
     * Constructeur injectant le service des baux.
     *
     * @param bailService le service des baux
     */
    public BailController(BailService bailService) {
        this.bailService = bailService;
    }

    /**
     * Récupère la liste de tous les baux.
     *
     * @return la liste des baux
     */
    @GetMapping
    public List<ResponseDto> getAll() {
        return bailService.getAll();
    }

    /**
     * Récupère le bail à partir de son identifiant.
     *
     * @param id l'identifiant du bail
     * @return le bail trouvé
     * @throws Throwable si le bail n'est pas trouvé
     */
    @GetMapping("/{id}")
    public ResponseDto getById(@PathVariable Long id) throws Throwable {
        return bailService.getById(id);
    }

    /**
     * Crée un bail.
     *
     * @param bailRequest les données de la requête
     * @return le bail créé
     */
    @PostMapping
    public ResponseDto create(@RequestBody BailRequestDto bailRequest) {
        return bailService.create(bailRequest);
    }

    /**
     * Modifie le bail.
     *
     * @param id l'identifiant du bail à modifier
     * @param bailRequest les données de la requête
     * @return le bail modifié
     */
    @PutMapping("/{id}")
    public ResponseDto update(@PathVariable Long id, @RequestBody BailRequestDto bailRequest) {
        return bailService.update(id, bailRequest);
    }

    /**
     * Supprime le bail à partir de son identifiant.
     *
     * @param id l'identifiant du bail
     * @return le bail supprimé
     * @throws Throwable si le bail n'est pas trouvé
     */
    @DeleteMapping("/{id}")
    public ResponseDto delete(@PathVariable Long id) throws Throwable {
        return bailService.delete(id);
    }
}
