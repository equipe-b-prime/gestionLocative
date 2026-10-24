package com.gestionLocative.gestionLocative.dto;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * DTO représentant le revenu total d'un immeuble pour un mois donné.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RevenuImmeubleDto {
    private Long immeubleId;
    private String immeubleNom;
    private String mois;
    private BigDecimal totalRevenu;
    private List<AppartementRevenuDto> details;
    private double tauxOccupation;
}
