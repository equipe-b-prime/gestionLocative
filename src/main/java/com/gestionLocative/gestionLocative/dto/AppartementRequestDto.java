package com.gestionLocative.gestionLocative.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;

/**
 * Requête de création ou de modification d'un appartement.
 * L'immeuble est transmis via son identifiant.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppartementRequestDto implements RequestDto {
    @NotBlank(message = "Le nom de l'appartement est obligatoire")
    private String nom;
    @Positive(message = "La surface de l'appartement doit être positive")
    private BigDecimal surface;
    @Positive(message = "Le nombre de pièces de l'appartement doit être positif")
    private int nbPieces;
    @PositiveOrZero(message = "L'étage de l'appartement ne peut pas être négatif")
    private int etage;
    @NotNull(message = "L'immeuble est obligatoire")
    private Long immeubleId;
}
