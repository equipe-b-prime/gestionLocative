package com.gestionLocative.gestionLocative.exception;

public class RessourceUtiliseeException extends RuntimeException {
    public RessourceUtiliseeException(String message) {
        super(message);
    }
    public RessourceUtiliseeException(String ressource, long id, String dependance) {
        super("Impossible de supprimer la ressource " + ressource + " (ID: " + id + "), elle est actuellement utilisée par " + dependance);
    }
}