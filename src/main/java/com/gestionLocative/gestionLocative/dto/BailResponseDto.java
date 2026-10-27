package com.gestionLocative.gestionLocative.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

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
    private boolean enCours;
    private AppartementResponseDto appartement;
    private LocataireResponseDto locataire;
}