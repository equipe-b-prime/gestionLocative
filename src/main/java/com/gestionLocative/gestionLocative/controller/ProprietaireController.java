package com.gestionLocative.gestionLocative.controller;

import com.gestionLocative.gestionLocative.service.ProprietaireService;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur REST exposant les endpoints de gestion des propriétaires.
 */
@RestController
@RequestMapping("/proprietaires")
public class ProprietaireController extends CrudController {
    public ProprietaireController(ProprietaireService proprietaireService) {
        super(proprietaireService);
    }
}
