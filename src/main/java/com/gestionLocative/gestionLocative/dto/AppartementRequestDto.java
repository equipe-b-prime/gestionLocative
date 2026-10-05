package com.gestionLocative.gestionLocative.dto;

import lombok.*;

import java.math.BigDecimal;

/**
 * Requête de création ou de modification d'un appartement.
 * Les relations sont transmises via les identifiants du propriétaire et de l'immeuble.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppartementRequestDto {
    private String nom;
    private BigDecimal surface;
    private int nbPieces;
    private int etage;
    private Long proprietaireId;
    private Long immeubleId;
}