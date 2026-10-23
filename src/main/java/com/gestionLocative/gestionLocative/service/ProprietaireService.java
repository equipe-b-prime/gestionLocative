package com.gestionLocative.gestionLocative.service;

import com.gestionLocative.gestionLocative.exception.DoublonRessourceException;
import com.gestionLocative.gestionLocative.exception.NotFoundException;
import com.gestionLocative.gestionLocative.exception.RessourceUtiliseeException;

import com.gestionLocative.gestionLocative.entity.Proprietaire;
import com.gestionLocative.gestionLocative.dto.ProprietaireRequestDto;
import com.gestionLocative.gestionLocative.dto.ProprietaireResponseDto;
import com.gestionLocative.gestionLocative.dto.RequestDto;
import com.gestionLocative.gestionLocative.dto.ResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import java.util.List;
import java.util.stream.Collectors;
import com.gestionLocative.gestionLocative.entity.Immeuble;
import java.time.LocalDateTime;
import com.gestionLocative.gestionLocative.dto.ImmeubleAvecStatsResponseDto;
import org.springframework.stereotype.Service;
import com.gestionLocative.gestionLocative.service.ImmeubleService;

import com.gestionLocative.gestionLocative.repository.ImmeubleRepository;
import com.gestionLocative.gestionLocative.repository.AppartementRepository;
import com.gestionLocative.gestionLocative.repository.BailRepository;
import com.gestionLocative.gestionLocative.repository.ProprietaireRepository;

/**
 * Service métier gérant les propriétaires.
 * Fournit la création, la modification, la suppression et les conversions entité/DTO.
 */
@Service
public class ProprietaireService extends AbstractCrudService {

    private final ProprietaireRepository proprietaireRepository;
    private final ImmeubleRepository immeubleRepository;
    private final AppartementRepository appartementRepository;
    private final BailRepository bailRepository;
    private final ImmeubleService immeubleService;

    /**
     * Constructeur injectant le repository des propriétaires et celui des immeubles.
     *
     * @param proprietaireRepository le repository des propriétaires
     * @param immeubleRepository le repository des immeubles
     * @param appartementRepository le repository des appartements
     * @param bailRepository le repository des baux
     */
    public ProprietaireService(ProprietaireRepository proprietaireRepository,
                               ImmeubleRepository immeubleRepository,
                               AppartementRepository appartementRepository,
                               BailRepository bailRepository,
                               ImmeubleService immeubleService) {
        super(proprietaireRepository);
        this.proprietaireRepository = proprietaireRepository;
        this.immeubleRepository = immeubleRepository;
        this.appartementRepository = appartementRepository;
        this.bailRepository = bailRepository;
        this.immeubleService = immeubleService;
    }

    /**
     * Convertit une requête de propriétaire en entité.
     *
     * @param proprietaireRequest Les données de la requête pour créer l'entité.
     * @return Proprietaire L'entité Proprietaire construite.
     */
    public Proprietaire toEntity(ProprietaireRequestDto proprietaireRequest) {
        return Proprietaire.builder()
                .nom(proprietaireRequest.getNom())
                .prenom(proprietaireRequest.getPrenom())
                .numero(proprietaireRequest.getNumero())
                .email(proprietaireRequest.getEmail())
                .build();
    }

    /**
     * Convertit une entité Proprietaire en réponse DTO.
     *
     * @param entity L'entité du propriétaire.
     * @return ProprietaireResponseDto Les détails du propriétaire converti.
     */
    @Override
    public ResponseDto toDto(Object entity) {
        Proprietaire proprietaire = (Proprietaire) entity;
        return ProprietaireResponseDto.builder()
                .id(proprietaire.getId())
                .nom(proprietaire.getNom())
                .prenom(proprietaire.getPrenom())
                .numero(proprietaire.getNumero())
                .email(proprietaire.getEmail())
                .build();
    }

    /**
     * Récupère une page de propriétaires triés par nom.
     *
     * @param page l'index de la page souhaitée
     * @param taille le nombre d'éléments par page
     * @return une page d'objets {@link ResponseDto} triée par nom
     */
    @Override
    public Page<ResponseDto> getAll(int page, int taille) {
        Pageable pageable = PageRequest.of(pageAutorisee(page), tailleAutorisee(taille), Sort.by("nom"));
        return proprietaireRepository.findAll(pageable).map(this::toDto);
    }

    /**
     * Crée un nouveau propriétaire après contrôle de l'unicité de l'email.
     *
     * @param request Les données du propriétaire à créer.
     * @return ProprietaireResponseDto Les détails du propriétaire créé.
     * @throws DoublonRessourceException si l'email est déjà utilisé
     */
    @Override
    public ResponseDto create(RequestDto request) {
        ProprietaireRequestDto proprietaireRequest = (ProprietaireRequestDto) request;
        if (emailFourni(proprietaireRequest.getEmail())
                && proprietaireRepository.existsByEmail(proprietaireRequest.getEmail())) {
            throw new DoublonRessourceException("Un propriétaire avec l'email '"
                    + proprietaireRequest.getEmail() + "' existe déjà.");
        }
        Proprietaire proprietaire = toEntity(proprietaireRequest);
        return toDto(proprietaireRepository.save(proprietaire));
    }

    /**
     * Met à jour un propriétaire après contrôle de l'unicité de l'email.
     *
     * @param id L'identifiant du propriétaire à mettre à jour.
     * @param request Les nouvelles données du propriétaire.
     * @return ProprietaireResponseDto Les détails du propriétaire mis à jour.
     * @throws NotFoundException si le propriétaire n'existe pas
     * @throws DoublonRessourceException si l'email est déjà utilisé par un autre propriétaire
     */
    @Override
    public ResponseDto update(Long id, RequestDto request) {
        ProprietaireRequestDto proprietaireRequest = (ProprietaireRequestDto) request;
        Proprietaire proprietaire = proprietaireRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Propriétaire non trouvé"));
        if (emailFourni(proprietaireRequest.getEmail())
                && proprietaireRepository.existsByEmailAndIdNot(proprietaireRequest.getEmail(), id)) {
            throw new DoublonRessourceException("Un autre propriétaire avec l'email '"
                    + proprietaireRequest.getEmail() + "' existe déjà.");
        }
        proprietaire.setNom(proprietaireRequest.getNom());
        proprietaire.setPrenom(proprietaireRequest.getPrenom());
        proprietaire.setNumero(proprietaireRequest.getNumero());
        proprietaire.setEmail(proprietaireRequest.getEmail());
        return toDto(proprietaireRepository.save(proprietaire));
    }

    /**
     * Supprime un propriétaire après vérification qu'il ne possède aucun immeuble.
     *
     * @param id L'identifiant du propriétaire à supprimer.
     * @return ResponseDto Les données du propriétaire supprimé.
     * @throws NotFoundException si le propriétaire n'existe pas
     * @throws RessourceUtiliseeException si le propriétaire possède encore des immeubles
     */
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

    /**
     * Récupère les immeubles d'un propriétaire avec leurs statistiques.
     *
     * @param proprietaireId L'identifiant du propriétaire.
     * @return La liste des immeubles du propriétaire avec le nombre d'appartements et de baux en cours.
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
     * Indique si un email est fourni (non nul et non vide).
     *
     * @param email l'email à contrôler
     * @return true si l'email est présent
     */
    private boolean emailFourni(String email) {
        return email != null && !email.isBlank();
    }
}
