package com.gestionLocative.gestionLocative.service;

import com.gestionLocative.gestionLocative.exception.NotFoundException;
import com.gestionLocative.gestionLocative.exception.RequeteInvalideException;

import com.gestionLocative.gestionLocative.repository.RevisionRepository;
import com.gestionLocative.gestionLocative.repository.AppartementRepository;
import com.gestionLocative.gestionLocative.repository.BailRepository;
import com.gestionLocative.gestionLocative.dto.PeriodOccupation;
import com.gestionLocative.gestionLocative.dto.RevenuImmeubleDto;
import com.gestionLocative.gestionLocative.dto.AppartementRevenuDto;
import com.gestionLocative.gestionLocative.dto.BailRequestDto;
import com.gestionLocative.gestionLocative.dto.BailResponseDto;
import com.gestionLocative.gestionLocative.dto.AppartementResponseDto;
import com.gestionLocative.gestionLocative.dto.ImmeubleResponseDto;
import com.gestionLocative.gestionLocative.dto.LocataireResponseDto;
import com.gestionLocative.gestionLocative.dto.ProprietaireResponseDto;
import com.gestionLocative.gestionLocative.dto.RequestDto;
import com.gestionLocative.gestionLocative.dto.ResponseDto;
import com.gestionLocative.gestionLocative.entity.Appartement;
import com.gestionLocative.gestionLocative.entity.Bail;
import com.gestionLocative.gestionLocative.entity.Immeuble;
import com.gestionLocative.gestionLocative.entity.Locataire;
import com.gestionLocative.gestionLocative.entity.Revision;
import com.gestionLocative.gestionLocative.repository.LocataireRepository;
import com.gestionLocative.gestionLocative.repository.ImmeubleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;
import java.math.BigDecimal;

/**
 * Service métier gérant les baux.
 * Fournit la création, la modification, la suppression et les conversions entité/DTO.
 */
@Service
public class BailService extends AbstractCrudService {

    private final BailRepository bailRepository;
    private final AppartementRepository appartementRepository;
    private final LocataireRepository locataireRepository;
    private final RevisionRepository revisionRepository;
    private final ImmeubleRepository immeubleRepository;

    public BailService(BailRepository bailRepository,
                       AppartementRepository appartementRepository,
                       LocataireRepository locataireRepository,
                       RevisionRepository revisionRepository,
                       ImmeubleRepository immeubleRepository) {
        super(bailRepository);
        this.bailRepository = bailRepository;
        this.appartementRepository = appartementRepository;
        this.locataireRepository = locataireRepository;
        this.revisionRepository = revisionRepository;
        this.immeubleRepository = immeubleRepository;
    }

    @Override
    public ResponseDto toDto(Object entity) {
        Bail bail = (Bail) entity;
        Appartement appartement = bail.getAppartement();
        Immeuble immeuble = appartement.getImmeuble();

        ProprietaireResponseDto proprietaireDto = ProprietaireResponseDto.builder()
                .id(immeuble.getProprietaire().getId())
                .nom(immeuble.getProprietaire().getNom())
                .prenom(immeuble.getProprietaire().getPrenom())
                .numero(immeuble.getProprietaire().getNumero())
                .email(immeuble.getProprietaire().getEmail())
                .build();

        ImmeubleResponseDto immeubleDto = ImmeubleResponseDto.builder()
                .id(immeuble.getId())
                .nom(immeuble.getNom())
                .adresse(immeuble.getAdresse())
                .nbEtages(immeuble.getNbEtages())
                .proprietaire(proprietaireDto)
                .build();

        AppartementResponseDto appDto = AppartementResponseDto.builder()
                .id(appartement.getId())
                .nom(appartement.getNom())
                .surface(appartement.getSurface())
                .nbPieces(appartement.getNbPieces())
                .etage(appartement.getEtage())
                .immeuble(immeubleDto)
                .build();

        LocataireResponseDto locataireDto = LocataireResponseDto.builder()
                .id(bail.getLocataire().getId())
                .nom(bail.getLocataire().getNom())
                .prenom(bail.getLocataire().getPrenom())
                .numero(bail.getLocataire().getNumero())
                .email(bail.getLocataire().getEmail())
                .build();

        return BailResponseDto.builder()
                .id(bail.getId())
                .dateDebut(bail.getDateDebut())
                .dateFin(bail.getDateFin())
                .montantLoyer(bail.getMontantLoyer())
                .provisionCharges(bail.getProvisionCharges())
                .depotGarantie(bail.getDepotGarantie())
                .appartement(appDto)
                .locataire(locataireDto)
                .enCours(bail.getDateDebut() != null
                        && !bail.getDateDebut().after(new Date())
                        && (bail.getDateFin() == null || !bail.getDateFin().before(new Date())))
                .build();
    }

    public Page<ResponseDto> getAll(int page, int taille, String sort) {
        Pageable pageable;
        if (sort != null && !sort.isEmpty()) {
            String[] parts = sort.split(",");
            String property = parts[0].trim();
            Sort.Direction direction = parts.length > 1 && "desc".equalsIgnoreCase(parts[1].trim())
                    ? Sort.Direction.DESC : Sort.Direction.ASC;
            pageable = PageRequest.of(pageAutorisee(page), tailleAutorisee(taille), Sort.by(direction, property));
        } else {
            pageable = PageRequest.of(pageAutorisee(page), tailleAutorisee(taille));
        }
        return bailRepository.findAll(pageable).map(this::toDto);
    }

    @Override
    public ResponseDto create(RequestDto request) {
        BailRequestDto bailRequest = (BailRequestDto) request;
        Appartement appartement = appartementRepository.findById(bailRequest.getAppartementId())
                .orElseThrow(() -> new NotFoundException("Appartement non trouvé"));
        Locataire locataire = locataireRepository.findById(bailRequest.getLocataireId())
                .orElseThrow(() -> new NotFoundException("Locataire non trouvé"));
        if (bailRequest.getDateFin() != null && !bailRequest.getDateFin().after(bailRequest.getDateDebut())) {
            throw new IllegalArgumentException("La date de fin doit être postérieure à la date de début.");
        }
        List<Bail> bauxExistants = bailRepository.findByAppartementId(bailRequest.getAppartementId());
        for (Bail existant : bauxExistants) {
            boolean overlap = false;
            if (existant.getDateFin() == null) {
                if (!bailRequest.getDateDebut().after(existant.getDateDebut())) {
                    overlap = true;
                }
            } else {
                if (chevauche(bailRequest.getDateDebut(), bailRequest.getDateFin(),
                        existant.getDateDebut(), existant.getDateFin())) {
                    overlap = true;
                }
            }
            if (overlap) {
                throw new IllegalArgumentException(
                    "Bail existant du " + existant.getDateDebut() +
                    " au " + existant.getDateFin() +
                    " bloque la période demandée.");
            }
        }
        if (bailRequest.getDateFin() != null) {
            long jours = (bailRequest.getDateFin().getTime() - bailRequest.getDateDebut().getTime())
                    / (1000L * 60 * 60 * 24);
            if (jours < 30) {
                throw new RequeteInvalideException("La durée minimum d'un bail est d'un mois");
            }
        }
        Bail bail = toEntity(bailRequest, appartement, locataire);
        return toDto(bailRepository.save(bail));
    }

    @Override
    public ResponseDto update(Long id, RequestDto request) {
        BailRequestDto bailRequest = (BailRequestDto) request;
        Bail bail = bailRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Bail non trouvé"));
        if (bailRequest.getDateFin() != null && !bailRequest.getDateFin().after(bail.getDateDebut())) {
            throw new IllegalArgumentException("La date de fin doit être postérieure à la date de début.");
        }
        if (bailRequest.getDateFin() != null) {
            long jours = (bailRequest.getDateFin().getTime() - bail.getDateDebut().getTime())
                    / (1000L * 60 * 60 * 24);
            if (jours < 30) {
                throw new RequeteInvalideException("La durée minimum d'un bail est d'un mois");
            }
        }
        if (nonCommence(bail)) {
            bail.setMontantLoyer(bailRequest.getMontantLoyer());
        }
        bail.setDateFin(bailRequest.getDateFin());
        return toDto(bailRepository.save(bail));
    }

    @Override
    public ResponseDto delete(Long id) {
        Bail bail = bailRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_MESSAGE));
        if (!nonCommence(bail)) {
            throw new IllegalArgumentException("Un bail en cours ne peut pas être supprimé, il doit être résilié.");
        }
        bailRepository.deleteById(id);
        return toDto(bail);
    }

    private Bail toEntity(BailRequestDto bailRequest, Appartement appartement, Locataire locataire) {
        return Bail.builder()
                .dateDebut(bailRequest.getDateDebut())
                .dateFin(bailRequest.getDateFin())
                .montantLoyer(bailRequest.getMontantLoyer())
                .provisionCharges(bailRequest.getProvisionCharges())
                .depotGarantie(bailRequest.getDepotGarantie())
                .appartement(appartement)
                .locataire(locataire)
                .build();
    }

    private boolean nonCommence(Bail bail) {
        return bail.getDateDebut() != null && bail.getDateDebut().after(new Date());
    }

    private boolean chevauche(Date debut1, Date fin1, Date debut2, Date fin2) {
        boolean debut1AvantFin2 = fin2 == null || debut1.before(fin2);
        boolean debut2AvantFin1 = fin1 == null || debut2.before(fin1);
        return debut1AvantFin2 && debut2AvantFin1;
    }

    public ResponseDto resiliation(Long id, String dateEffet) {
        Bail bail = bailRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Bail non trouvé"));
        Date effets;
        try {
            effets = new java.text.SimpleDateFormat("yyyy-MM-dd").parse(dateEffet);
        } catch (java.text.ParseException e) {
            throw new RequeteInvalideException("Format de date invalide, attendu yyyy-MM-dd");
        }
        if (bail.getDateFin() == null || effets.after(bail.getDateFin())) {
            throw new IllegalArgumentException("Bail déjà résilié ou date d'effet hors période");
        }
        if (nonCommence(bail)) {
            throw new IllegalArgumentException("Bail non commencé, impossible de résilier");
        }
        if (effets.before(bail.getDateDebut()) || effets.after(bail.getDateFin())) {
            throw new IllegalArgumentException("Date d'effet doit être dans la période du bail");
        }
        if (effets.before(new Date())) {
            throw new IllegalArgumentException("Date d'effet ne peut pas être antérieure à aujourd'hui");
        }
        bail.setDateFin(effets);
        return toDto(bailRepository.save(bail));
    }

    public ResponseDto renouvellement(Long id, String nouvelleDateFin) {
        Bail bail = bailRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Bail non trouvé"));
        Date nouvelleFin;
        try {
            nouvelleFin = new java.text.SimpleDateFormat("yyyy-MM-dd").parse(nouvelleDateFin);
        } catch (java.text.ParseException e) {
            throw new RequeteInvalideException("Format de date invalide, attendu yyyy-MM-dd");
        }
        if (bail.getDateFin() == null || !nouvelleFin.after(bail.getDateFin())) {
            throw new IllegalArgumentException(
                "La nouvelle date doit être postérieure à la date de fin actuelle " + bail.getDateFin());
        }
        List<Bail> bauxExistants = bailRepository.findByAppartementId(bail.getAppartement().getId());
        for (Bail existant : bauxExistants) {
            if (!existant.getId().equals(id)) {
                if (chevauche(bail.getDateFin(), nouvelleFin,
                        existant.getDateDebut(), existant.getDateFin())) {
                    throw new IllegalArgumentException(
                        "Période ajoutée chevauche le bail " + existant.getId()
                        + " du " + existant.getDateDebut() + " au " + existant.getDateFin());
                }
            }
        }
        bail.setDateFin(nouvelleFin);
        return toDto(bailRepository.save(bail));
    }

    public ResponseDto revision(Long id, BigDecimal nouveauMontant, String dateEffet) {
        Bail bail = bailRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Bail non trouvé"));
        if (nouveauMontant == null || nouveauMontant.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RequeteInvalideException("Le montant doit être strictement positif");
        }
        Date effets;
        try {
            effets = new java.text.SimpleDateFormat("yyyy-MM-dd").parse(dateEffet);
        } catch (java.text.ParseException e) {
            throw new RequeteInvalideException("Format de date invalide, attendu yyyy-MM-dd");
        }
        if (effets.before(bail.getDateDebut()) || (bail.getDateFin() != null && effets.after(bail.getDateFin()))) {
            throw new IllegalArgumentException("Date d'effet doit être comprise dans la période du bail");
        }
        bail.setMontantLoyer(nouveauMontant);
        bailRepository.save(bail);
        Revision revision = new Revision();
        revision.setBail(bail);
        revision.setDateEffet(effets);
        revision.setMontantLoyer(nouveauMontant);
        revisionRepository.save(revision);
        return toDto(bailRepository.save(bail));
    }

    public List<ResponseDto> getBauxByAppartement(Long appartementId) {
        appartementRepository.findById(appartementId)
                .orElseThrow(() -> new NotFoundException("Appartement non trouvé"));
        List<Bail> baux = bailRepository.findByAppartementIdOrderByDateDebutDesc(appartementId);
        return baux.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<Appartement> getAppartementsLibres(LocalDate date) {
        LocalDate ref = date != null ? date : LocalDate.now();
        java.util.Date refDate = java.util.Date.from(
                ref.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant());
        return bailRepository.findLibresByDate(refDate);
    }

    /**
     * F18. Reconstituer l'occupation d'un appartement.
     * Pour chaque période, le locataire et le loyer applicable,
     * les périodes de vacance apparaissant explicitement.
     */
    public List<PeriodOccupation> getOccupationAppartement(Long appartementId) {
        Appartement appartement = appartementRepository.findById(appartementId)
                .orElseThrow(() -> new NotFoundException("Appartement non trouvé"));

        List<Bail> baux = bailRepository.findByAppartementIdOrderByDateDebutDesc(appartementId);
        // Tri chronologique (du plus ancien au plus récent)
        baux.sort(Comparator.comparing(Bail::getDateDebut));

        List<PeriodOccupation> periods = new ArrayList<>();

        if (baux.isEmpty()) {
            periods.add(PeriodOccupation.builder()
                    .dateDebut(appartement.getDateCreation() != null
                            ? convertDateToLocalDate(appartement.getDateCreation())
                            : LocalDate.now())
                    .dateFin(LocalDate.now())
                    .libelle("VACANT")
                    .nomLocataire(null)
                    .loyer(BigDecimal.ZERO)
                    .build());
            return periods;
        }

        // Vacance avant le premier bail
        LocalDate debutPremierBail = convertDateToLocalDate(baux.get(0).getDateDebut());
        LocalDate dateCreation = appartement.getDateCreation() != null
                ? convertDateToLocalDate(appartement.getDateCreation())
                : debutPremierBail;
        if (dateCreation.isBefore(debutPremierBail)) {
            periods.add(PeriodOccupation.builder()
                    .dateDebut(dateCreation)
                    .dateFin(debutPremierBail)
                    .libelle("VACANT")
                    .nomLocataire(null)
                    .loyer(BigDecimal.ZERO)
                    .build());
        }

        for (int i = 0; i < baux.size(); i++) {
            Bail bail = baux.get(i);
            String nomLocataire = bail.getLocataire() != null
                    ? bail.getLocataire().getNom() + " " + bail.getLocataire().getPrenom()
                    : null;

            periods.add(PeriodOccupation.builder()
                    .dateDebut(convertDateToLocalDate(bail.getDateDebut()))
                    .dateFin(bail.getDateFin() != null ? convertDateToLocalDate(bail.getDateFin()) : LocalDate.now())
                    .libelle("OCCUPE")
                    .nomLocataire(nomLocataire)
                    .loyer(bail.getMontantLoyer() != null ? bail.getMontantLoyer() : BigDecimal.ZERO)
                    .build());

            // Vacance entre deux baux
            if (i < baux.size() - 1) {
                LocalDate finBail = bail.getDateFin() != null ? convertDateToLocalDate(bail.getDateFin()) : null;
                LocalDate debutSuivant = convertDateToLocalDate(baux.get(i + 1).getDateDebut());
                if (finBail != null && finBail.isBefore(debutSuivant)) {
                    periods.add(PeriodOccupation.builder()
                            .dateDebut(finBail)
                            .dateFin(debutSuivant)
                            .libelle("VACANT")
                            .nomLocataire(null)
                            .loyer(BigDecimal.ZERO)
                            .build());
                }
            }
        }

        // Vacance après le dernier bail
        Bail dernierBail = baux.get(baux.size() - 1);
        if (dernierBail.getDateFin() != null) {
            LocalDate finDernier = convertDateToLocalDate(dernierBail.getDateFin());
            if (finDernier.isBefore(LocalDate.now())) {
                periods.add(PeriodOccupation.builder()
                        .dateDebut(finDernier)
                        .dateFin(LocalDate.now())
                        .libelle("VACANT")
                        .nomLocataire(null)
                        .loyer(BigDecimal.ZERO)
                        .build());
            }
        }

        return periods;
    }

    private LocalDate convertDateToLocalDate(Date date) {
        if (date == null) return null;
        return date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * F19. Calculer le revenu locatif d'un immeuble sur un mois.
     * Somme des loyers des baux actifs au prorata des jours pour les baux partiels.
     */
    public RevenuImmeubleDto getRevenuImmeuble(Long immeubleId, String mois) {
        Immeuble immeuble = immeubleRepository.findById(immeubleId)
                .orElseThrow(() -> new NotFoundException("Immeuble non trouvé"));

        YearMonth yearMonth;
        if (mois != null) {
            try {
                yearMonth = YearMonth.parse(mois);
            } catch (Exception e) {
                throw new RequeteInvalideException("Mois doit être au format yyyy-MM");
            }
        } else {
            yearMonth = YearMonth.now();
        }

        LocalDate firstDay = yearMonth.atDay(1);
        LocalDate lastDay = yearMonth.atEndOfMonth();
        long joursMois = java.time.temporal.ChronoUnit.DAYS.between(firstDay, lastDay) + 1;

        List<Bail> baux = bailRepository.findByImmeubleId(immeubleId);
        List<Appartement> appartements = appartementRepository.findByImmeubleId(immeubleId);

        List<AppartementRevenuDto> details = new ArrayList<>();
        BigDecimal totalRevenu = BigDecimal.ZERO;
        long appartementsOccupes = 0;

        for (Appartement app : appartements) {
            BigDecimal revenuAppartement = BigDecimal.ZERO;
            boolean occupe = false;
            BigDecimal dernierLoyer = BigDecimal.ZERO;
            long dernierJoursBail = 0;

            for (Bail bail : baux) {
                if (!bail.getAppartement().getId().equals(app.getId())) continue;

                LocalDate debutBail = convertDateToLocalDate(bail.getDateDebut());
                LocalDate finBail = bail.getDateFin() != null ? convertDateToLocalDate(bail.getDateFin()) : null;

                if (debutBail != null && debutBail.isAfter(lastDay)) continue;
                if (finBail != null && finBail.isBefore(firstDay)) continue;

                occupe = true;
                dernierLoyer = bail.getMontantLoyer() != null ? bail.getMontantLoyer() : BigDecimal.ZERO;

                LocalDate debutEffectif = debutBail.isBefore(firstDay) ? firstDay : debutBail;
                LocalDate finEffectif = (finBail == null || finBail.isAfter(lastDay)) ? lastDay : finBail;
                long joursBail = java.time.temporal.ChronoUnit.DAYS.between(debutEffectif, finEffectif) + 1;
                dernierJoursBail = joursBail;

                if (joursMois > 0 && bail.getMontantLoyer() != null) {
                    BigDecimal loyerProrata = bail.getMontantLoyer()
                            .multiply(BigDecimal.valueOf(joursBail))
                            .divide(BigDecimal.valueOf(joursMois), 2, BigDecimal.ROUND_HALF_UP);
                    revenuAppartement = revenuAppartement.add(loyerProrata);
                }
            }

            if (occupe) {
                appartementsOccupes++;
                totalRevenu = totalRevenu.add(revenuAppartement);

                details.add(AppartementRevenuDto.builder()
                        .appartementId(app.getId())
                        .appartementNom(app.getNom())
                        .surface(app.getSurface() != null ? app.getSurface() : BigDecimal.ZERO)
                        .loyerMensuel(dernierLoyer)
                        .joursBail(dernierJoursBail)
                        .joursMois(joursMois)
                        .apportPartiel(revenuAppartement)
                        .build());
            }
        }

        double tauxOccupation = !appartements.isEmpty()
                ? (double) appartementsOccupes / appartements.size() * 100
                : 0;

        return RevenuImmeubleDto.builder()
                .immeubleId(immeubleId)
                .immeubleNom(immeuble.getNom())
                .mois(yearMonth.toString())
                .totalRevenu(totalRevenu)
                .details(details)
                .tauxOccupation(tauxOccupation)
                .build();
    }
}
