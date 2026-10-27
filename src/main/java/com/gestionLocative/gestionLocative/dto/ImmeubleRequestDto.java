package com.gestionLocative.gestionLocative.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImmeubleRequestDto implements RequestDto {
    private Long id;
    private String nom;
    @NotBlank(message = "L'adresse est obligatoire")
    private String adresse;
    @NotNull(message = "Le nombre d'étages est obligatoire")
    @Min(value = 1, message = "Le nombre d'étages doit être positif")
    private Integer nbEtages;
    @NotNull(message = "Le propriétaire est obligatoire")
    private Long proprietaireId;
}