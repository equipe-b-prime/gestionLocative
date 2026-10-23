package com.gestionLocative.gestionLocative.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Requête de création ou de modification d'un propriétaire.
 * Le nom et le téléphone (numero) sont obligatoires ; l'email est optionnel
 * mais doit être unique s'il est fourni.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProprietaireRequestDto implements RequestDto {
    @NotBlank(message = "Le nom du propriétaire est obligatoire")
    private String nom;
    @NotBlank(message = "Le prenom du propriétaire est obligatoire")
    private String prenom;
    @NotBlank(message = "Le numero du propriétaire est obligatoire")
    private String numero;
    @Email(message = "Le mail du propriétaire doit être valide")
    private String email;
}
