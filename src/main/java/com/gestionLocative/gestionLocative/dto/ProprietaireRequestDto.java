package com.gestionLocative.gestionLocative.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProprietaireRequestDto implements RequestDto {
    private Long id;
    @NotBlank(message = "Le nom est obligatoire")
    private String nom;
    private String prenom;
    @NotBlank(message = "Le téléphone est obligatoire")
    private String numero;
    @Email(message = "L'email doit être valide")
    private String email;
    private String adresse;
}