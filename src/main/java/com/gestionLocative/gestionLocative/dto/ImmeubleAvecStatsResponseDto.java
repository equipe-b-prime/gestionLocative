package com.gestionLocative.gestionLocative.dto;

import lombok.*;

/**
 * Réponse contenant les données d'un immeuble,
 * avec le nombre d'appartements et le nombre de baux en cours.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImmeubleAvecStatsResponseDto implements ResponseDto {
    private Long id;
    private String nom;
    private String adresse;
    private Integer nbEtages;
    private ProprietaireResponseDto proprietaire;
    private int nombreAppartements;
    private int nombreBauxEnCours;
}
