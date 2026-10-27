package com.gestionLocative.gestionLocative.service;

import com.gestionLocative.gestionLocative.dto.*;
import com.gestionLocative.gestionLocative.entity.*;
import com.gestionLocative.gestionLocative.exception.NotFoundException;
import com.gestionLocative.gestionLocative.exception.RequeteInvalideException;
import com.gestionLocative.gestionLocative.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BailService extends AbstractCrudService {

    private final BailRepository bailRepository;
    private final AppartementRepository appartementRepository;
    private final LocataireRepository locataireRepository;
    private final ProprietaireRepository proprietaireRepository;
    private final ImmeubleRepository immeubleRepository;

    public BailService(BailRepository bailRepository,
                       AppartementRepository appartementRepository,
                       LocataireRepository locataireRepository,
                       ProprietaireRepository proprietaireRepository,
                       ImmeubleRepository immeubleRepository) {
        super(bailRepository);
        this.bailRepository = bailRepository;
        this.appartementRepository = appartementRepository;
        this.locataireRepository = locataireRepository;
        this.proprietaireRepository = proprietaireRepository;
        this.immeubleRepository = immeubleRepository;
    }

    private ProprietaireResponseDto toProprietaireDto(Proprietaire p) {
        if (p == null) return null;
        return ProprietaireResponseDto.builder()
                .id(p.getId()).nom(p.getNom()).prenom(p.getPrenom())
                .numero(p.getNumero()).email(p.getEmail()).build();
    }

    private ImmeubleResponseDto toImmeubleDto(Immeuble i) {
        Proprietaire pi = i.getProprietaireId() != null
                ? proprietaireRepository.findById(i.getProprietaireId()).orElse(null) : null;
        return ImmeubleResponseDto.builder()
                .id(i.getId()).nom(i.getNom()).adresse(i.getAdresse())
                .nbEtages(i.getNbEtages()).proprietaireId(i.getProprietaireId())
                .proprietaire(toProprietaireDto(pi))
                .nbAppartements(appartementRepository.countByImmeubleId(i.getId()))
                .build();
    }

    private AppartementResponseDto toAppartementDto(Appartement a) {
        Proprietaire p = a.getProprietaire();
        return AppartementResponseDto.builder()
                .id(a.getId()).nom(a.getNom()).surface(a.getSurface())
                .nbPieces(a.getNbPieces()).etage(a.getEtage())
                .proprietaire(toProprietaireDto(p))
                .immeuble(toImmeubleDto(a.getImmeuble()))
                .build();
    }

    @Override
    public ResponseDto toDto(Object entity) {
        Bail bail = (Bail) entity;
        Date maintenant = new Date();
        boolean enCours = bail.getDateDebut() != null && !bail.getDateDebut().after(maintenant)
                && (bail.getDateFin() == null || !bail.getDateFin().before(maintenant));
        Locataire l = bail.getLocataire();
        return BailResponseDto.builder()
                .id(bail.getId())
                .dateDebut(bail.getDateDebut())
                .dateFin(bail.getDateFin())
                .montantLoyer(bail.getMontantLoyer())
                .provisionCharges(bail.getProvisionCharges())
                .depotGarantie(bail.getDepotGarantie())
                .enCours(enCours)
                .appartement(toAppartementDto(bail.getAppartement()))
                .locataire(LocataireResponseDto.builder()
                        .id(l.getId()).nom(l.getNom()).prenom(l.getPrenom())
                        .numero(l.getNumero()).email(l.getEmail()).build())
                .build();
    }

    @Override
    public ResponseDto create(RequestDto request) {
        BailRequestDto dto = (BailRequestDto) request;
        if (dto.getDateFin() != null && !dto.getDateFin().after(dto.getDateDebut())) {
            throw new IllegalArgumentException("La date de fin doit être postérieure à la date de début.");
        }
        long jours = (dto.getDateFin() != null)
                ? (dto.getDateFin().getTime() - dto.getDateDebut().getTime()) / (1000L * 60 * 60 * 24)
                : 0;
        if (dto.getDateFin() != null && jours < 30) {
            throw new RequeteInvalideException("La durée minimum d'un bail est d'un mois.");
        }
        Appartement appartement = appartementRepository.findById(dto.getAppartementId())
                .orElseThrow(() -> new NotFoundException("Appartement non trouvé"));
        Locataire locataire = locataireRepository.findById(dto.getLocataireId())
                .orElseThrow(() -> new NotFoundException("Locataire non trouvé"));

        List<Bail> conflits = bailRepository.findConflictingBails(
                dto.getAppartementId(), dto.getDateDebut(),
                dto.getDateFin() != null ? dto.getDateFin() : new Date(System.currentTimeMillis() + 365L * 24 * 3600 * 1000));
        if (!conflits.isEmpty()) {
            throw new IllegalArgumentException("L'appartement est déjà occupé sur la période demandée.");
        }

        Bail bail = Bail.builder()
                .dateDebut(dto.getDateDebut())
                .dateFin(dto.getDateFin())
                .montantLoyer(dto.getMontantLoyer())
                .provisionCharges(dto.getProvisionCharges())
                .depotGarantie(dto.getDepotGarantie())
                .appartement(appartement)
                .locataire(locataire)
                .build();
        return toDto(bailRepository.save(bail));
    }

    @Override
    public ResponseDto update(Long id, RequestDto request) {
        BailRequestDto dto = (BailRequestDto) request;
        Bail bail = bailRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Bail non trouvé"));
        if (dto.getDateFin() != null && !dto.getDateFin().after(bail.getDateDebut())) {
            throw new IllegalArgumentException("La date de fin doit être postérieure à la date de début.");
        }
        boolean nonCommence = bail.getDateDebut() != null && bail.getDateDebut().after(new Date());
        if (nonCommence) {
            bail.setMontantLoyer(dto.getMontantLoyer());
        }
        bail.setDateFin(dto.getDateFin());
        return toDto(bailRepository.save(bail));
    }

    @Override
    public ResponseDto delete(Long id) {
        Bail bail = bailRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_MESSAGE));
        boolean nonCommence = bail.getDateDebut() != null && bail.getDateDebut().after(new Date());
        if (!nonCommence) {
            throw new IllegalArgumentException("Un bail en cours ne peut pas être supprimé, il doit être résilié.");
        }
        bailRepository.deleteById(id);
        return toDto(bail);
    }
}