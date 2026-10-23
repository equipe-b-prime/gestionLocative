package com.gestionLocative.gestionLocative.controller;

import com.gestionLocative.gestionLocative.dto.ImmeubleAvecStatsResponseDto;
import com.gestionLocative.gestionLocative.dto.ProprietaireRequestDto;
import com.gestionLocative.gestionLocative.dto.Response;
import com.gestionLocative.gestionLocative.dto.ResponseDto;
import com.gestionLocative.gestionLocative.service.ProprietaireService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/proprietaires")
public class ProprietaireController {

    private final ProprietaireService proprietaireService;

    public ProprietaireController(ProprietaireService proprietaireService) {
        this.proprietaireService = proprietaireService;
    }

    @GetMapping
    public Response<ResponseDto> getAll(@RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "10") int taille) {
        return new Response<>(proprietaireService.getAll(page, taille).getContent());
    }

    @GetMapping("/{id}")
    public Response<ResponseDto> getById(@PathVariable Long id) {
        return new Response<>(proprietaireService.getById(id));
    }

    @PostMapping
    public Response<ResponseDto> create(@Valid @RequestBody ProprietaireRequestDto proprietaireRequest) {
        return new Response<>(proprietaireService.create(proprietaireRequest));
    }

    @PutMapping("/{id}")
    public Response<ResponseDto> update(@PathVariable Long id,@Valid  @RequestBody ProprietaireRequestDto proprietaireRequest) {
        return new Response<>(proprietaireService.update(id, proprietaireRequest));
    }

    @DeleteMapping("/{id}")
    public Response<ResponseDto> delete(@PathVariable Long id) {
        return new Response<>(proprietaireService.delete(id));
    }

    @GetMapping("/{id}/immeubles")
    public Response<ResponseDto> getImmeubles(@PathVariable Long id) {
        return new Response<>(proprietaireService.getImmeublesByProprietaire(id));
    }
}