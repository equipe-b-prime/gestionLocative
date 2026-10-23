package com.gestionLocative.gestionLocative.dto;

import lombok.*;

/**
 * Réponse contenant les données d'un immeuble,
 * avec son propriétaire et le nombre d'appartements qu'il contient.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImmeubleResponseDto implements ResponseDto {
    private Long id;
    private String nom;
    private String adresse;
    private Integer nbEtages;
    private ProprietaireResponseDto proprietaire;
    private Long nbAppartements;
}
