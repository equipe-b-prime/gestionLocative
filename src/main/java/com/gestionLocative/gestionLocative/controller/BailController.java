package com.gestionLocative.gestionLocative.controller;

import com.gestionLocative.gestionLocative.dto.BailRequestDto;
import com.gestionLocative.gestionLocative.dto.BailResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import com.gestionLocative.gestionLocative.service.BailService;

/**
 * Contrôleur REST exposant les endpoints de gestion des baux.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/baux")
public class BailController {
    private final BailService bailService;

    /**
     * Récupère la liste de tous les baux.
     *
     * @return List BailResponseDto La liste des baux récupérés.
     */
    @GetMapping
    public List<BailResponseDto> getAllBails() {
        return bailService.getAllBails();
    }

    /**
     * Récupère un bail à partir de son identifiant.
     *
     * @param id L'identifiant du bail à récupérer.
     * @return BailResponseDto Les détails du bail récupéré.
     */
    @GetMapping("/{id}")
    public BailResponseDto getBailById(@PathVariable Long id) {
        return bailService.getBailById(id);
    }

    /**
     * Supprime un bail à partir de son identifiant.
     *
     * @param id L'identifiant du bail à supprimer.
     * @return BailResponseDto Les détails du bail supprimé.
     */
    @DeleteMapping("/{id}")
    public BailResponseDto deleteById(@PathVariable Long id) {
        return bailService.deleteById(id);
    }

    /**
     * Met à jour un bail à partir de son identifiant et des nouvelles données.
     *
     * @param id L'identifiant du bail à mettre à jour.
     * @param bailRequest Les nouvelles données du bail.
     * @return BailResponseDto Les détails du bail mis à jour.
     */
    @PutMapping("/{id}")
    public BailResponseDto updateById(@PathVariable Long id, @RequestBody BailRequestDto bailRequest) {
        return bailService.updateById(id, bailRequest);
    }

    /**
     * Crée un nouveau bail à partir des données fournies.
     *
     * @param bailRequest Les données du bail à créer.
     * @return BailResponseDto Les détails du bail créé.
     */
    @PostMapping
    public BailResponseDto createById(@RequestBody BailRequestDto bailRequest) {
        return bailService.createById(bailRequest);
    }
}