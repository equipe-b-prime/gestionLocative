package com.gestionLocative.gestionLocative.controller;

import com.gestionLocative.gestionLocative.dto.BailResponseDto;
import com.gestionLocative.gestionLocative.dto.LocataireRequestDto;
import com.gestionLocative.gestionLocative.dto.LocataireResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import com.gestionLocative.gestionLocative.service.LocataireService;

/**
 * Contrôleur REST exposant les endpoints de gestion des locataires.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/locataires")
public class LocataireController {
    private final LocataireService locataireService;

    /**
     * Récupère la liste de tous les locataires.
     *
     * @return List<LocataireResponseDto> La liste des locataires récupérés.
     */
    @GetMapping
    public List<LocataireResponseDto> getAllLocataires() {
        return locataireService.getAllLocataires();
    }

    /**
     * Récupère un locataire à partir de son identifiant.
     *
     * @param id L'identifiant du locataire à récupérer.
     * @return LocataireResponseDto Les détails du locataire récupéré.
     */
    @GetMapping("/{id}")
    public LocataireResponseDto getLocataireById(@PathVariable Long id) {
        return locataireService.getLocataireById(id);
    }

    /**
     * Supprime un locataire à partir de son identifiant.
     *
     * @param id L'identifiant du locataire à supprimer.
     * @return LocataireResponseDto Les détails du locataire supprimé.
     */
    @DeleteMapping("/{id}")
    public LocataireResponseDto deleteById(@PathVariable Long id) {
        return locataireService.deleteById(id);
    }

    /**
     * Met à jour un locataire à partir de son identifiant et des nouvelles données.
     *
     * @param id L'identifiant du locataire à mettre à jour.
     * @param locataireRequest Les nouvelles données du locataire.
     * @return LocataireResponseDto Les détails du locataire mis à jour.
     */
    @PutMapping("/{id}")
    public LocataireResponseDto updateById(@PathVariable Long id, @RequestBody LocataireRequestDto locataireRequest) {
        return locataireService.updateById(id, locataireRequest);
    }

    /**
     * Crée un nouveau locataire à partir des données fournies.
     *
     * @param locataireRequest Les données du locataire à créer.
     * @return LocataireResponseDto Les détails du locataire créé.
     */
    @PostMapping
    public LocataireResponseDto createById(@RequestBody LocataireRequestDto locataireRequest) {
        return locataireService.createById(locataireRequest);
    }

    /**
     * Récupère les baux d'un locataire.
     *
     * @param id L'identifiant du locataire.
     * @return List<BailResponseDto> Les baux du locataire.
     */
    @GetMapping("/{id}/baux")
    public List<BailResponseDto> getBauxByLocataire(@PathVariable Long id) {
        return locataireService.getBauxByLocataire(id);
    }
}