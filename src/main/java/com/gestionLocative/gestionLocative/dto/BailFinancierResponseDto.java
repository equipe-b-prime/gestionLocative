package com.gestionLocative.gestionLocative.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Réponse représentant la situation financière agrégée d'un bail.
 * Un bail sans quittance obtient des compteurs et des montants à zéro.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BailFinancierResponseDto implements ResponseDto {
    private Long bailId;
    private Long nbQuittances;
    private BigDecimal totalFacture;
    private BigDecimal totalEncaisse;
    private BigDecimal resteDu;
    private Long nbQuittancesNonPayees;
    private Integer dernierMoisFacture;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dernierPaiement;
}
