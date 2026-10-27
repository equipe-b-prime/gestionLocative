package com.gestionLocative.gestionLocative.service;

import com.gestionLocative.gestionLocative.dto.RequestDto;
import com.gestionLocative.gestionLocative.dto.ResponseDto;
import com.gestionLocative.gestionLocative.exception.NotFoundException;

public interface CrudService {

    String NOT_FOUND_MESSAGE = "La ressource demandée est introuvable.";
    int TAILLE_DEFAUT = 10;
    int TAILLE_MAX = 100;

    ResponseDto create(RequestDto request);

    ResponseDto update(Long id, RequestDto request);

    ResponseDto toDto(Object entity);
}