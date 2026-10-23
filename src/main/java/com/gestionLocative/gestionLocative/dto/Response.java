package com.gestionLocative.gestionLocative.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"code","hasErreur","message","nombreTotalElements","totalElements","totalPages","data"})
public class Response<T> {
    private List<T> data;
    private int code;
    private String message;
    private Long nombreTotalElements;
    private Long totalElements;
    private Long totalPages;
    private boolean hasErreur;

    public Response(List<T> data, int code, String message, Long nombreTotalElements, Long totalElements, Long totalPages, boolean hasErreur) {
        this.data = data;
        this.code = code;
        this.message = message;
        this.nombreTotalElements = nombreTotalElements;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.hasErreur = hasErreur;
    }

    /**
     * Constructeur pour une liste de résultats.
     *
     * @param data la liste des données retournées
     */
    public Response(List<T> data) {
        this.message = "Opération effectuée avec succès";
        this.code = 800;
        this.hasErreur = false;
        this.nombreTotalElements = (long) data.size();
        this.data = data;
    }
    /**
     * Constructeur pour un résultat unique.
     *
     * @param data l'unique donnée retournée
     */
    public Response(T data) {
        this.data = List.of(data);
        this.message = "Opération effectuée avec succès";
        this.hasErreur = false;
        this.nombreTotalElements = 1L;
        this.code = 800;

    }



}
