package com.gestionLocative.gestionLocative.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
/**
 * Représente le paiement d'un loyer par un locataire.
 * Plusieurs paiements peuvent être rattachés à une même quittance.
 */
@Entity
@Table(name = "paiement", indexes = {
        @Index(name = "idx_paiement_quittance", columnList = "quittance_id")
})
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Paiement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "date_paiement",nullable = false)
    private LocalDateTime datePaiement;
    @Column(name = "montant_paye")
    private BigDecimal montantPaye;
    @ManyToOne
    @JoinColumn(name = "quittance_id")
    private Quittance quittance;
}
