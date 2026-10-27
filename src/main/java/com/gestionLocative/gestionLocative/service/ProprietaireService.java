package com.gestionLocative.gestionLocative.service;

import com.gestionLocative.gestionLocative.dto.ProprietaireRequestDto;
import com.gestionLocative.gestionLocative.dto.ProprietaireResponseDto;
import com.gestionLocative.gestionLocative.dto.RequestDto;
import com.gestionLocative.gestionLocative.dto.ResponseDto;
import com.gestionLocative.gestionLocative.entity.Proprietaire;
import com.gestionLocative.gestionLocative.entity.Immeuble;
import com.gestionLocative.gestionLocative.dto.ImmeubleResponseDto;
import com.gestionLocative.gestionLocative.exception.DoublonRessourceException;
import com.gestionLocative.gestionLocative.exception.NotFoundException;
import com.gestionLocative.gestionLocative.exception.RessourceUtiliseeException;
import com.gestionLocative.gestionLocative.repository.ImmeubleRepository;
import com.gestionLocative.gestionLocative.repository.AppartementRepository;
import com.gestionLocative.gestionLocative.repository.BailRepository;
import com.gestionLocative.gestionLocative.repository.ProprietaireRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class ProprietaireService extends AbstractCrudService {

    private final ProprietaireRepository proprietaireRepository;
    private final ImmeubleRepository immeubleRepository;
    private final AppartementRepository appartementRepository;
    private final BailRepository bailRepository;

    public ProprietaireService(ProprietaireRepository proprietaireRepository,
                               ImmeubleRepository immeubleRepository,
                               AppartementRepository appartementRepository,
                               BailRepository bailRepository) {
        super(proprietaireRepository);
        this.proprietaireRepository = proprietaireRepository;
        this.immeubleRepository = immeubleRepository;
        this.appartementRepository = appartementRepository;
        this.bailRepository = bailRepository;
    }

    public Proprietaire toEntity(ProprietaireRequestDto dto) {
        return Proprietaire.builder()
                .nom(dto.getNom())
                .prenom(dto.getPrenom())
                .numero(dto.getNumero())
                .email(dto.getEmail())
                .adresse(dto.getAdresse())
                .build();
    }

    @Override
    public ResponseDto toDto(Object entity) {
        Proprietaire p = (Proprietaire) entity;
        return ProprietaireResponseDto.builder()
                .id(p.getId())
                .nom(p.getNom())
                .prenom(p.getPrenom())
                .numero(p.getNumero())
                .email(p.getEmail())
                .adresse(p.getAdresse())
                .build();
    }

    @Override
    public ResponseDto create(RequestDto request) {
        ProprietaireRequestDto dto = (ProprietaireRequestDto) request;
        if (dto.getEmail() != null && !dto.getEmail().isBlank()
                && proprietaireRepository.existsByEmail(dto.getEmail())) {
            throw new DoublonRessourceException("Un propriétaire avec l'email '" + dto.getEmail() + "' existe déjà.");
        }
        return toDto(proprietaireRepository.save(toEntity(dto)));
    }

    @Override
    public ResponseDto update(Long id, RequestDto request) {
        ProprietaireRequestDto dto = (ProprietaireRequestDto) request;
        Proprietaire proprietaire = proprietaireRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_MESSAGE));
        if (dto.getEmail() != null && !dto.getEmail().isBlank()
                && proprietaireRepository.existsByEmailAndIdNot(dto.getEmail(), id)) {
            throw new DoublonRessourceException("Un autre propriétaire avec l'email '" + dto.getEmail() + "' existe déjà.");
        }
        proprietaire.setNom(dto.getNom());
        proprietaire.setPrenom(dto.getPrenom());
        proprietaire.setNumero(dto.getNumero());
        proprietaire.setEmail(dto.getEmail());
        proprietaire.setAdresse(dto.getAdresse());
        return toDto(proprietaireRepository.save(proprietaire));
    }

    @Override
    public ResponseDto delete(Long id) {
        Proprietaire proprietaire = proprietaireRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_MESSAGE));
        if (immeubleRepository.existsByProprietaireId(id)) {
            throw new RessourceUtiliseeException("Proprietaire", id, "Immeuble");
        }
        proprietaireRepository.deleteById(id);
        return toDto(proprietaire);
    }

    public List<ResponseDto> getImmeublesByProprietaire(Long id) {
        List<Immeuble> immeubles = immeubleRepository.findByProprietaireId(id);
        LocalDateTime maintenant = LocalDateTime.now();
        return immeubles.stream()
                .map(immeuble -> {
                    ImmeubleResponseDto dto = ImmeubleResponseDto.builder()
                            .id(immeuble.getId())
                            .nom(immeuble.getNom())
                            .adresse(immeuble.getAdresse())
                            .nbEtages(immeuble.getNbEtages())
                            .proprietaireId(immeuble.getProprietaireId())
                            .nbAppartements(appartementRepository.countByImmeubleId(immeuble.getId()))
                            .nbBauxEnCours(0L)
                            .build();
                    long nbBauxEnCours = bailRepository.countBauxEnCoursParImmeuble(immeuble.getId(), maintenant);
                    dto.setNbBauxEnCours(nbBauxEnCours);
                    return dto;
                })
                .collect(Collectors.toList());
    }
}