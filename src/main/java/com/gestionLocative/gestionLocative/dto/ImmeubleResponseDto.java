package com.gestionLocative.gestionLocative.dto;

import lombok.*;

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
    private Long proprietaireId;
    private ProprietaireResponseDto proprietaire;
    private long nbAppartements;
}