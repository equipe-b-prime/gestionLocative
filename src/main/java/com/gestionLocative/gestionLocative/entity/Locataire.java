package com.gestionLocative.gestionLocative.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.UncheckedIOException;

/**
 * Représente  le locataire d'un appartement
 */
@Entity
@Table(name = "locataire")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Locataire {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String nom;
    @Column(nullable = false)
    private String prenom;
    @Column(nullable = false,unique = true)
    private String numero;
    @Column(unique = true)
    private String email;
}
