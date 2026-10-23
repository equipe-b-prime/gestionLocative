package com.gestionLocative.gestionLocative.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

/**
 * Requête de création ou de modification d'un immeuble.
 * Le propriétaire est transmis via son identifiant.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImmeubleRequestDto implements RequestDto {
    private Long id;
    @NotBlank(message = "Le nom de l'immeuble est obligatoire")
    private String nom;
    @NotBlank(message = "L'adresse de l'immeuble est obligatoire")
    private String adresse;
    @NotNull(message = "Le nombre d'étages de l'immeuble est obligatoire")
    @Positive(message = "Le nombre d'étages de l'immeuble doit être positif")
    private Integer nbEtages;
    @NotNull(message = "Le propriétaire de l'immeuble est obligatoire")
    private Long proprietaireId;
}
