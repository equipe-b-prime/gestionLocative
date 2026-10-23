package com.gestionLocative.gestionLocative.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Vue d'agrégation financière par bail.
 * Calcule le total facturé, encaissé, le reste du et les quittances non payées.
 * Lecture seule, mappée sur la vue SQL v_bail_financier.
 */
@Entity
@Immutable
@Table(name = "v_bail_financier")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BailFinancierView {

    @Id
    @Column(name = "bail_id")
    private Long bailId;

    @Column(name = "nb_quittances")
    private Long nbQuittances;

    @Column(name = "total_facture")
    private BigDecimal totalFacture;

    @Column(name = "total_encaisse")
    private BigDecimal totalEncaisse;

    @Column(name = "reste_du")
    private BigDecimal resteDu;

    @Column(name = "nb_quittances_non_payees")
    private Long nbQuittancesNonPayees;

    @Column(name = "dernier_mois_facture")
    private Integer dernierMoisFacture;

    @Column(name = "dernier_paiement")
    private LocalDateTime dernierPaiement;
}