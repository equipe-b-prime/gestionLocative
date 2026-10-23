package com.gestionLocative.gestionLocative.service;

import com.gestionLocative.gestionLocative.dto.ImmeubleRequestDto;
import com.gestionLocative.gestionLocative.dto.ImmeubleResponseDto;
import com.gestionLocative.gestionLocative.dto.ImmeubleAvecStatsResponseDto;
import com.gestionLocative.gestionLocative.dto.ProprietaireResponseDto;
import com.gestionLocative.gestionLocative.dto.RequestDto;
import com.gestionLocative.gestionLocative.dto.ResponseDto;
import com.gestionLocative.gestionLocative.entity.Appartement;
import com.gestionLocative.gestionLocative.entity.Immeuble;
import com.gestionLocative.gestionLocative.entity.Proprietaire;
import com.gestionLocative.gestionLocative.repository.AppartementRepository;
import com.gestionLocative.gestionLocative.repository.BailRepository;
import com.gestionLocative.gestionLocative.repository.ImmeubleRepository;
import com.gestionLocative.gestionLocative.repository.ProprietaireRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.gestionLocative.gestionLocative.exception.NotFoundException;
import com.gestionLocative.gestionLocative.exception.RessourceUtiliseeException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service métier gérant les immeubles.
 * Fournit la création, la modification, la suppression et les conversions entité/DTO.
 */
@Service
public class ImmeubleService extends AbstractCrudService {

    private final ImmeubleRepository immeubleRepository;
    private final AppartementRepository appartementRepository;
    private final ProprietaireRepository proprietaireRepository;
    private final BailRepository bailRepository;
    private final AppartementService appartementService;

    /**
     * Constructeur injectant les repositories de l'immeuble, des appartements et des propriétaires.
     *
     * @param immeubleRepository le repository des immeubles
     * @param appartementRepository le repository des appartements
     * @param proprietaireRepository le repository des propriétaires
     * @param bailRepository le repository des baux
     * @param appartementService le service des appartements
     */
    public ImmeubleService(ImmeubleRepository immeubleRepository,
                           AppartementRepository appartementRepository,
                           ProprietaireRepository proprietaireRepository,
                           BailRepository bailRepository,
                           AppartementService appartementService) {
        super(immeubleRepository);
        this.immeubleRepository = immeubleRepository;
        this.appartementRepository = appartementRepository;
        this.proprietaireRepository = proprietaireRepository;
        this.bailRepository = bailRepository;
        this.appartementService = appartementService;
    }

    /**
     * Convertit une requête d'immeuble en entité.
     *
     * @param immeubleRequest La requête de l'immeuble.
     * @param proprietaire Le propriétaire associé.
     * @return L'entité Immeuble construite.
     */
    public Immeuble toEntity(ImmeubleRequestDto immeubleRequest, Proprietaire proprietaire) {
        return Immeuble.builder()
                .nom(immeubleRequest.getNom())
                .adresse(immeubleRequest.getAdresse())
                .nbEtages(immeubleRequest.getNbEtages())
                .proprietaire(proprietaire)
                .build();
    }

    /**
     * Convertit une entité Immeuble en réponse DTO, avec son propriétaire et son nombre d'appartements.
     *
     * @param entity L'entité de l'immeuble.
     * @return La réponse DTO de l'immeuble.
     */
    @Override
    public ResponseDto toDto(Object entity) {
        Immeuble immeuble = (Immeuble) entity;
        ProprietaireResponseDto proprietaireDto = null;
        if (immeuble.getProprietaire() != null) {
            Proprietaire proprietaire = immeuble.getProprietaire();
            proprietaireDto = ProprietaireResponseDto.builder()
                    .id(proprietaire.getId())
                    .nom(proprietaire.getNom())
                    .prenom(proprietaire.getPrenom())
                    .numero(proprietaire.getNumero())
                    .email(proprietaire.getEmail())
                    .build();
        }
        return ImmeubleResponseDto.builder()
                .id(immeuble.getId())
                .nom(immeuble.getNom())
                .adresse(immeuble.getAdresse())
                .nbEtages(immeuble.getNbEtages())
                .proprietaire(proprietaireDto)
                .nbAppartements(appartementRepository.countByImmeubleId(immeuble.getId()))
                .build();
    }

    /**
     * Récupère une page d'immeubles triés par adresse.
     *
     * @param page l'index de la page souhaitée
     * @param taille le nombre d'éléments par page
     * @return une page d'objets {@link ResponseDto} triée par adresse
     */
    @Override
    public Page<ResponseDto> getAll(int page, int taille) {
        Pageable pageable = PageRequest.of(pageAutorisee(page), tailleAutorisee(taille), Sort.by("adresse"));
        return immeubleRepository.findAll(pageable).map(this::toDto);
    }

    /**
     * Crée un nouvel immeuble après vérification de l'existence du propriétaire.
     *
     * @param request La requête contenant les données de l'immeuble.
     * @return L'immeuble créé, converti en DTO.
     * @throws NotFoundException si le propriétaire n'existe pas
     */
    @Override
    public ResponseDto create(RequestDto request) {
        ImmeubleRequestDto immeubleRequest = (ImmeubleRequestDto) request;
        Proprietaire proprietaire = proprietaireRepository.findById(immeubleRequest.getProprietaireId())
                .orElseThrow(() -> new NotFoundException("Propriétaire non trouvé"));
        Immeuble immeuble = toEntity(immeubleRequest, proprietaire);
        return toDto(immeubleRepository.save(immeuble));
    }

    /**
     * Met à jour un immeuble existant, en contrôlant que le nombre d'étages
     * reste supérieur ou égal à l'étage le plus élevé des appartements enregistrés.
     *
     * @param id L'identifiant de l'immeuble à mettre à jour.
     * @param request La requête contenant les nouvelles données.
     * @return L'immeuble mis à jour, converti en DTO.
     * @throws NotFoundException si l'immeuble ou le propriétaire n'existe pas
     * @throws IllegalArgumentException si le nombre d'étages est ramené sous l'étage le plus élevé
     */
    @Override
    public ResponseDto update(Long id, RequestDto request) {
        ImmeubleRequestDto immeubleRequest = (ImmeubleRequestDto) request;
        Immeuble immeuble = immeubleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Immeuble non trouvé"));
        Proprietaire proprietaire = proprietaireRepository.findById(immeubleRequest.getProprietaireId())
                .orElseThrow(() -> new NotFoundException("Propriétaire non trouvé"));
        int etageMax = appartementRepository.findByImmeubleId(id).stream()
                .mapToInt(Appartement::getEtage)
                .max()
                .orElse(0);
        if (immeubleRequest.getNbEtages() < etageMax) {
            throw new IllegalArgumentException("Le nombre d'étages (" + immeubleRequest.getNbEtages()
                    + ") ne peut pas être inférieur à l'étage le plus élevé des appartements (" + etageMax + ").");
        }
        immeuble.setNom(immeubleRequest.getNom());
        immeuble.setAdresse(immeubleRequest.getAdresse());
        immeuble.setNbEtages(immeubleRequest.getNbEtages());
        immeuble.setProprietaire(proprietaire);
        return toDto(immeubleRepository.save(immeuble));
    }

    /**
     * Supprime un immeuble après vérification qu'il ne contient aucun appartement.
     *
     * @param id L'identifiant de l'immeuble à supprimer.
     * @return ResponseDto Les données de l'immeuble supprimé.
     * @throws NotFoundException si l'immeuble n'existe pas
     * @throws RessourceUtiliseeException si l'immeuble contient encore des appartements
     */
    @Override
    public ResponseDto delete(Long id) {
        Immeuble immeuble = immeubleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Immeuble non trouvé"));
        if (appartementRepository.existsByImmeubleId(id)) {
            throw new RessourceUtiliseeException("Immeuble", id, "Appartement");
        }
        immeubleRepository.deleteById(id);
        return toDto(immeuble);
    }

    /**
     * Récupère les immeubles d'un propriétaire, avec pour chacun le nombre d'appartements
     * et le nombre de baux en cours.
     *
     * @param proprietaireId L'identifiant du propriétaire.
     * @return La liste des immeubles du propriétaire.
     * @throws NotFoundException si le propriétaire n'existe pas.
     */
    public List<ResponseDto> getImmeublesByProprietaire(Long proprietaireId) {
        Proprietaire proprietaire = proprietaireRepository.findById(proprietaireId)
                .orElseThrow(() -> new NotFoundException("Propriétaire non trouvé"));
        List<Immeuble> immeubles = immeubleRepository.findByProprietaireId(proprietaireId);
        LocalDateTime maintenant = LocalDateTime.now();
        return immeubles.stream()
                .map(immeuble -> {
                    ImmeubleAvecStatsResponseDto dto = new ImmeubleAvecStatsResponseDto();
                    dto.setId(immeuble.getId());
                    dto.setNom(immeuble.getNom());
                    dto.setAdresse(immeuble.getAdresse());
                    dto.setNbEtages(immeuble.getNbEtages());
                    dto.setProprietaire(ProprietaireResponseDto.builder()
                            .id(proprietaire.getId())
                            .nom(proprietaire.getNom())
                            .prenom(proprietaire.getPrenom())
                            .numero(proprietaire.getNumero())
                            .email(proprietaire.getEmail())
                            .build());
                    dto.setNombreAppartements((int) appartementRepository.countByImmeubleId(immeuble.getId()));
                    dto.setNombreBauxEnCours((int) bailRepository.countBauxEnCoursParImmeuble(immeuble.getId(), maintenant));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    /**
     * Récupère les appartements d'un immeuble, triés par étage croissant,
     * avec l'état d'occupation de chacun.
     *
     * @param immeubleId L'identifiant de l'immeuble.
     * @return La liste des appartements de l'immeuble.
     */
    public List<ResponseDto> getAppartementsByImmeuble(Long immeubleId) {
        return appartementService.getAppartementsByImmeuble(immeubleId);
    }
}