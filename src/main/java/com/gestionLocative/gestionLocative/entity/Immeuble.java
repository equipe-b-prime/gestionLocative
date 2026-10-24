package com.gestionLocative.gestionLocative.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Représente un immeuble appartenant à un propriétaire et pouvant contenir plusieurs appartements
 */
@Entity
@Table(name = "immeuble", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"nom", "adresse"})
}, indexes = {
        @Index(name = "idx_immeuble_proprietaire", columnList = "proprietaire_id")
})
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Immeuble {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 50, nullable = false)
    private String nom;
    @Column(length = 50, nullable = false)
    private String adresse;
    @Column(name = "nb_etages")
    private Integer nbEtages;
    @ManyToOne
    @JoinColumn(name = "proprietaire_id")
    private Proprietaire proprietaire;
}
