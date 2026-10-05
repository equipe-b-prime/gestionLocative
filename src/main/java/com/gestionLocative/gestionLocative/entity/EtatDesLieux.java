package com.gestionLocative.gestionLocative.entity;

import jakarta.persistence.*;
import lombok.*;
/**
 * Représente a associé un locataire
 */
import java.math.BigDecimal;
import java.time.LocalDateTime;
@Entity
@Table(name = "Etat_les_lieux")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EtatDesLieux {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime dateConstat;
    private BigDecimal coutReparation;
    @Column(columnDefinition = "Text")
    private String observations;
    @ManyToOne
    @JoinColumn(name = "bail_id")
    private Bail bail;
    @ManyToOne
    @JoinColumn(name = "type_constat_id")
    private TypeConstat typeConstat;
}
