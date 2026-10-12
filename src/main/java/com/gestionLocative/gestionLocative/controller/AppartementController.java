package com.gestionLocative.gestionLocative.controller;

import com.gestionLocative.gestionLocative.dto.AppartementRequestDto;
import com.gestionLocative.gestionLocative.dto.ResponseDto;
import com.gestionLocative.gestionLocative.service.AppartementService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST pour la gestion des appartements.
 */
@RestController
@RequestMapping("/appartements")
public class AppartementController {

    private final AppartementService appartementService;

    /**
     * Constructeur injectant le service des appartements.
     *
     * @param appartementService le service des appartements
     */
    public AppartementController(AppartementService appartementService) {
        this.appartementService = appartementService;
    }

    /**
     * Récupère la liste de tous les appartements.
     *
     * @return la liste des appartements
     */
    @GetMapping
    public List<ResponseDto> getAll() {
        return appartementService.getAll();
    }

    /**
     * Récupère l'appartement à partir de son identifiant.
     *
     * @param id l'identifiant de l'appartement
     * @return l'appartement trouvé
     * @throws Throwable si l'appartement n'est pas trouvé
     */
    @GetMapping("/{id}")
    public ResponseDto getById(@PathVariable Long id) throws Throwable {
        return appartementService.getById(id);
    }

    /**
     * Crée un appartement.
     *
     * @param appartementRequest les données de la requête
     * @return l'appartement créé
     */
    @PostMapping
    public ResponseDto create(@RequestBody AppartementRequestDto appartementRequest) {
        return appartementService.create(appartementRequest);
    }

    /**
     * Modifie l'appartement.
     *
     * @param id l'identifiant de l'appartement à modifier
     * @param appartementRequest les données de la requête
     * @return l'appartement modifié
     */
    @PutMapping("/{id}")
    public ResponseDto update(@PathVariable Long id, @RequestBody AppartementRequestDto appartementRequest) {
        return appartementService.update(id, appartementRequest);
    }

    /**
     * Supprime l'appartement à partir de son identifiant.
     *
     * @param id l'identifiant de l'appartement
     * @return l'appartement supprimé
     * @throws Throwable si l'appartement n'est pas trouvé
     */
    @DeleteMapping("/{id}")
    public ResponseDto delete(@PathVariable Long id) throws Throwable {
        return appartementService.delete(id);
    }
}
