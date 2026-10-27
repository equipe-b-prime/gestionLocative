package com.gestionLocative.gestionLocative.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"code", "hasErreur", "message", "nombreTotalElements", "totalElements", "totalPages", "data"})
public class Response<T> {
    private int code;
    private String message;
    private boolean hasErreur;
    private List<T> data;
    private Long nombreTotalElements;
    private Long totalElements;
    private Integer totalPages;

    public Response(List<T> data) {
        this.code = 800;
        this.message = "Opération effectuée avec succès";
        this.hasErreur = false;
        this.nombreTotalElements = (long) data.size();
        this.data = data;
    }

    public Response(T data) {
        this.data = List.of(data);
        this.code = 800;
        this.message = "Opération effectuée avec succès";
        this.hasErreur = false;
        this.nombreTotalElements = 1L;
    }

    public Response(Page<T> page) {
        this.code = 800;
        this.message = "Opération effectuée avec succès";
        this.hasErreur = false;
        this.data = page.getContent();
        this.nombreTotalElements = (long) page.getContent().size();
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
    }
}