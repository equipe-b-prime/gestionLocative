package com.gestionLocative.gestionLocative.dto;

import lombok.*;

import java.math.BigDecimal;

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
    private boolean etageOccupe;
    private ProprietaireResponseDto proprietaire;
    private ImmeubleResponseDto immeuble;
}