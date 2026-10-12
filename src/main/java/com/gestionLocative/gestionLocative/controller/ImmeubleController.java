package com.gestionLocative.gestionLocative.controller;

import com.gestionLocative.gestionLocative.dto.AppartementResponseDto;
import com.gestionLocative.gestionLocative.dto.ImmeubleRequestDto;
import com.gestionLocative.gestionLocative.dto.ProprietaireResponseDto;
import com.gestionLocative.gestionLocative.dto.ResponseDto;
import com.gestionLocative.gestionLocative.service.ImmeubleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST pour la gestion des immeubles.
 * Outre le CRUD standard, il expose les appartements et les propriétaires
 * rattachés à un immeuble.
 */
@RestController
@RequestMapping("/immeubles")
public class ImmeubleController {

    private final ImmeubleService immeubleService;

    /**
     * Constructeur injectant le service des immeubles.
     *
     * @param immeubleService le service des immeubles
     */
    public ImmeubleController(ImmeubleService immeubleService) {
        this.immeubleService = immeubleService;
    }

    /**
     * Récupère la liste de toutes les immeubles.
     *
     * @return la liste des immeubles
     */
    @GetMapping
    public List<ResponseDto> getAll() {
        return immeubleService.getAll();
    }

    /**
     * Récupère un immeuble à partir de son identifiant.
     *
     * @param id l'identifiant de l'immeuble
     * @return l'immeuble trouvé
     * @throws Throwable si l'immeuble n'est pas trouvé
     */
    @GetMapping("/{id}")
    public ResponseDto getById(@PathVariable Long id) throws Throwable {
        return immeubleService.getById(id);
    }

    /**
     * Crée un nouvel immeuble.
     *
     * @param immeubleRequest les données de la requête
     * @return l'immeuble créé
     */
    @PostMapping
    public ResponseDto create(@RequestBody ImmeubleRequestDto immeubleRequest) {
        return immeubleService.create(immeubleRequest);
    }

    /**
     * Modifie un immeuble existant.
     *
     * @param id l'identifiant de l'immeuble à modifier
     * @param immeubleRequest les données de la requête
     * @return l'immeuble modifié
     */
    @PutMapping("/{id}")
    public ResponseDto update(@PathVariable Long id, @RequestBody ImmeubleRequestDto immeubleRequest) {
        return immeubleService.update(id, immeubleRequest);
    }

    /**
     * Supprime un immeuble à partir de son identifiant.
     *
     * @param id l'identifiant de l'immeuble
     * @return l'immeuble supprimé
     * @throws Throwable si l'immeuble n'est pas trouvé
     */
    @DeleteMapping("/{id}")
    public ResponseDto delete(@PathVariable Long id) throws Throwable {
        return immeubleService.delete(id);
    }

    /**
     * Récupère les appartements d'un immeuble.
     *
     * @param id L'identifiant de l'immeuble.
     * @return List&lt;AppartementResponseDto&gt; Les appartements de l'immeuble.
     */
    @GetMapping("/{id}/appartements")
    public List<AppartementResponseDto> getAppartementsByImmeuble(@PathVariable Long id) {
        return immeubleService.getAppartementsByImmeuble(id);
    }

    /**
     * Récupère les propriétaires d'un immeuble (déduits via les appartements).
     *
     * @param id L'identifiant de l'immeuble.
     * @return List&lt;ProprietaireResponseDto&gt; Les propriétaires de l'immeuble.
     */
    @GetMapping("/{id}/proprietaires")
    public List<ProprietaireResponseDto> getProprietairesByImmeuble(@PathVariable Long id) {
        return immeubleService.getProprietairesByImmeuble(id);
    }
}
