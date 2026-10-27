package com.gestionLocative.gestionLocative.service;

import com.gestionLocative.gestionLocative.dto.ResponseDto;
import com.gestionLocative.gestionLocative.exception.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public abstract class AbstractCrudService implements CrudService {

    private final JpaRepository repository;

    public AbstractCrudService(JpaRepository repository) {
        this.repository = repository;
    }

    public Page<ResponseDto> getAll(int page, int taille, Sort sort) {
        Pageable pageable = PageRequest.of(pageAutorisee(page), tailleAutorisee(taille), sort);
        return repository.findAll(pageable).map(this::toDto);
    }

    @SuppressWarnings("unchecked")
    public ResponseDto getById(Long id) {
        Object entity = ((Optional<Object>) repository.findById(id))
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_MESSAGE));
        return toDto(entity);
    }

    @SuppressWarnings("unchecked")
    public ResponseDto delete(Long id) {
        Object entity = ((Optional<Object>) repository.findById(id))
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_MESSAGE));
        repository.deleteById(id);
        return toDto(entity);
    }

    protected int tailleAutorisee(int taille) {
        if (taille < 1) return TAILLE_DEFAUT;
        return Math.min(taille, TAILLE_MAX);
    }

    protected int pageAutorisee(int page) {
        return Math.max(page, 0);
    }
}