package com.gestionLocative.gestionLocative.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonSetter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppartementRequestDto extends AbstractClean implements RequestDto {
    private String nom;
    @NotNull(message = "La surface est obligatoire")
    @Positive(message = "La surface doit être positive")
    private BigDecimal surface;
    @NotNull(message = "Le nombre de pièces est obligatoire")
    @Min(value = 1, message = "Le nombre de pièces doit être positif")
    private int nbPieces;
    @Min(value = 0, message = "L'étage ne peut pas être négatif")
    private int etage;
    @NotNull(message = "Le propriétaire est obligatoire")
    private Long proprietaireId;
    @NotNull(message = "L'immeuble est obligatoire")
    private Long immeubleId;

    @JsonSetter("nom")
    public void setNom(String nom) {
        this.nom = cleanText(nom);
    }
}