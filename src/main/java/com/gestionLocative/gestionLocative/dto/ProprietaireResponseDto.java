package com.gestionLocative.gestionLocative.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProprietaireResponseDto implements ResponseDto {
    private Long id;
    private String nom;
    private String prenom;
    private String numero;
    private String email;
    private String adresse;
}