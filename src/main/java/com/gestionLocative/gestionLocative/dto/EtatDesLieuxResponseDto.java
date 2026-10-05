package com.gestionLocative.gestionLocative.dto;

import com.gestionLocative.gestionLocative.dto.BailResponseDto;
import com.gestionLocative.gestionLocative.dto.TypeConstatResponseDto;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Réponse contenant les données d'un état des lieux,
 * avec les informations du bail et du type de constat associés.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EtatDesLieuxResponseDto {
    private Long id;
    private LocalDateTime dateConstat;
    private BigDecimal coutReparation;
    private String observations;
    private BailResponseDto bail;
    private TypeConstatResponseDto typeConstat;
}