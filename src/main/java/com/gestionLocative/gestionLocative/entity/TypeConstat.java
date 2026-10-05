package com.gestionLocative.gestionLocative.entity;

import jakarta.persistence.*;
import lombok.*;
/**
 * Représente le type de detat de lieux  pour un locataire
 */
@Entity
@Table(name = "type_constat")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TypeConstat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String libelle;
}
