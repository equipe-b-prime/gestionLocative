package com.gestionLocative.gestionLocative.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Requête de création ou de modification d'un bail.
 * Les relations sont transmises via les identifiants de l'appartement et du locataire.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BailRequestDto implements RequestDto {
    private Date dateDebut;
    private Date dateFin;
    private BigDecimal montantLoyer;
    private BigDecimal provisionCharges;
    private BigDecimal depotGarantie;
    private Long appartementId;
    private Long locataireId;
}