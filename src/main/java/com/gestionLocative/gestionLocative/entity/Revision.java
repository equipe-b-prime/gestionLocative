package com.gestionLocative.gestionLocative.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Entité suivant l'historique des révisions de loyer d'un bail.
 * Chaque révision enregistre la date d'effet et le nouveau montant du loyer.
 */
@Entity
@Table(name = "revision", indexes = {
        @Index(name = "idx_revision_bail", columnList = "bail_id")
})
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Revision {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "bail_id", nullable = false)
    private Bail bail;

    @Column(name = "date_effet", nullable = false)
    private Date dateEffet;

    @Column(name = "montant_loyer", nullable = false, precision = 19, scale = 2)
    private BigDecimal montantLoyer;
}