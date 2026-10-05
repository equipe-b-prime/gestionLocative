package com.gestionLocative.gestionLocative.controller;

import com.gestionLocative.gestionLocative.dto.ProprietaireRequestDto;
import com.gestionLocative.gestionLocative.dto.ProprietaireResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import com.gestionLocative.gestionLocative.service.ProprietaireService;

/**
 * Contrôleur REST exposant les endpoints de gestion des propriétaires.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/proprietaires")
public class ProprietaireController {
    private final ProprietaireService proprietaireService;

    /**
     * Récupère la liste de tous les propriétaires.
     *
     * @return List<ProprietaireResponseDto> La liste des propriétaires récupérés.
     */
    @GetMapping
    public List<ProprietaireResponseDto> getAllProprietaires() {
        return proprietaireService.getAllProprietaires();
    }

    /**
     * Récupère un propriétaire à partir de son identifiant.
     *
     * @param id L'identifiant du propriétaire à récupérer.
     * @return ProprietaireResponseDto Les détails du propriétaire récupéré.
     */
    @GetMapping("/{id}")
    public ProprietaireResponseDto getProprietaireById(@PathVariable Long id) {
        return proprietaireService.getProprietaireById(id);
    }

    /**
     * Supprime un propriétaire à partir de son identifiant.
     *
     * @param id L'identifiant du propriétaire à supprimer.
     * @return ProprietaireResponseDto Les détails du propriétaire supprimé.
     */
    @DeleteMapping("/{id}")
    public ProprietaireResponseDto deleteById(@PathVariable Long id) {
        return proprietaireService.deleteById(id);
    }

    /**
     * Met à jour un propriétaire à partir de son identifiant et des nouvelles données.
     *
     * @param id L'identifiant du propriétaire à mettre à jour.
     * @param proprietaireRequest Les nouvelles données du propriétaire.
     * @return ProprietaireResponseDto Les détails du propriétaire mis à jour.
     */
    @PutMapping("/{id}")
    public ProprietaireResponseDto updateById(@PathVariable Long id, @RequestBody ProprietaireRequestDto proprietaireRequest) {
        return proprietaireService.updateById(id, proprietaireRequest);
    }

    /**
     * Crée un nouveau propriétaire à partir des données fournies.
     *
     * @param proprietaireRequest Les données du propriétaire à créer.
     * @return ProprietaireResponseDto Les détails du propriétaire créé.
     */
    @PostMapping
    public ProprietaireResponseDto createById(@RequestBody ProprietaireRequestDto proprietaireRequest) {
        return proprietaireService.createById(proprietaireRequest);
    }
}