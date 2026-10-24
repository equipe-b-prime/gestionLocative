package com.gestionLocative.gestionLocative.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Représente un appartement dans le système de gestion locative.
 * Un appartement est associé à un immeuble.
 */
@Entity
@Table(name = "appartement", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"nom", "immeuble_id"})
}, indexes = {
        @Index(name = "idx_appartement_immeuble", columnList = "immeuble_id")
})
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Appartement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String nom;
    private BigDecimal surface;
    @Column(name = "nb_pieces")
    private int nbPieces;
    @Column(nullable = false)
    private int etage;
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
