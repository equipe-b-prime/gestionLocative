package com.gestionLocative.gestionLocative.service;

import com.gestionLocative.gestionLocative.dto.*;
import com.gestionLocative.gestionLocative.entity.*;
import com.gestionLocative.gestionLocative.exception.NotFoundException;
import com.gestionLocative.gestionLocative.exception.RequeteInvalideException;
import com.gestionLocative.gestionLocative.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Niveau 4 - restitution : appartements libres (F17), occupation reconstituée (F18), revenu d'un immeuble (F19).
 * Les périodes de bail sont [dateDebut, dateFin[ (dateFin exclue), cohérent avec le contrôle de chevauchement.
 */
@Service
@Transactional(readOnly = true)
public class OccupationService {

    private static final ZoneId ZONE = ZoneId.systemDefault();

    private final AppartementRepository appartementRepository;
    private final ImmeubleRepository immeubleRepository;
    private final BailRepository bailRepository;
    private final RevisionRepository revisionRepository;

    public OccupationService(AppartementRepository appartementRepository, ImmeubleRepository immeubleRepository,
                             BailRepository bailRepository, RevisionRepository revisionRepository) {
        this.appartementRepository = appartementRepository;
        this.immeubleRepository = immeubleRepository;
        this.bailRepository = bailRepository;
        this.revisionRepository = revisionRepository;
    }

    private static LocalDate local(Date d) {
        return d == null ? null : d.toInstant().atZone(ZONE).toLocalDate();
    }

    // ------------------------------------------------------------------ F17

    public List<ResponseDto> appartementsLibres(LocalDate date) {
        LocalDate jour = date != null ? date : LocalDate.now();
        Date instant = Date.from(jour.atStartOfDay(ZONE).toInstant());
        Map<Long, AppartementsLibresParImmeubleDto> groupes = new LinkedHashMap<>();
        for (Appartement a : appartementRepository.findLibres(instant)) {
            Immeuble immeuble = a.getImmeuble();
            AppartementsLibresParImmeubleDto groupe = groupes.computeIfAbsent(immeuble.getId(), k ->
                    AppartementsLibresParImmeubleDto.builder()
                            .immeubleId(immeuble.getId()).immeubleNom(immeuble.getNom())
                            .adresse(immeuble.getAdresse()).appartements(new ArrayList<>()).build());
            groupe.getAppartements().add(AppartementLibreDto.builder()
                    .id(a.getId()).nom(a.getNom()).surface(a.getSurface()).nbPieces(a.getNbPieces())
                    .etage(a.getEtage()).dernierLoyer(dernierLoyerConnu(a.getId())).build());
        }
        return new ArrayList<>(groupes.values());
    }

    /** Loyer du bail le plus récent, révisions comprises ; null si jamais loué. */
    private BigDecimal dernierLoyerConnu(Long appartementId) {
        return bailRepository.findByAppartementIdOrderByDateDebutDesc(appartementId).stream().findFirst()
                .map(b -> revisionRepository.findTopByBailIdOrderByDateEffetDesc(b.getId())
                        .map(Revision::getMontantLoyer).orElse(b.getMontantLoyer()))
                .orElse(null);
    }

    // ------------------------------------------------------------------ F18

    public List<ResponseDto> occupation(Long appartementId) {
        Appartement a = appartementRepository.findById(appartementId)
                .orElseThrow(() -> new NotFoundException("Appartement non trouvé"));
        return new ArrayList<>(periodes(a));
    }

    /** Frise chronologique : périodes occupées (par loyer applicable) et périodes de vacance explicites. */
    List<PeriodeOccupationDto> periodes(Appartement a) {
        LocalDate creation = local(a.getDateCreation());
        List<Bail> baux = bailRepository.findByAppartementIdOrderByDateDebutAsc(a.getId());
        List<PeriodeOccupationDto> res = new ArrayList<>();
        if (baux.isEmpty()) {
            res.add(vacance(creation, null));
            return res;
        }
        LocalDate curseur = creation;
        LocalDate premierDebut = local(baux.get(0).getDateDebut());
        if (premierDebut.isBefore(curseur)) curseur = premierDebut;
        for (Bail bail : baux) {
            LocalDate debut = local(bail.getDateDebut());
            LocalDate fin = local(bail.getDateFin());
            if (curseur != null && curseur.isBefore(debut)) res.add(vacance(curseur, debut));
            res.addAll(segmentsOccupes(bail, debut, fin));
            curseur = fin;
            if (fin == null) break;
        }
        if (curseur != null) res.add(vacance(curseur, null));
        return res;
    }

    private PeriodeOccupationDto vacance(LocalDate debut, LocalDate fin) {
        return PeriodeOccupationDto.builder().type("VACANT").dateDebut(debut).dateFin(fin).build();
    }

    /** Découpe un bail en sous-périodes à chaque révision de loyer. */
    private List<PeriodeOccupationDto> segmentsOccupes(Bail bail, LocalDate debut, LocalDate fin) {
        Locataire l = bail.getLocataire();
        LocataireResponseDto loc = LocataireResponseDto.builder().id(l.getId()).nom(l.getNom())
                .prenom(l.getPrenom()).numero(l.getNumero()).email(l.getEmail()).build();
        List<PeriodeOccupationDto> segs = new ArrayList<>();
        BigDecimal loyer = bail.getMontantLoyer();
        LocalDate segDebut = debut;
        for (Revision r : revisionRepository.findByBailIdOrderByDateEffetAsc(bail.getId())) {
            LocalDate d = local(r.getDateEffet());
            if (!d.isAfter(debut)) {
                loyer = r.getMontantLoyer();
            } else if (fin == null || d.isBefore(fin)) {
                segs.add(occupe(bail, loc, segDebut, d, loyer));
                segDebut = d;
                loyer = r.getMontantLoyer();
            }
        }
        segs.add(occupe(bail, loc, segDebut, fin, loyer));
        return segs;
    }

    private PeriodeOccupationDto occupe(Bail bail, LocataireResponseDto loc, LocalDate debut, LocalDate fin, BigDecimal loyer) {
        return PeriodeOccupationDto.builder().type("OCCUPE").dateDebut(debut).dateFin(fin)
                .bailId(bail.getId()).locataire(loc).loyer(loyer).build();
    }

    // ------------------------------------------------------------------ F19

    public ResponseDto revenu(Long immeubleId, String mois) {
        Immeuble immeuble = immeubleRepository.findById(immeubleId)
                .orElseThrow(() -> new NotFoundException("Immeuble non trouvé"));
        YearMonth ym;
        try {
            ym = YearMonth.parse(mois);
        } catch (DateTimeParseException | NullPointerException e) {
            throw new RequeteInvalideException("Le mois doit être au format yyyy-MM (ex: 2026-11).");
        }
        LocalDate debutMois = ym.atDay(1);
        LocalDate finMois = ym.plusMonths(1).atDay(1);
        int joursDuMois = ym.lengthOfMonth();

        List<Appartement> appartements = appartementRepository.findByImmeubleIdOrderByEtageAscSurfaceAsc(immeubleId);
        List<RevenuAppartementDto> details = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        long joursOccupesTotal = 0;
        for (Appartement a : appartements) {
            BigDecimal loyerJours = BigDecimal.ZERO; // somme loyer x jours
            long jours = 0;
            for (PeriodeOccupationDto p : periodes(a)) {
                if (!"OCCUPE".equals(p.getType())) continue;
                LocalDate s = p.getDateDebut().isAfter(debutMois) ? p.getDateDebut() : debutMois;
                LocalDate e = (p.getDateFin() == null || p.getDateFin().isAfter(finMois)) ? finMois : p.getDateFin();
                long n = Math.max(0, ChronoUnit.DAYS.between(s, e));
                if (n > 0) {
                    jours += n;
                    loyerJours = loyerJours.add(p.getLoyer().multiply(BigDecimal.valueOf(n)));
                }
            }
            BigDecimal revenu = loyerJours.divide(BigDecimal.valueOf(joursDuMois), 2, RoundingMode.HALF_UP);
            details.add(RevenuAppartementDto.builder().appartementId(a.getId()).nom(a.getNom())
                    .joursOccupes(jours).revenu(revenu).build());
            total = total.add(revenu);
            joursOccupesTotal += jours;
        }
        BigDecimal taux = appartements.isEmpty() ? BigDecimal.ZERO.setScale(2)
                : BigDecimal.valueOf(joursOccupesTotal * 100L)
                        .divide(BigDecimal.valueOf((long) appartements.size() * joursDuMois), 2, RoundingMode.HALF_UP);
        return RevenuImmeubleDto.builder().immeubleId(immeuble.getId()).mois(ym.toString())
                .nbAppartements(appartements.size()).revenuTotal(total.setScale(2, RoundingMode.HALF_UP))
                .tauxOccupation(taux).details(details).build();
    }
}
