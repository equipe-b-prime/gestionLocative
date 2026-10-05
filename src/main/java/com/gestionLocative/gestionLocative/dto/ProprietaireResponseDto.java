package com.gestionLocative.gestionLocative.dto;

import lombok.*;

/**
 * Réponse contenant les données d'un propriétaire.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProprietaireResponseDto {
    private Long id;
    private String nom;
    private String prenom;
    private String numero;
    private String adresse;
}