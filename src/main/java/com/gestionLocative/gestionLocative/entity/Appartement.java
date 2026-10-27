package com.gestionLocative.gestionLocative.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;
/**
 * Représente un appartement dans le système de gestion locative.
 * Un appartement est associé à un propriétaire et à un immeuble.
 */
@Entity
@Table(name = "appartement")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Appartement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nom;
    private BigDecimal surface;
    @Column(name = "nb_pieces")
    private int nbPieces;
    private int etage;
    @ManyToOne
    @JoinColumn(name = "proprietaire_id")
    private Proprietaire proprietaire;
    @ManyToOne
    @JoinColumn(name = "immeuble_id")
    private Immeuble immeuble;
    @Column(name = "date_creation", nullable = false, updatable = false)
    private Date dateCreation;

    @PrePersist
    protected void onCreate() {
        this.dateCreation = new Date();
    }
}
