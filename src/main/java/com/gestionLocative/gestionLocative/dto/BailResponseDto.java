package com.gestionLocative.gestionLocative.dto;

import com.gestionLocative.gestionLocative.dto.AppartementResponseDto;
import com.gestionLocative.gestionLocative.dto.LocataireResponseDto;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Réponse contenant les données d'un bail,
 * avec les informations de l'appartement et du locataire associés.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BailResponseDto {
    private Long id;
    private Date dateDebut;
    private Date dateFin;
    private BigDecimal montantLoyer;
    private BigDecimal provisionCharges;
    private BigDecimal depotGarantie;
    private AppartementResponseDto appartement;
    private LocataireResponseDto locataire;
}