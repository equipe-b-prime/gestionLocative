package com.gestionLocative.gestionLocative.service;

import com.gestionLocative.gestionLocative.dto.*;
import com.gestionLocative.gestionLocative.entity.Appartement;
import com.gestionLocative.gestionLocative.entity.Bail;
import com.gestionLocative.gestionLocative.entity.Immeuble;
import com.gestionLocative.gestionLocative.entity.Proprietaire;
import com.gestionLocative.gestionLocative.exception.NotFoundException;
import com.gestionLocative.gestionLocative.exception.RessourceUtiliseeException;
import com.gestionLocative.gestionLocative.repository.AppartementRepository;
import com.gestionLocative.gestionLocative.repository.BailRepository;
import com.gestionLocative.gestionLocative.repository.ImmeubleRepository;
import com.gestionLocative.gestionLocative.repository.ProprietaireRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppartementService extends AbstractCrudService {

    private final AppartementRepository appartementRepository;
    private final ProprietaireRepository proprietaireRepository;
    private final ImmeubleRepository immeubleRepository;
    private final BailRepository bailRepository;

    public AppartementService(AppartementRepository appartementRepository,
                              ProprietaireRepository proprietaireRepository,
                              ImmeubleRepository immeubleRepository,
                              BailRepository bailRepository) {
        super(appartementRepository);
        this.appartementRepository = appartementRepository;
        this.proprietaireRepository = proprietaireRepository;
        this.immeubleRepository = immeubleRepository;
        this.bailRepository = bailRepository;
    }

    public Appartement toEntity(AppartementRequestDto dto, Proprietaire proprietaire, Immeuble immeuble) {
        return Appartement.builder()
                .nom(dto.getNom())
                .surface(dto.getSurface())
                .nbPieces(dto.getNbPieces())
                .etage(dto.getEtage())
                .proprietaire(proprietaire)
                .immeuble(immeuble)
                .build();
    }

    @Override
    public ResponseDto toDto(Object entity) {
        Appartement a = (Appartement) entity;
        Proprietaire p = a.getProprietaire();
        Immeuble i = a.getImmeuble();

        ProprietaireResponseDto proprietaireDto = null;
        if (p != null) {
            proprietaireDto = ProprietaireResponseDto.builder()
                    .id(p.getId())
                    .nom(p.getNom())
                    .prenom(p.getPrenom())
                    .numero(p.getNumero())
                    .email(p.getEmail())
                    .adresse(p.getAdresse())
                    .build();
        }

        Proprietaire proprietaireImmeuble = null;
        if (i.getProprietaireId() != null) {
            proprietaireImmeuble = proprietaireRepository.findById(i.getProprietaireId()).orElse(null);
        }
        ProprietaireResponseDto proprietaireImmeubleDto = null;
        if (proprietaireImmeuble != null) {
            proprietaireImmeubleDto = ProprietaireResponseDto.builder()
                    .id(proprietaireImmeuble.getId())
                    .nom(proprietaireImmeuble.getNom())
                    .prenom(proprietaireImmeuble.getPrenom())
                    .numero(proprietaireImmeuble.getNumero())
                    .email(proprietaireImmeuble.getEmail())
                    .build();
        }

        ImmeubleResponseDto immeubleDto = ImmeubleResponseDto.builder()
                .id(i.getId())
                .nom(i.getNom())
                .adresse(i.getAdresse())
                .nbEtages(i.getNbEtages())
                .proprietaireId(i.getProprietaireId())
                .proprietaire(proprietaireImmeubleDto)
                .nbAppartements(appartementRepository.countByImmeubleId(i.getId()))
                .build();

        return AppartementResponseDto.builder()
                .id(a.getId())
                .nom(a.getNom())
                .surface(a.getSurface())
                .nbPieces(a.getNbPieces())
                .etage(a.getEtage())
                .proprietaire(proprietaireDto)
                .immeuble(immeubleDto)
                .build();
    }

    public Page<ResponseDto> getAll(int page, int taille) {
        return getAll(page, taille, Sort.by("etage", "surface"));
    }

    @Override
    public ResponseDto create(RequestDto request) {
        AppartementRequestDto dto = (AppartementRequestDto) request;
        Proprietaire proprietaire = proprietaireRepository.findById(dto.getProprietaireId())
                .orElseThrow(() -> new NotFoundException("Propriétaire non trouvé"));
        Immeuble immeuble = immeubleRepository.findById(dto.getImmeubleId())
                .orElseThrow(() -> new NotFoundException("Immeuble non trouvé"));
        if (dto.getEtage() > immeuble.getNbEtages()) {
            throw new IllegalArgumentException("L'étage (" + dto.getEtage()
                    + ") ne peut pas dépasser le nombre d'étages de l'immeuble (" + immeuble.getNbEtages() + ").");
        }
        return toDto(appartementRepository.save(toEntity(dto, proprietaire, immeuble)));
    }

    @Override
    public ResponseDto update(Long id, RequestDto request) {
        AppartementRequestDto dto = (AppartementRequestDto) request;
        Appartement appartement = appartementRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Appartement non trouvé"));
        Immeuble immeuble = appartement.getImmeuble();
        if (dto.getEtage() > immeuble.getNbEtages()) {
            throw new IllegalArgumentException("L'étage (" + dto.getEtage()
                    + ") ne peut pas dépasser le nombre d'étages de l'immeuble (" + immeuble.getNbEtages() + ").");
        }
        appartement.setNom(dto.getNom());
        appartement.setSurface(dto.getSurface());
        appartement.setNbPieces(dto.getNbPieces());
        appartement.setEtage(dto.getEtage());
        return toDto(appartementRepository.save(appartement));
    }

    @Override
    public ResponseDto delete(Long id) {
        Appartement appartement = appartementRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Appartement non trouvé"));
        if (bailRepository.existsByAppartementId(id)) {
            throw new RessourceUtiliseeException("Appartement", id, "Bail");
        }
        appartementRepository.deleteById(id);
        return toDto(appartement);
    }

    public List<ResponseDto> getAppartementsByImmeuble(Long immeubleId) {
        Immeuble immeuble = immeubleRepository.findById(immeubleId)
                .orElseThrow(() -> new NotFoundException("Immeuble non trouvé"));
        Date maintenant = new Date();
        return appartementRepository.findByImmeubleId(immeubleId).stream()
                .map(a -> {
                    AppartementResponseDto dto = (AppartementResponseDto) toDto(a);
                    boolean occupe = bailRepository.findByAppartementId(a.getId()).stream()
                            .anyMatch(b -> b.getDateDebut() != null && !b.getDateDebut().after(maintenant)
                                    && (b.getDateFin() == null || !b.getDateFin().before(maintenant)));
                    dto.setEtageOccupe(occupe);
                    return dto;
                })
                .collect(Collectors.toList());
    }
}