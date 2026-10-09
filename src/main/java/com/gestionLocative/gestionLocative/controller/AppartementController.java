package com.gestionLocative.gestionLocative.controller;

import com.gestionLocative.gestionLocative.service.AppartementService;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur REST exposant les endpoints de gestion des appartements.
 */
@RestController
@RequestMapping("/appartements")
public class AppartementController extends CrudController {
    public AppartementController(AppartementService appartementService) {
        super(appartementService);
    }
}
