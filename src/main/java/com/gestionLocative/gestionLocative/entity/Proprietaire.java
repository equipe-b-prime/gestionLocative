package com.gestionLocative.gestionLocative.entity;

import jakarta.persistence.*;
import lombok.*;
/**
 * Représente  le proprietaire d'un immeuble
 */
@Entity
@Table(name = "proprietaire")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Proprietaire {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String nom;
    private String prenom;
    @Column(nullable = false)
    private String numero;
    @Column(unique = true)
    private String email;
    private String adresse;
}
