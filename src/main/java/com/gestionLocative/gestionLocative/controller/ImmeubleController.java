package com.gestionLocative.gestionLocative.controller;

import com.gestionLocative.gestionLocative.dto.AppartementResponseDto;
import com.gestionLocative.gestionLocative.dto.ProprietaireResponseDto;
import com.gestionLocative.gestionLocative.service.ImmeubleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST exposant les endpoints de gestion des immeubles.
 * Le CRUD standard est hérité de {@link CrudController} ; seules les
 * requêtes métier propres aux immeubles sont déclarées ici.
 */
@RestController
@RequestMapping("/immeubles")
public class ImmeubleController extends CrudController {
    private final ImmeubleService immeubleService;

    public ImmeubleController(ImmeubleService immeubleService) {
        super(immeubleService);
        this.immeubleService = immeubleService;
    }

    /**
     * Récupère les appartements d'un immeuble.
     *
     * @param id L'identifiant de l'immeuble.
     * @return List<AppartementResponseDto> Les appartements de l'immeuble.
     */
    @GetMapping("/{id}/appartements")
    public List<AppartementResponseDto> getAppartementsByImmeuble(@PathVariable Long id) {
        return immeubleService.getAppartementsByImmeuble(id);
    }

    /**
     * Récupère les propriétaires d'un immeuble (déduits via les appartements).
     *
     * @param id L'identifiant de l'immeuble.
     * @return List<ProprietaireResponseDto> Les propriétaires de l'immeuble.
     */
    @GetMapping("/{id}/proprietaires")
    public List<ProprietaireResponseDto> getProprietairesByImmeuble(@PathVariable Long id) {
        return immeubleService.getProprietairesByImmeuble(id);
    }
}
