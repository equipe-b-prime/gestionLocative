package com.gestionLocative.gestionLocative.dto;

import java.math.BigDecimal;
import java.util.List;
import lombok.*;


@Getter @Setter @Builder @AllArgsConstructor @NoArgsConstructor
public class RevenuImmeubleDto implements ResponseDto {
    private Long immeubleId;
    private String mois;
    private int nbAppartements;
    private BigDecimal revenuTotal;
    /** Jours-appartements occupés / jours-appartements du mois, en pourcentage. */
    private BigDecimal tauxOccupation;
    private List<RevenuAppartementDto> details;
}
