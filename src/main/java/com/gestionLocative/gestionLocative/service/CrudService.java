package com.gestionLocative.gestionLocative.service;

import com.gestionLocative.gestionLocative.dto.RequestDto;
import com.gestionLocative.gestionLocative.dto.ResponseDto;
import com.gestionLocative.gestionLocative.exception.NotFoundException;

/**
 * Contrat définissant les opérations d'écriture (création, modification)
 * et les règles de transformation (mapping) pour les services de l'application.
 * Chaque service spécifique (ex: ImmeubleService, LocataireService) doit implémenter
 * ces méthodes pour garantir une architecture uniforme.
 */
public interface CrudService {

    /**
     * Message d'erreur par défaut lorsqu'une entité recherchée est introuvable en base.
     */
    String NOT_FOUND_MESSAGE = "La ressource demandée est introuvable.";

    /**
     * Nombre d'éléments retournés par défaut lorsqu'une taille de page invalide est transmise.
     */
    int TAILLE_DEFAUT = 10;

    /**
     * Nombre maximal d'éléments qu'une requête paginée est autorisée à retourner.
     */
    int TAILLE_MAX = 100;

    /**
     * Crée une nouvelle ressource en base de données à partir des données reçues.
     *
     * @param request l'objet {@link RequestDto} contenant les informations de création
     * @return un objet {@link ResponseDto} représentant la ressource sauvegardée
     */
    ResponseDto create(RequestDto request);

    /**
     * Met à jour une ressource existante en base de données.
     *
     * @param id l'identifiant unique de la ressource à modifier
     * @param request l'objet {@link RequestDto} contenant les  informations
     * @return un objet {@link ResponseDto} représentant la ressource mise à jour
     * @throws NotFoundException si la ressource correspondante n'est pas trouvée
     */
    ResponseDto update(Long id, RequestDto request);

    /**
     * Transforme une entité issue de la base de données (Object) en un DTO de réponse.
     * Cette méthode centralise la logique d'exposition des données vers le client.
     *
     * @param entity l'entité brute récupérée depuis la base de données
     * @return un objet {@link ResponseDto} prêt à être envoyé au client HTTP
     */
    ResponseDto toDto(Object entity);

}
