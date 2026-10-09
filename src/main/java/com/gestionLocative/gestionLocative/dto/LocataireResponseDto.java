package com.gestionLocative.gestionLocative.dto;

import lombok.*;

/**
 * Réponse contenant les données d'un locataire.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LocataireResponseDto implements ResponseDto {
    private Long id;
    private String nom;
    private String prenom;
    private String numero;
    private String email;
}