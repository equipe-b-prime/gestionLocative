package com.gestionLocative.gestionLocative.controller;

import com.gestionLocative.gestionLocative.dto.AppartementResponseDto;
import com.gestionLocative.gestionLocative.dto.ImmeubleRequestDto;
import com.gestionLocative.gestionLocative.dto.ImmeubleResponseDto;
import com.gestionLocative.gestionLocative.dto.ProprietaireResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import com.gestionLocative.gestionLocative.service.ImmeubleService;

/**
 * Contrôleur REST exposant les endpoints de gestion des immeubles.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/immeubles")
public class ImmeubleController {
    private final ImmeubleService immeubleService;

    /**
     * Récupère la liste de tous les immeubles.
     *
     * @return List<ImmeubleResponseDto> La liste des immeubles récupérés.
     */
    @GetMapping
    public List<ImmeubleResponseDto> getAllImmeubles() {
        return immeubleService.getAllImmeubles();
    }

    /**
     * Récupère un immeuble à partir de son identifiant.
     *
     * @param id L'identifiant de l'immeuble à récupérer.
     * @return ImmeubleResponseDto Les détails de l'immeuble récupéré.
     */
    @GetMapping("/{id}")
    public ImmeubleResponseDto getImmeubleById(@PathVariable Long id) {
        return immeubleService.getImmeubleById(id);
    }

    /**
     * Supprime un immeuble à partir de son identifiant.
     *
     * @param id L'identifiant de l'immeuble à supprimer.
     * @return ImmeubleResponseDto Les détails de l'immeuble supprimé.
     */
    @DeleteMapping("/{id}")
    public ImmeubleResponseDto deleteById(@PathVariable Long id) {
        return immeubleService.deleteById(id);
    }

    /**
     * Met à jour un immeuble à partir de son identifiant et des nouvelles données.
     *
     * @param id L'identifiant de l'immeuble à mettre à jour.
     * @param immeubleRequest Les nouvelles données de l'immeuble.
     * @return ImmeubleResponseDto Les détails de l'immeuble mis à jour.
     */
    @PutMapping("/{id}")
    public ImmeubleResponseDto updateById(@PathVariable Long id, @RequestBody ImmeubleRequestDto immeubleRequest) {
        return immeubleService.updateById(id, immeubleRequest);
    }

    /**
     * Crée un nouvel immeuble à partir des données fournies.
     *
     * @param immeubleRequest Les données de l'immeuble à créer.
     * @return ImmeubleResponseDto Les détails de l'immeuble créé.
     */
    @PostMapping
    public ImmeubleResponseDto createById(@RequestBody ImmeubleRequestDto immeubleRequest) {
        return immeubleService.createById(immeubleRequest);
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