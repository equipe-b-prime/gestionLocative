package com.gestionLocative.gestionLocative.service;

import com.gestionLocative.gestionLocative.dto.AppartementRequestDto;
import com.gestionLocative.gestionLocative.dto.AppartementResponseDto;
import com.gestionLocative.gestionLocative.dto.BailResponseDto;
import com.gestionLocative.gestionLocative.dto.LocataireResponseDto;
import com.gestionLocative.gestionLocative.dto.RequestDto;
import com.gestionLocative.gestionLocative.dto.ResponseDto;
import com.gestionLocative.gestionLocative.entity.Appartement;
import com.gestionLocative.gestionLocative.entity.Immeuble;
import com.gestionLocative.gestionLocative.entity.Bail;
import com.gestionLocative.gestionLocative.dto.ImmeubleResponseDto;
import com.gestionLocative.gestionLocative.exception.RessourceUtiliseeException;
import com.gestionLocative.gestionLocative.exception.NotFoundException;
import com.gestionLocative.gestionLocative.repository.AppartementRepository;
import com.gestionLocative.gestionLocative.repository.BailRepository;
import com.gestionLocative.gestionLocative.repository.ImmeubleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.PageImpl;

/**
 * Service métier gérant les appartements.
 * Fournit la création, la modification, la suppression et les conversions entité/DTO.
 */
@Service
public class AppartementService extends AbstractCrudService {

    private final AppartementRepository appartementRepository;
    private final ImmeubleRepository immeubleRepository;
    private final BailRepository bailRepository;

    /**
     * Constructeur injectant les repositories de l'appartement, de l'immeuble et des baux.
     *
     * @param appartementRepository le repository des appartements
     * @param immeubleRepository le repository des immeubles
     * @param bailRepository le repository des baux
     */
    public AppartementService(AppartementRepository appartementRepository,
                              ImmeubleRepository immeubleRepository,
                              BailRepository bailRepository) {
        super(appartementRepository);
        this.appartementRepository = appartementRepository;
        this.immeubleRepository = immeubleRepository;
        this.bailRepository = bailRepository;
    }

    /**
     * Convertit une requête d'appartement en entité.
     *
     * @param appartementRequest La requête de l'appartement.
     * @param immeuble L'immeuble associé.
     * @return L'entité Appartement construite.
     */
    public Appartement toEntity(AppartementRequestDto appartementRequest, Immeuble immeuble) {
        return Appartement.builder()
                .nom(appartementRequest.getNom())
                .surface(appartementRequest.getSurface())
                .nbPieces(appartementRequest.getNbPieces())
                .etage(appartementRequest.getEtage())
                .immeuble(immeuble)
                .build();
    }

    /**
     * Convertit une entité Appartement en réponse DTO, avec l'immeuble imbriqué.
     *
     * @param entity L'entité de l'appartement.
     * @return La réponse DTO de l'appartement.
     */
    @Override
    public ResponseDto toDto(Object entity) {
        Appartement appartement = (Appartement) entity;
        return AppartementResponseDto.builder()
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
                        .build())
                .build();
    }

    /**
     * Récupère une page d'appartements triés par étage puis par surface.
     *
     * @param page l'index de la page souhaitée
     * @param taille le nombre d'éléments par page
     * @return une page d'objets {@link ResponseDto} triée par étage puis surface
     */
    @Override
    public Page<ResponseDto> getAll(int page, int taille) {
        Pageable pageable = PageRequest.of(pageAutorisee(page), tailleAutorisee(taille),
                Sort.by("etage", "surface"));
        return appartementRepository.findAll(pageable).map(this::toDto);
    }

    /**
     * Récupère les appartements correspondant à des critères de recherche.
     *
     * @param page l'index de la page souhaitée
     * @param taille le nombre d'éléments par page
     * @param surfaceMin surface minimale (optionnel, en m²)
     * @param surfaceMax surface maximale (optionnel, en m²)
     * @param pieces nombre de pièces (optionnel)
     * @return une page d'appartements filtrés
     */
public Page<ResponseDto> findBySearchCriteria(int page, int taille, Double surfaceMin, Double surfaceMax, Integer pieces) {
        Pageable pageable = PageRequest.of(pageAutorisee(page), tailleAutorisee(taille),
                Sort.by("etage", "surface"));
        Page<Appartement> appartementPage = appartementRepository.findAll(pageable);

        // Filtrer les entités d'abord, puis convertir en DTO
        List<Appartement> filteredApartments = appartementPage.getContent().stream()
                .filter(a -> surfaceMin == null || a.getSurface() != null && a.getSurface().compareTo(new BigDecimal(surfaceMin)) >= 0)
                .filter(a -> surfaceMax == null || a.getSurface() != null && a.getSurface().compareTo(new BigDecimal(surfaceMax)) <= 0)
                .filter(a -> pieces == null || a.getNbPieces() == pieces)
                .collect(Collectors.toList());

        // Convertir les appartements filtrés en DTO
        List<ResponseDto> dtoList = filteredApartments.stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        // Si des critères ont été fournis, on utilise la taille filtrée pour la pagination
        if (surfaceMin != null || surfaceMax != null || pieces != null) {
            return new PageImpl<>(dtoList, pageable, filteredApartments.size());
        }

        // Sinon, on retourne la page complète
        return (Page<ResponseDto>) (Page<?>) appartementPage.map(this::toDto);
    }
    /**
     * Crée un nouvel appartement après vérification de l'immeuble et de l'étage.
     *
     * @param request La requête contenant les données et l'identifiant de l'immeuble.
     * @return L'appartement créé, converti en DTO.
     * @throws NotFoundException si l'immeuble n'existe pas
     * @throws IllegalArgumentException si l'étage dépasse le nombre d'étages de l'immeuble
     */
    @Override
    public ResponseDto create(RequestDto request) {
        AppartementRequestDto appartementRequest = (AppartementRequestDto) request;
        Immeuble immeuble = immeubleRepository.findById(appartementRequest.getImmeubleId())
                .orElseThrow(() -> new NotFoundException("Immeuble non trouvé"));
        if (appartementRequest.getEtage() > immeuble.getNbEtages()) {
            throw new IllegalArgumentException("L'étage (" + appartementRequest.getEtage()
                    + ") ne peut pas dépasser le nombre d'étages de l'immeuble (" + immeuble.getNbEtages() + ").");
        }
        Appartement appartement = toEntity(appartementRequest, immeuble);
        return toDto(appartementRepository.save(appartement));
    }

    /**
     * Met à jour un appartement existant. L'immeuble est figé (non modifiable) ;
     * la surface, le nombre de pièces et l'étage restent modifiables.
     *
     * @param id L'identifiant de l'appartement à mettre à jour.
     * @param request La requête contenant les nouvelles données.
     * @return L'appartement mis à jour, converti en DTO.
     * @throws NotFoundException si l'appartement n'existe pas
     * @throws IllegalArgumentException si l'étage dépasse le nombre d'étages de l'immeuble
     */
    @Override
    public ResponseDto update(Long id, RequestDto request) {
        AppartementRequestDto appartementRequest = (AppartementRequestDto) request;
        Appartement appartement = appartementRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Appartement non trouvé"));
        Immeuble immeuble = appartement.getImmeuble();
        if (appartementRequest.getEtage() > immeuble.getNbEtages()) {
            throw new IllegalArgumentException("L'étage (" + appartementRequest.getEtage()
                    + ") ne peut pas dépasser le nombre d'étages de l'immeuble (" + immeuble.getNbEtages() + ").");
        }
        appartement.setNom(appartementRequest.getNom());
        appartement.setSurface(appartementRequest.getSurface());
        appartement.setNbPieces(appartementRequest.getNbPieces());
        appartement.setEtage(appartementRequest.getEtage());
        return toDto(appartementRepository.save(appartement));
    }

    /**
     * Supprime un appartement après vérification qu'il n'a fait l'objet d'aucun bail.
     *
     * @param id L'identifiant de l'appartement à supprimer.
     * @return ResponseDto Les données de l'appartement supprimé.
     * @throws NotFoundException si l'appartement n'existe pas
     * @throws RessourceUtiliseeException si un bail existe pour cet appartement
     */
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

    /**
     * Récupère les appartements d'un immeuble, triés par étage croissant,
     * avec l'état d'occupation de chacun (occupé si au moins un bail en cours).
     *
     * @param immeubleId L'identifiant de l'immeuble.
     * @return La liste des appartements de l'immeuble.
     * @throws NotFoundException si l'immeuble n'existe pas.
     */
    public List<ResponseDto> getAppartementsByImmeuble(Long immeubleId) {
        Immeuble immeuble = immeubleRepository.findById(immeubleId)
                .orElseThrow(() -> new NotFoundException("Immeuble non trouvé"));
        List<Appartement> appartements = appartementRepository.findByImmeubleIdOrderByEtageAsc(immeubleId);
        Date maintenant = new Date();
        return appartements.stream()
                .map(appartement -> {
                    AppartementResponseDto dto = (AppartementResponseDto) toDto(appartement);
                    boolean etageOccupe = bailRepository.findByAppartementId(appartement.getId()).stream()
                            .anyMatch(b -> b.getDateDebut() != null && !b.getDateDebut().after(maintenant)
                                    && (b.getDateFin() == null || !b.getDateFin().before(maintenant)));
                    dto.setEtageOccupe(etageOccupe);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    /**
     * Récupère les baux d'un appartement, triés par date de début décroissante,
     * avec leur état d'occupation (en cours ou non).
     *
     * @param id L'identifiant de l'appartement.
     * @return La liste des baux de l'appartement.
     * @throws NotFoundException si l'appartement n'existe pas.
     */
    public List<ResponseDto> getBauxByAppartement(Long id) {
        Appartement appartement = appartementRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Appartement non trouvé"));
        List<Bail> baux = bailRepository.findByAppartementIdOrderByDateDebutDesc(id);
        Date maintenant = new Date();
        return baux.stream()
                .map(bail -> BailResponseDto.builder()
                        .id(bail.getId())
                        .dateDebut(bail.getDateDebut())
                        .dateFin(bail.getDateFin())
                        .montantLoyer(bail.getMontantLoyer())
                        .locataire(LocataireResponseDto.builder()
                                .id(bail.getLocataire().getId())
                                .nom(bail.getLocataire().getNom())
                                .prenom(bail.getLocataire().getPrenom())
                                .build())
                        .enCours(bail.getDateDebut() != null
                                && !bail.getDateDebut().after(maintenant)
                                && (bail.getDateFin() == null || !bail.getDateFin().before(maintenant)))
                        .build())
                .collect(Collectors.toList());
    }
}