package com.gestionLocative.gestionLocative.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
/**
 * Représente la quittance d'un loyer pour un locataire
 */

@Entity
@Table(name = "quittance")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Quittance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Integer mois;
    @Column(nullable = false)
    private Integer annee;
    @Column(name ="montant_loyer_facture", nullable = false)
    private BigDecimal montantLoyerFacture;
    @Column(name ="montant_charge_facture", nullable = false)
    private BigDecimal montantChargeFacture;
    @ManyToOne
    @JoinColumn(name = "bail_id")
    private Bail bail;
}
