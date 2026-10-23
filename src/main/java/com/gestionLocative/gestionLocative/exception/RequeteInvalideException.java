package com.gestionLocative.gestionLocative.exception;

/**
 * Exception levée lorsqu'une requête client est invalide
 * (paramètres manquants, incohérents ou hors bornes).
 */
public class RequeteInvalideException extends RuntimeException {

    /**
     * Constructeur avec message d'erreur.
     *
     * @param message le message décrivant l'erreur de requête
     */
    public RequeteInvalideException(String message) {
        super(message);
    }
}
