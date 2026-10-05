package com.gestionLocative.gestionLocative.controller;

import com.gestionLocative.gestionLocative.dto.AppartementRequestDto;
import com.gestionLocative.gestionLocative.dto.AppartementResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import com.gestionLocative.gestionLocative.service.AppartementService;

/**
 * Contrôleur REST exposant les endpoints de gestion des appartements.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/appartements")
public class AppartementController {
    private final AppartementService appartementService;

    /**
     * Récupère la liste de tous les appartements.
     *
     * @return List<AppartementResponseDto> La liste des appartements récupérés.
     */
    @GetMapping
    public List<AppartementResponseDto> getAllAppartements() {
        return appartementService.getAllAppartements();
    }

    /**
     * Récupère un appartement à partir de son identifiant.
     *
     * @param id L'identifiant de l'appartement à récupérer.
     * @return AppartementResponseDto Les détails de l'appartement récupéré.
     */
    @GetMapping("/{id}")
    public AppartementResponseDto getAppartementById(@PathVariable Long id) {
        return appartementService.getAppartementById(id);
    }

    /**
     * Supprime un appartement à partir de son identifiant.
     *
     * @param id L'identifiant de l'appartement à supprimer.
     * @return AppartementResponseDto Les détails de l'appartement supprimé.
     */
    @DeleteMapping("/{id}")
    public AppartementResponseDto deleteById(@PathVariable Long id) {
        return appartementService.deleteById(id);
    }

    /**
     * Met à jour un appartement à partir de son identifiant et des nouvelles données.
     *
     * @param id L'identifiant de l'appartement à mettre à jour.
     * @param appartementRequest Les nouvelles données de l'appartement.
     * @return AppartementResponseDto Les détails de l'appartement mis à jour.
     */
    @PutMapping("/{id}")
    public AppartementResponseDto updateById(@PathVariable Long id, @RequestBody AppartementRequestDto appartementRequest) {
        return appartementService.updateById(id, appartementRequest);
    }

    /**
     * Crée un nouvel appartement à partir des données fournies.
     *
     * @param appartementRequest Les données de l'appartement à créer.
     * @return AppartementResponseDto Les détails de l'appartement créé.
     */
    @PostMapping
    public AppartementResponseDto createById(@RequestBody AppartementRequestDto appartementRequest) {
        return appartementService.createById(appartementRequest);
    }
}