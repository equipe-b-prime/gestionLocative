package com.gestionLocative.gestionLocative.dto;

import com.gestionLocative.gestionLocative.dto.BailResponseDto;
import lombok.*;

import java.math.BigDecimal;

/**
 * Réponse contenant les données d'une quittance de loyer,
 * avec les informations du bail associé.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuittanceResponseDto {
    private Long id;
    private Integer mois;
    private Integer annee;
    private BigDecimal montantLoyerFacture;
    private BigDecimal montantChargeFacture;
    private BailResponseDto bail;
}