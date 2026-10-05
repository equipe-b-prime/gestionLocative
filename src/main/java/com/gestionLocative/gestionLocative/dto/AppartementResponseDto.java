package com.gestionLocative.gestionLocative.dto;

import com.gestionLocative.gestionLocative.dto.ImmeubleResponseDto;
import com.gestionLocative.gestionLocative.dto.ProprietaireResponseDto;
import lombok.*;

import java.math.BigDecimal;

/**
 * Réponse contenant les données d'un appartement,
 * avec les informations du propriétaire et de l'immeuble associés.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppartementResponseDto {
    private Long id;
    private String nom;
    private BigDecimal surface;
    private int nbPieces;
    private int etage;
    private ProprietaireResponseDto proprietaire;
    private ImmeubleResponseDto immeuble;
}