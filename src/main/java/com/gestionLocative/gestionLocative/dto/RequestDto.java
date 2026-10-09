package com.gestionLocative.gestionLocative.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
@JsonSubTypes({
        @JsonSubTypes.Type(AppartementRequestDto.class),
        @JsonSubTypes.Type(BailRequestDto.class),
        @JsonSubTypes.Type(ImmeubleRequestDto.class),
        @JsonSubTypes.Type(LocataireRequestDto.class),
        @JsonSubTypes.Type(ProprietaireRequestDto.class)

})
public interface RequestDto { }
