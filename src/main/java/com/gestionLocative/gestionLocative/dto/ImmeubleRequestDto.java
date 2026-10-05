package com.gestionLocative.gestionLocative.dto;

import lombok.*;

/**
 * Requête de création ou de modification d'un immeuble.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImmeubleRequestDto {
    private Long id;
    private String nom;
    private String adresse;
    private Integer nbEtages;
}