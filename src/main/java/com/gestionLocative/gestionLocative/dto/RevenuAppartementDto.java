package com.gestionLocative.gestionLocative.dto;

import java.math.BigDecimal;
import lombok.*;


@Getter @Setter @Builder @AllArgsConstructor @NoArgsConstructor
public class RevenuAppartementDto {
    private Long appartementId;
    private String nom;
    private long joursOccupes;
    private BigDecimal revenu;
}
