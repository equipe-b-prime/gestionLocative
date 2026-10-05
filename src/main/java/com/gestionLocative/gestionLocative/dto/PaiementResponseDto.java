package com.gestionLocative.gestionLocative.dto;

import com.gestionLocative.gestionLocative.dto.QuittanceResponseDto;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Réponse contenant les données d'un paiement de loyer,
 * avec les informations de la quittance associée.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaiementResponseDto {
    private Long id;
    private LocalDateTime datePaiement;
    private BigDecimal montantPaye;
    private QuittanceResponseDto quittance;
}