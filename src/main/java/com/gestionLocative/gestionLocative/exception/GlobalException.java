package com.gestionLocative.gestionLocative.exception;

import com.gestionLocative.gestionLocative.dto.AbstractClean;
import com.gestionLocative.gestionLocative.dto.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.*;

@RestControllerAdvice
public class GlobalException {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Response<String>> handleNotFoundException(NotFoundException ex) {
        Response<String> response = new Response<>(List.of(ex.getMessage()));
        response.setHasErreur(true);
        response.setNombreTotalElements(1L);
        response.setCode(HttpStatus.NOT_FOUND.value());
        response.setMessage("Ressource non trouvée");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

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
        response.setMessage("Le paramètre '" + ex.getName() + "' doit être de type " + typeAttendu);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DoublonRessourceException.class)
    public ResponseEntity<Response<String>> handleDoublonRessource(DoublonRessourceException ex) {
        Response<String> response = new Response<>(List.of(ex.getMessage()));
        response.setHasErreur(true);
        response.setNombreTotalElements(1L);
        response.setCode(HttpStatus.CONFLICT.value());
        response.setMessage("Doublon de ressource");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(RessourceUtiliseeException.class)
    public ResponseEntity<Response<String>> handleRessourceUtilisee(RessourceUtiliseeException ex) {
        Response<String> response = new Response<>(List.of(ex.getMessage()));
        response.setHasErreur(true);
        response.setNombreTotalElements(1L);
        response.setCode(HttpStatus.CONFLICT.value());
        response.setMessage("Ressource utilisée");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Response<String>> handleIllegalArgument(IllegalArgumentException ex) {
        Response<String> response = new Response<>(List.of(ex.getMessage()));
        response.setHasErreur(true);
        response.setNombreTotalElements(1L);
        response.setCode(HttpStatus.BAD_REQUEST.value());
        response.setMessage("Règle métier non respectée");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Response<String>> handleIllegalStateException(IllegalStateException ex) {
        Response<String> response = new Response<>(List.of(ex.getMessage()));
        response.setHasErreur(true);
        response.setNombreTotalElements(1L);
        response.setCode(HttpStatus.CONFLICT.value());
        response.setMessage("Opération impossible");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

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