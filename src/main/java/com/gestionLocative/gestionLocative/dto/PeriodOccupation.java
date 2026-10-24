package com.gestionLocative.gestionLocative.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO représentant une période d'occupation (ou vacance) d'un appartement.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PeriodOccupation implements ResponseDto {
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private String libelle;
    private String nomLocataire;
    private BigDecimal loyer;
}
