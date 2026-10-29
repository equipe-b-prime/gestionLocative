package com.gestionLocative.gestionLocative.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

/**
 * Trace des modifications de la date de fin d'un bail (renouvellement, résiliation),
 * afin de conserver l'ancienne date de fin.
 */
@Entity
@Table(name = "bail_historique")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BailHistorique {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false)
    @JoinColumn(name = "bail_id", nullable = false)
    private Bail bail;
    @Column(nullable = false)
    private String type;
    @Column(name = "ancienne_date_fin")
    private Date ancienneDateFin;
    @Column(name = "nouvelle_date_fin")
    private Date nouvelleDateFin;
    @Column(name = "date_operation", nullable = false)
    private Date dateOperation;
}
