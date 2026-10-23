package com.gestionLocative.gestionLocative.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Requête de création ou de modification d'un bail.
 * Les relations sont transmises via les identifiants de l'appartement et du locataire.
 * La date de fin est optionnelle ; si elle est fournie, elle doit être postérieure à la date de début.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BailRequestDto implements RequestDto {
    @NotNull(message = "La date de début du bail est obligatoire")
    private Date dateDebut;
    private Date dateFin;
    @NotNull(message = "Le montant du loyer est obligatoire")
    @Positive(message = "Le montant du loyer doit être positif")
    private BigDecimal montantLoyer;
    @Positive(message = "Le montant de la provision pour charges doit être positif")
    private BigDecimal provisionCharges;
    @Positive(message = "Le dépôt de garantie doit être positif")
    private BigDecimal depotGarantie;
    @NotNull(message = "L'appartement est obligatoire")
    private Long appartementId;
    @NotNull(message = "Le locataire est obligatoire")
    private Long locataireId;
}
