package com.gestionLocative.gestionLocative.controller;

import com.gestionLocative.gestionLocative.dto.BailResponseDto;
import com.gestionLocative.gestionLocative.service.LocataireService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST exposant les endpoints de gestion des locataires.
 * Le CRUD standard est hérité de {@link CrudController} ; seules les
 * requêtes métier propres aux locataires sont déclarées ici.
 */
@RestController
@RequestMapping("/locataires")
public class LocataireController extends CrudController {
    private final LocataireService locataireService;

    public LocataireController(LocataireService locataireService) {
        super(locataireService);
        this.locataireService = locataireService;
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
