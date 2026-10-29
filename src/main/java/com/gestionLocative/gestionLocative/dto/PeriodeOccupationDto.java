package com.gestionLocative.gestionLocative.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.*;


@Getter @Setter @Builder @AllArgsConstructor @NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PeriodeOccupationDto implements ResponseDto {
    /** OCCUPE ou VACANT. */
    private String type;
    private LocalDate dateDebut;
    /** null = période ouverte (sans fin). */
    private LocalDate dateFin;
    private Long bailId;
    private LocataireResponseDto locataire;
    private BigDecimal loyer;
}
