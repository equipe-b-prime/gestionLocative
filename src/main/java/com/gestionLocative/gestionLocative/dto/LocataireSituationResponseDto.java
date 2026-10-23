package com.gestionLocative.gestionLocative.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * Réponse représentant la situation financière agrégée d'un locataire,
 * calculée sur la totalité de ses baux.
 * Un locataire sans quittance obtient des compteurs et des montants à zéro.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LocataireSituationResponseDto implements ResponseDto {
    private Long locataireId;
    private Long nbBaux;
    private Long nbBauxActifs;
    private Long nbQuittances;
    private BigDecimal totalFacture;
    private BigDecimal totalEncaisse;
    private BigDecimal resteDu;
    private Long nbQuittancesNonPayees;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date premierBail;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dernierPaiement;
}
