package com.gestionLocative.gestionLocative.dto;

/**
 * Interface de base commune à tous les DTO de requête.
 * Elle sert de marqueur de type : chaque contrôleur déclare déjà le DTO concret
 * qu'il attend, aucune désérialisation polymorphe n'est donc nécessaire.
 * Le polymorphisme par {@code @JsonTypeInfo(use = DEDUCTION)} a été retiré :
 * il rend toutes les écritures indisponibles dès que deux DTO partagent la même
 * forme (ce qui était le cas de {@link LocataireRequestDto} et
 * {@link ProprietaireRequestDto}, tous deux en {@code nom, prenom, numero, email}).
 */
public interface RequestDto { }
