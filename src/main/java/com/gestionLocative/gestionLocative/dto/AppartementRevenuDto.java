package com.gestionLocative.gestionLocative.dto;

import lombok.*;
import java.math.BigDecimal;

/**
 * DTO représentant la contribution en revenu d'un appartement pour un mois donné.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppartementRevenuDto {
    private Long appartementId;
    private String appartementNom;
    private BigDecimal surface;
    private BigDecimal loyerMensuel;
    private long joursBail;
    private long joursMois;
    private BigDecimal apportPartiel;
}
