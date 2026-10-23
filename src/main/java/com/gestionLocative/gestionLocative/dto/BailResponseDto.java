package com.gestionLocative.gestionLocative.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.Date;

/**
 * Réponse contenant les données d'un bail,
 * avec une indication si le bail est en cours.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BailResponseDto implements ResponseDto {
    private Long id;
    private Date dateDebut;
    private Date dateFin;
    private BigDecimal montantLoyer;
    private BigDecimal provisionCharges;
    private BigDecimal depotGarantie;
    private AppartementResponseDto appartement;
    private LocataireResponseDto locataire;
    private boolean enCours;
}
