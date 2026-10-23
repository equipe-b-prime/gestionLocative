package com.gestionLocative.gestionLocative.service;

import com.gestionLocative.gestionLocative.exception.NotFoundException;

import com.gestionLocative.gestionLocative.repository.AppartementRepository;
import com.gestionLocative.gestionLocative.dto.AppartementResponseDto;
import com.gestionLocative.gestionLocative.dto.BailRequestDto;
import com.gestionLocative.gestionLocative.dto.BailResponseDto;
import com.gestionLocative.gestionLocative.entity.Appartement;
import com.gestionLocative.gestionLocative.entity.Bail;
import com.gestionLocative.gestionLocative.entity.Locataire;
import com.gestionLocative.gestionLocative.dto.ImmeubleResponseDto;
import com.gestionLocative.gestionLocative.repository.LocataireRepository;
import com.gestionLocative.gestionLocative.dto.LocataireResponseDto;
import com.gestionLocative.gestionLocative.dto.ProprietaireResponseDto;
import com.gestionLocative.gestionLocative.dto.RequestDto;
import com.gestionLocative.gestionLocative.dto.ResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import com.gestionLocative.gestionLocative.repository.BailRepository;

/**
 * Service métier gérant les baux.
 * Fournit la création, la modification, la suppression et les conversions entité/DTO.
 */
@Service
public class BailService extends AbstractCrudService {

    private final BailRepository bailRepository;
    private final AppartementRepository appartementRepository;
    private final LocataireRepository locataireRepository;

    /**
     * Constructeur injectant les repositories du bail, de l'appartement et du locataire.
     *
     * @param bailRepository le repository des baux
     * @param appartementRepository le repository des appartements
     * @param locataireRepository le repository des locataires
     */
    public BailService(BailRepository bailRepository,
                       AppartementRepository appartementRepository,
                       LocataireRepository locataireRepository) {
        super(bailRepository);
        this.bailRepository = bailRepository;
        this.appartementRepository = appartementRepository;
        this.locataireRepository = locataireRepository;
    }

    /**
     * Convertit une requête de bail en entité.
     *
     * @param bailRequest La requête du bail.
     * @param appartement L'appartement associé.
     * @param locataire Le locataire associé.
     * @return L'entité Bail construite.
     */
    public Bail toEntity(BailRequestDto bailRequest, Appartement appartement, Locataire locataire) {
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

    /**
     * Convertit une entité Bail en réponse DTO, avec l'appartement (et son immeuble)
     * et le locataire imbriqués.
     *
     * @param entity L'entité du bail.
     * @return La réponse DTO du bail.
     */
    @Override
    public ResponseDto toDto(Object entity) {
        Bail bail = (Bail) entity;
        Appartement appartement = bail.getAppartement();
        return BailResponseDto.builder()
                .id(bail.getId())
                .dateDebut(bail.getDateDebut())
                .dateFin(bail.getDateFin())
                .montantLoyer(bail.getMontantLoyer())
                .provisionCharges(bail.getProvisionCharges())
                .depotGarantie(bail.getDepotGarantie())

                .appartement(AppartementResponseDto.builder()
                        .id(appartement.getId())
                        .nom(appartement.getNom())
                        .surface(appartement.getSurface())
                        .nbPieces(appartement.getNbPieces())
                        .etage(appartement.getEtage())

                        .immeuble(ImmeubleResponseDto.builder()
                                .id(appartement.getImmeuble().getId())
                                .nom(appartement.getImmeuble().getNom())
                                .adresse(appartement.getImmeuble().getAdresse())
                                .nbEtages(appartement.getImmeuble().getNbEtages())

                                .proprietaire(ProprietaireResponseDto.builder()
                                        .id(appartement.getImmeuble().getProprietaire().getId())
                                        .nom(appartement.getImmeuble().getProprietaire().getNom())
                                        .prenom(appartement.getImmeuble().getProprietaire().getPrenom())
                                        .numero(appartement.getImmeuble().getProprietaire().getNumero())
                                        .email(appartement.getImmeuble().getProprietaire().getEmail())
                                        .build())

                                .build())

                        .build())

                .locataire(LocataireResponseDto.builder()
                        .id(bail.getLocataire().getId())
                        .nom(bail.getLocataire().getNom())
                        .prenom(bail.getLocataire().getPrenom())
                        .numero(bail.getLocataire().getNumero())
                        .email(bail.getLocataire().getEmail())
                        .build())

                .build();
    }

    /**
     * Récupère une page de baux triés par date de début décroissante.
     *
     * @param page l'index de la page souhaitée
     * @param taille le nombre d'éléments par page
     * @return une page d'objets {@link ResponseDto} triée par date de début décroissante
     */
    @Override
    public Page<ResponseDto> getAll(int page, int taille) {
        Pageable pageable = PageRequest.of(pageAutorisee(page), tailleAutorisee(taille),
                Sort.by("dateDebut").descending());
        return bailRepository.findAll(pageable).map(this::toDto);
    }

    /**
     * Crée un nouveau bail après contrôle des dates et de la disponibilité de l'appartement.
     *
     * @param request La requête contenant les données et les identifiants des relations.
     * @return Le bail créé, converti en DTO.
     * @throws NotFoundException si l'appartement ou le locataire n'existe pas
     * @throws IllegalArgumentException si la date de fin n'est pas postérieure au début
     *         ou si l'appartement n'est pas libre sur la période
     */
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
        for (Bail existant : bailRepository.findByAppartementId(bailRequest.getAppartementId())) {
            if (chevauche(bailRequest.getDateDebut(), bailRequest.getDateFin(),
                    existant.getDateDebut(), existant.getDateFin())) {
                throw new IllegalArgumentException("L'appartement n'est pas libre sur la période demandée.");
            }
        }
        Bail bail = toEntity(bailRequest, appartement, locataire);
        return toDto(bailRepository.save(bail));
    }

    /**
     * Met à jour un bail. Tant que le bail n'a pas commencé, le loyer et la date de fin
     * sont modifiables ; une fois commencé, seule la date de fin reste modifiable.
     *
     * @param id L'identifiant du bail à mettre à jour.
     * @param request La requête contenant les nouvelles données.
     * @return Le bail mis à jour, converti en DTO.
     * @throws NotFoundException si le bail n'existe pas
     * @throws IllegalArgumentException si la date de fin n'est pas postérieure au début
     */
    @Override
    public ResponseDto update(Long id, RequestDto request) {
        BailRequestDto bailRequest = (BailRequestDto) request;
        Bail bail = bailRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Bail non trouvé"));
        if (bailRequest.getDateFin() != null && !bailRequest.getDateFin().after(bail.getDateDebut())) {
            throw new IllegalArgumentException("La date de fin doit être postérieure à la date de début.");
        }
        if (nonCommence(bail)) {
            bail.setMontantLoyer(bailRequest.getMontantLoyer());
        }
        bail.setDateFin(bailRequest.getDateFin());
        return toDto(bailRepository.save(bail));
    }

    /**
     * Supprime un bail uniquement s'il n'a pas commencé. Un bail en cours doit être résilié,
     * il ne peut pas être supprimé.
     *
     * @param id L'identifiant du bail à supprimer.
     * @return ResponseDto Les données du bail supprimé.
     * @throws NotFoundException si le bail n'existe pas
     * @throws IllegalArgumentException si le bail a déjà commencé
     */
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

    /**
     * Indique si un bail n'a pas encore commencé (date de début strictement postérieure à aujourd'hui).
     *
     * @param bail le bail à contrôler
     * @return true si le bail n'a pas commencé
     */
    private boolean nonCommence(Bail bail) {
        return bail.getDateDebut() != null && bail.getDateDebut().after(new Date());
    }

    /**
     * Indique si deux périodes se chevauchent. Une date de fin nulle signifie « pas de fin »
     * (période ouverte).
     *
     * @param debut1 début de la première période
     * @param fin1 fin de la première période (peut être null)
     * @param debut2 début de la seconde période
     * @param fin2 fin de la seconde période (peut être null)
     * @return true si les deux périodes se chevauchent
     */
    private boolean chevauche(Date debut1, Date fin1, Date debut2, Date fin2) {
        boolean debut1AvantFin2 = fin2 == null || debut1.before(fin2);
        boolean debut2AvantFin1 = fin1 == null || debut2.before(fin1);
        return debut1AvantFin2 && debut2AvantFin1;
    }

    /**
     * Récupère les baux d'un appartement, triés par date de début décroissante.
     * Chaque bail indique s'il est en cours.
     *
     * @param appartementId L'identifiant de l'appartement.
     * @return La liste des baux de l'appartement.
     * @throws NotFoundException si l'appartement n'existe pas.
     */
public List<ResponseDto> getBauxByAppartement(Long appartementId) {
        Appartement appartement = appartementRepository.findById(appartementId)
                .orElseThrow(() -> new NotFoundException("Appartement non trouvé"));
        List<Bail> baux = bailRepository.findByAppartementIdOrderByDateDebutDesc(appartementId);
        Date maintenant = new Date();
        return baux.stream()
                .map(bail -> BailResponseDto.builder()
                        .id(bail.getId())
                        .dateDebut(bail.getDateDebut())
                        .dateFin(bail.getDateFin())
                        .montantLoyer(bail.getMontantLoyer())
                        .provisionCharges(bail.getProvisionCharges())
                        .depotGarantie(bail.getDepotGarantie())
                        .appartement(AppartementResponseDto.builder()
                                .id(bail.getAppartement().getId())
                                .nom(bail.getAppartement().getNom())
                                .surface(bail.getAppartement().getSurface())
                                .nbPieces(bail.getAppartement().getNbPieces())
                                .etage(bail.getAppartement().getEtage())
                                .build())
                        .locataire(LocataireResponseDto.builder()
                                .id(bail.getLocataire().getId())
                                .nom(bail.getLocataire().getNom())
                                .prenom(bail.getLocataire().getPrenom())
                                .numero(bail.getLocataire().getNumero())
                                .email(bail.getLocataire().getEmail())
                                .build())
                        .enCours(bail.getDateDebut() != null
                                && !bail.getDateDebut().after(maintenant)
                                && (bail.getDateFin() == null || !bail.getDateFin().before(maintenant)))
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Récupère une page de baux triés selon un critère donné.
     *
     * @param page l'index de la page souhaitée (0 par défaut)
     * @param taille le nombre d'éléments par page (10 par défaut, borné à 100)
     * @param sort le tri sous la forme "propriete,direction" (ex: dateDebut,desc)
     * @return une page d'objets {@link ResponseDto} triés selon le critère donné
     */
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
}
