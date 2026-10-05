package com.gestionLocative.gestionLocative.dto;

import lombok.*;

/**
 * Requête de création ou de modification d'un locataire.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LocataireRequestDto {
    private Long id;
    private String nom;
    private String prenom;
    private String numero;
    private String email;
}