package com.gestionLocative.gestionLocative.exception;

/**
 * Exception levée lorsqu'une suppression est refusée car la ressource est
 * encore référencée par une autre ressource (dépendance en base de données).
 */
public class RessourceUtiliseeException extends RuntimeException {

    /**
     * Constructeur avec message d'erreur.
     *
     * @param message le message décrivant la dépendance bloquante
     */
    public RessourceUtiliseeException(String message) {
        super(message);
    }

    /**
     * Constructeur construisant automatiquement le message à partir de la ressource,
     * de son identifiant et de la ressource qui l'utilise.
     *
     * @param ressource le nom de la ressource à supprimer (ex : "Immeuble")
     * @param id l'identifiant de la ressource à supprimer
     * @param dependance le nom de la ressource qui l'utilise (ex : "Appartement")
     */
    public RessourceUtiliseeException(String ressource, long id, String dependance) {
        super("Impossible de supprimer la ressource " + ressource + " (ID: " + id + ") , elle"
                + " est actuellement utilisée par " + dependance);
    }
}
