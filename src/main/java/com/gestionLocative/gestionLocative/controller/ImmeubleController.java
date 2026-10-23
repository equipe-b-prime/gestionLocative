package com.gestionLocative.gestionLocative.controller;

import com.gestionLocative.gestionLocative.dto.Response;
import com.gestionLocative.gestionLocative.dto.ResponseDto;
import com.gestionLocative.gestionLocative.service.ImmeubleService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/immeubles")
public class ImmeubleController {

    private final ImmeubleService immeubleService;

    public ImmeubleController(ImmeubleService immeubleService) {
        this.immeubleService = immeubleService;
    }

    @GetMapping
    public Response<ResponseDto> getAll(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "10") int taille) {
        return new Response<>(immeubleService.getAll(page, taille).getContent());
    }

    @GetMapping("/{id}")
    public Response<ResponseDto> getById(@PathVariable Long id) {
        return new Response<>(immeubleService.getById(id));
    }

    @PostMapping
    public Response<ResponseDto> create(@Valid @RequestBody com.gestionLocative.gestionLocative.dto.ImmeubleRequestDto immeubleRequest) {
        return new Response<>(immeubleService.create(immeubleRequest));
    }

    @PutMapping("/{id}")
    public Response<ResponseDto> update(@PathVariable Long id,
                                        @Valid @RequestBody com.gestionLocative.gestionLocative.dto.ImmeubleRequestDto immeubleRequest) {
        return new Response<>(immeubleService.update(id, immeubleRequest));
    }

    @DeleteMapping("/{id}")
    public Response<ResponseDto> delete(@PathVariable Long id) {
        return new Response<>(immeubleService.delete(id));
    }

    @GetMapping("/{id}/appartements")
    public Response<ResponseDto> getAppartements(@PathVariable Long id) {
        return new Response<>(immeubleService.getAppartementsByImmeuble(id));
    }
}