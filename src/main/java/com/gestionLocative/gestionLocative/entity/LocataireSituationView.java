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
import java.util.Date;

/**
 * Vue d'agrégation de la situation financière d'un locataire.
 * Agrège les baux, quittances et paiements par locataire.
 * Lecture seule, mappée sur la vue SQL v_locataire_situation.
 */
@Entity
@Immutable
@Table(name = "v_locataire_situation")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LocataireSituationView {

    @Id
    @Column(name = "locataire_id")
    private Long locataireId;

    @Column(name = "nb_baux")
    private Long nbBaux;

    @Column(name = "nb_baux_actifs")
    private Long nbBauxActifs;

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

    @Column(name = "premier_bail")
    private Date premierBail;

    @Column(name = "dernier_paiement")
    private LocalDateTime dernierPaiement;
}