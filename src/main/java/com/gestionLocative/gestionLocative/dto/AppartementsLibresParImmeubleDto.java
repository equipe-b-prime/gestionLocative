package com.gestionLocative.gestionLocative.dto;

import java.util.List;
import lombok.*;


@Getter @Setter @Builder @AllArgsConstructor @NoArgsConstructor
public class AppartementsLibresParImmeubleDto implements ResponseDto {
    private Long immeubleId;
    private String immeubleNom;
    private String adresse;
    private List<AppartementLibreDto> appartements;
}
