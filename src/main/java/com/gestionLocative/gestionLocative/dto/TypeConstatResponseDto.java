package com.gestionLocative.gestionLocative.dto;

import lombok.*;

/**
 * Réponse contenant les données d'un type de constat.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TypeConstatResponseDto {
    private Long id;
    private String libelle;
}