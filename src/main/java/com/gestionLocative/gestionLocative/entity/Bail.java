package com.gestionLocative.gestionLocative.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;
/**
 * Représente le contart entre un  locotaire et un appartement
 */
@Entity
@Table(name = "bail")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Bail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "date_debut")
    private Date dateDebut;
    @Column(name = "date_fin")
    private Date dateFin;
    @Column(name = "montant_loyer")
    private BigDecimal montantLoyer;
    @Column(name = "provision_charges")
    private BigDecimal provisionCharges;
    @Column(name = "depot_garantie")
    private BigDecimal depotGarantie;
    @ManyToOne
    @JoinColumn(name = "appartement_id")
    private Appartement appartement;
    @ManyToOne
    @JoinColumn(name = "locataire_id")
    private Locataire locataire;
}