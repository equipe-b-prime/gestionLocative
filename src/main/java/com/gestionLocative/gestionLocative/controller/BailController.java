package com.gestionLocative.gestionLocative.controller;

import com.gestionLocative.gestionLocative.service.BailService;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur REST exposant les endpoints de gestion des baux.
 */
@RestController
@RequestMapping("/baux")
public class BailController extends CrudController {
    public BailController(BailService bailService) {
        super(bailService);
    }
}
