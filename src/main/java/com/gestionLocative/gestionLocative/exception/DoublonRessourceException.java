package com.gestionLocative.gestionLocative.exception;

/**
 * Exception levée lorsqu'une ressource enfreint une contrainte d'unicité métier
 * (par exemple deux salles de même nom dans la même ville, ou un email déjà utilisé).
 */
public class DoublonRessourceException extends RuntimeException {

    /**
     * Constructeur avec message d'erreur.
     *
     * @param message le message décrivant le doublon détecté
     */
    public DoublonRessourceException(String message) {
        super(message);
    }
}
