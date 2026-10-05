package com.gestionLocative.gestionLocative.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
/**
 * Représente  la charge que peut avoir un immeuble
 */
@Entity
@Table(name = "charge")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Charge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int annee;
    private BigDecimal montant;
    @ManyToOne
    @JoinColumn(name = "immeuble_id")
    private Immeuble immeuble;
}
