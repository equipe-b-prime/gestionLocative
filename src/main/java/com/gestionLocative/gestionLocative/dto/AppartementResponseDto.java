package com.gestionLocative.gestionLocative.dto;

import lombok.*;

import java.math.BigDecimal;

/**
 * Réponse contenant les données d'un appartement,
 * avec son état d'occupation (libre ou occupé).
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppartementResponseDto implements ResponseDto {
    private Long id;
    private String nom;
    private BigDecimal surface;
    private int nbPieces;
    private int etage;
    private ImmeubleResponseDto immeuble;
    private boolean etageOccupe;
}
