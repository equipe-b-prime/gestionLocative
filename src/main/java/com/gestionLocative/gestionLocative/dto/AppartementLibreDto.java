package com.gestionLocative.gestionLocative.dto;

import java.math.BigDecimal;
import lombok.*;


@Getter @Setter @Builder @AllArgsConstructor @NoArgsConstructor
public class AppartementLibreDto {
    private Long id;
    private String nom;
    private BigDecimal surface;
    private int nbPieces;
    private int etage;
    /** Loyer du dernier bail connu (révisions comprises), null si l'appartement n'a jamais été loué. */
    private BigDecimal dernierLoyer;
}
