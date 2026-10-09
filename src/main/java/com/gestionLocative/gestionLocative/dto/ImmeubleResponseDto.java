package com.gestionLocative.gestionLocative.dto;

import lombok.*;

/**
 * Réponse contenant les données d'un immeuble.
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
}