package com.gestionLocative.gestionLocative.exception;

import com.gestionLocative.gestionLocative.dto.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Gestion centralisée des exceptions de l'application.
 * Toutes les erreurs sortent dans la même enveloppe {@link Response} que les succès,
 * avec le drapeau {@code hasErreur} à true.
 */
@RestControllerAdvice
public class GlobalException {

    /**
     * Gère les erreurs de validation des requêtes annotées {@code @Valid}.
     *
     * @param ex l'exception de validation levée par Spring MVC
     * @return ResponseEntity<Response<Map<String,String>>> le détail des erreurs par champ
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Response<Map<String, String>>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        List<Map<String, String>> erreurs = new ArrayList<>();
        for (FieldError error : ex.getFieldErrors()) {
            Map<String, String> detail = new HashMap<>();
            detail.put("champ", error.getField());
            detail.put("message", error.getDefaultMessage());
            erreurs.add(detail);
        }
        Response<Map<String, String>> response = new Response<>(erreurs);
        response.setHasErreur(true);
        response.setNombreTotalElements((long) erreurs.size());
        response.setCode(HttpStatus.BAD_REQUEST.value());
        response.setMessage("Erreur de validation");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Gère les ressources introuvables en base de données.
     *
     * @param ex l'exception signalant l'absence de la ressource
     * @return ResponseEntity<Response> le message d'erreur encapsulé
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Response> handleNotFoundException(NotFoundException ex) {
        Response<String> response = new Response<>(List.of(ex.getMessage()));
        response.setHasErreur(true);
        response.setNombreTotalElements(1L);
        response.setCode(HttpStatus.NOT_FOUND.value());
        response.setMessage("L'opération a échoué");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * Gère les paramètres de requête dont le type ne correspond pas à celui attendu.
     *
     * @param ex l'exception signalant une incompatibilité de type sur un paramètre
     * @return ResponseEntity<Response<List<String>>> le message d'erreur encapsulé
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Response<List<String>>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String typeAttendu = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "inconnu";
        switch (typeAttendu) {
            case "int", "Long", "Integer" -> typeAttendu = "entier";
            case "boolean", "Boolean" -> typeAttendu = "booléen";
            case "String" -> typeAttendu = "chaîne de caractères";
            default -> typeAttendu = "inconnu";
        }
        Response<List<String>> response = new Response<>(new ArrayList<>());
        response.setData(null);
        response.setHasErreur(true);
        response.setNombreTotalElements(0L);
        response.setCode(HttpStatus.BAD_REQUEST.value());
        response.setMessage("Le parametre '" + ex.getName() + "' doit etre de type " + typeAttendu);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Gère les violations de contrainte d'unicité métier.
     *
     * @param ex l'exception signalant un doublon de ressource
     * @return ResponseEntity<Response<String>> le message d'erreur encapsulé
     */
    @ExceptionHandler(DoublonRessourceException.class)
    public ResponseEntity<Response<String>> handleDoublonRessource(DoublonRessourceException ex) {
        Response<String> response = new Response<>(List.of(ex.getMessage()));
        response.setHasErreur(true);
        response.setNombreTotalElements(1L);
        response.setCode(HttpStatus.CONFLICT.value());
        response.setMessage("Doublon de ressource");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    /**
     * Gère les suppressions refusées car la ressource est encore référencée.
     *
     * @param ex l'exception signalant une ressource encore utilisée
     * @return ResponseEntity<Response<String>> le message d'erreur encapsulé
     */
    @ExceptionHandler(RessourceUtiliseeException.class)
    public ResponseEntity<Response<String>> handleRessourceUtilisee(RessourceUtiliseeException ex) {
        Response<String> response = new Response<>(List.of(ex.getMessage()));
        response.setHasErreur(true);
        response.setNombreTotalElements(1L);
        response.setCode(HttpStatus.CONFLICT.value());
        response.setMessage("Ressource utilisée");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    /**
     * Gère les règles métier violées (arguments invalides).
     *
     * @param ex l'exception signalant une règle métier non respectée
     * @return ResponseEntity<Response<String>> le message d'erreur encapsulé
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Response<String>> handleIllegalArgument(IllegalArgumentException ex) {
        Response<String> response = new Response<>(List.of(ex.getMessage()));
        response.setHasErreur(true);
        response.setNombreTotalElements(1L);
        response.setCode(HttpStatus.CONFLICT.value());
        response.setMessage("Règle métier non respectée");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    /**
     * Gère les requêtes invalides (paramètres manquants ou incohérents).
     *
     * @param ex l'exception signalant une requête invalide
     * @return ResponseEntity<Response<String>> le message d'erreur encapsulé
     */
    @ExceptionHandler(RequeteInvalideException.class)
    public ResponseEntity<Response<String>> handleRequeteInvalide(RequeteInvalideException ex) {
        Response<String> response = new Response<>(ex.getMessage());
        response.setHasErreur(true);
        response.setNombreTotalElements(1L);
        response.setCode(HttpStatus.BAD_REQUEST.value());
        response.setMessage("Requête invalide");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
