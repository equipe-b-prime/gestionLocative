package com.gestionLocative.gestionLocative.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import com.fasterxml.jackson.annotation.JsonSetter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LocataireRequestDto extends AbstractClean implements RequestDto {
    private Long id;
    @NotBlank(message = "Le nom est obligatoire")
    private String nom;
    private String prenom;
    @NotBlank(message = "Le téléphone est obligatoire")
    private String numero;
    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email doit être valide")
    private String email;

    @JsonSetter("nom")
    public void setNom(String nom) {
        this.nom = cleanText(nom);
    }

    @JsonSetter("prenom")
    public void setPrenom(String prenom) {
        this.prenom = cleanText(prenom);
    }

    @JsonSetter("numero")
    public void setNumero(String numero) {
        this.numero = cleanText(numero);
    }

    @JsonSetter("email")
    public void setEmail(String email) {
        this.email = cleanText(email);
    }
}