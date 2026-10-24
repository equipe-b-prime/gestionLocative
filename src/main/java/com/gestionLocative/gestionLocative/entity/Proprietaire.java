package com.gestionLocative.gestionLocative.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Représente le propriétaire d'un immeuble
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
    @Column(nullable = false)
    private String prenom;
    @Column(nullable = false, unique = true)
    private String numero;
    @Column(unique = true, nullable = false)
    private String email;
}
