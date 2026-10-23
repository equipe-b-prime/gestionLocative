package com.gestionLocative.gestionLocative.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Requête de création ou de modification d'un locataire.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LocataireRequestDto implements RequestDto {
    @NotBlank(message = "Le nom du locataire est obligatoire")
    private String nom;
    @NotBlank(message = "Le prenom est obligatoire")
    private String prenom;
    @NotBlank(message = "Le numero est obligatoire")
    private String numero;
    @NotBlank(message = "Le mail est obligatoire")
    @Email(message = "Le mail doit être valide")
    private String email;
}