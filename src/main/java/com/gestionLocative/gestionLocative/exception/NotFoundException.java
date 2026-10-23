package com.gestionLocative.gestionLocative.exception;

/**
 * Exception levée lorsqu'une ressource demandée est introuvable en base de données.
 */
public class NotFoundException extends RuntimeException {

    /**
     * Constructeur avec le message décrivant la ressource introuvable.
     *
     * @param message le message d'erreur renvoyé au client
     */
    public NotFoundException(String message) {
        super(message);
    }
}
