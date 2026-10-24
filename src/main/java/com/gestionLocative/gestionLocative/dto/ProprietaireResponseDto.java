package com.gestionLocative.gestionLocative.dto;

import lombok.*;

/**
 * DTO de réponse pour un propriétaire.
 */
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
}
