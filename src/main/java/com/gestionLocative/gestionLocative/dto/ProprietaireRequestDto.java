package com.gestionLocative.gestionLocative.dto;

import lombok.*;

/**
 * Requête de création ou de modification d'un propriétaire.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProprietaireRequestDto {
    private Long id;
    private String nom;
    private String prenom;
    private String numero;
    private String adresse;
}