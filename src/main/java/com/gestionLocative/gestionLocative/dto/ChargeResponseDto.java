package com.gestionLocative.gestionLocative.dto;

import com.gestionLocative.gestionLocative.dto.ImmeubleResponseDto;
import lombok.*;

import java.math.BigDecimal;

/**
 * Réponse contenant les données d'une charge d'immeuble,
 * avec les informations de l'immeuble associé.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChargeResponseDto {
    private Long id;
    private int annee;
    private BigDecimal montant;
    private ImmeubleResponseDto immeuble;
}