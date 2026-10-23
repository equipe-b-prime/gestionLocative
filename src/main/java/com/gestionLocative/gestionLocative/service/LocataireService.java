package com.gestionLocative.gestionLocative.service;

import com.gestionLocative.gestionLocative.exception.DoublonRessourceException;
import com.gestionLocative.gestionLocative.exception.NotFoundException;
import com.gestionLocative.gestionLocative.exception.RessourceUtiliseeException;

import com.gestionLocative.gestionLocative.repository.BailRepository;
import com.gestionLocative.gestionLocative.dto.AppartementResponseDto;
import com.gestionLocative.gestionLocative.dto.BailResponseDto;
import com.gestionLocative.gestionLocative.entity.Bail;
import com.gestionLocative.gestionLocative.entity.Locataire;
import com.gestionLocative.gestionLocative.dto.ImmeubleResponseDto;
import com.gestionLocative.gestionLocative.dto.LocataireRequestDto;
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
import com.gestionLocative.gestionLocative.repository.LocataireRepository;

/**
 * Service métier gérant les locataires.
 * Fournit la création, la modification, la suppression et les conversions entité/DTO.
 */
@Service
public class LocataireService extends AbstractCrudService {

    private final LocataireRepository locataireRepository;
    private final BailRepository bailRepository;

    /**
     * Constructeur injectant les repositories des locataires et des baux.
     *
     * @param locataireRepository le repository des locataires
     * @param bailRepository le repository des baux
     */
    public LocataireService(LocataireRepository locataireRepository,
                            BailRepository bailRepository) {
        super(locataireRepository);
        this.locataireRepository = locataireRepository;
        this.bailRepository = bailRepository;
    }

    /**
     * Convertit une requête de locataire en entité.
     *
     * @param locataireRequest La requête du locataire.
     * @return L'entité Locataire construite.
     */
    public Locataire toEntity(LocataireRequestDto locataireRequest) {
        return Locataire.builder()
                .nom(locataireRequest.getNom())
                .prenom(locataireRequest.getPrenom())
                .numero(locataireRequest.getNumero())
                .email(locataireRequest.getEmail())
                .build();
    }

    /**
     * Convertit une entité Locataire en réponse DTO.
     *
     * @param entity L'entité du locataire.
     * @return La réponse DTO du locataire.
     */
    @Override
    public ResponseDto toDto(Object entity) {
        Locataire locataire = (Locataire) entity;
        return LocataireResponseDto.builder()
                .id(locataire.getId())
                .nom(locataire.getNom())
                .prenom(locataire.getPrenom())
                .numero(locataire.getNumero())
                .email(locataire.getEmail())
                .build();
    }

    /**
     * Récupère une page de locataires triés par nom.
     *
     * @param page l'index de la page souhaitée
     * @param taille le nombre d'éléments par page
     * @return une page d'objets {@link ResponseDto} triée par nom
     */
    @Override
    public Page<ResponseDto> getAll(int page, int taille) {
        Pageable pageable = PageRequest.of(pageAutorisee(page), tailleAutorisee(taille), Sort.by("nom"));
        return locataireRepository.findAll(pageable).map(this::toDto);
    }

    /**
     * Crée un nouveau locataire après contrôle de l'unicité de l'email.
     *
     * @param request La requête contenant les données du locataire.
     * @return Le locataire créé, converti en DTO.
     * @throws DoublonRessourceException si l'email est déjà utilisé
     */
    @Override
    public ResponseDto create(RequestDto request) {
        LocataireRequestDto locataireRequest = (LocataireRequestDto) request;
        if (locataireRepository.existsByEmail(locataireRequest.getEmail())) {
            throw new DoublonRessourceException("Un locataire avec l'email '"
                    + locataireRequest.getEmail() + "' existe déjà.");
        }
        Locataire locataire = toEntity(locataireRequest);
        return toDto(locataireRepository.save(locataire));
    }

    /**
     * Met à jour un locataire après contrôle de l'unicité de l'email.
     *
     * @param id L'identifiant du locataire à mettre à jour.
     * @param request La requête contenant les nouvelles données.
     * @return Le locataire mis à jour, converti en DTO.
     * @throws NotFoundException si le locataire n'existe pas
     * @throws DoublonRessourceException si l'email est déjà utilisé par un autre locataire
     */
    @Override
    public ResponseDto update(Long id, RequestDto request) {
        LocataireRequestDto locataireRequest = (LocataireRequestDto) request;
        Locataire locataire = locataireRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Locataire non trouvé"));
        if (locataireRepository.existsByEmailAndIdNot(locataireRequest.getEmail(), id)) {
            throw new DoublonRessourceException("Un autre locataire avec l'email '"
                    + locataireRequest.getEmail() + "' existe déjà.");
        }
        locataire.setNom(locataireRequest.getNom());
        locataire.setPrenom(locataireRequest.getPrenom());
        locataire.setNumero(locataireRequest.getNumero());
        locataire.setEmail(locataireRequest.getEmail());
        return toDto(locataireRepository.save(locataire));
    }

    /**
     * Supprime un locataire après vérification qu'il n'a aucun bail en cours.
     *
     * @param id L'identifiant du locataire à supprimer.
     * @return ResponseDto Les données du locataire supprimé.
     * @throws NotFoundException si le locataire n'existe pas
     * @throws RessourceUtiliseeException si le locataire a encore un bail en cours
     */
    @Override
    public ResponseDto delete(Long id) {
        Locataire locataire = locataireRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_MESSAGE));
        for (Bail bail : bailRepository.findByLocataireId(id)) {
            if (bailEnCours(bail)) {
                throw new RessourceUtiliseeException("Locataire", id, "Bail");
            }
        }
        locataireRepository.deleteById(id);
        return toDto(locataire);
    }

    /**
     * Récupère les baux d'un locataire (requête dérivée).
     *
     * @param id L'identifiant du locataire.
     * @return List<BailResponseDto> Les baux du locataire.
     */
    public List<BailResponseDto> getBauxByLocataire(Long id) {
        return bailRepository.findByLocataireId(id).stream()
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

                                .immeuble(ImmeubleResponseDto.builder()
                                        .id(bail.getAppartement().getImmeuble().getId())
                                        .nom(bail.getAppartement().getImmeuble().getNom())
                                        .adresse(bail.getAppartement().getImmeuble().getAdresse())
                                        .nbEtages(bail.getAppartement().getImmeuble().getNbEtages())

                                        .proprietaire(ProprietaireResponseDto.builder()
                                                .id(bail.getAppartement().getImmeuble().getProprietaire().getId())
                                                .nom(bail.getAppartement().getImmeuble().getProprietaire().getNom())
                                                .prenom(bail.getAppartement().getImmeuble().getProprietaire().getPrenom())
                                                .numero(bail.getAppartement().getImmeuble().getProprietaire().getNumero())
                                                .email(bail.getAppartement().getImmeuble().getProprietaire().getEmail())
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

                        .build())
                .toList();
    }

    /**
     * Indique si un bail est en cours : commencé (date de début passée ou aujourd'hui)
     * et non terminé (date de fin absente ou postérieure ou égale à aujourd'hui).
     *
     * @param bail le bail à contrôler
     * @return true si le bail est en cours
     */
    private boolean bailEnCours(Bail bail) {
        Date aujourdHui = new Date();
        Date debut = bail.getDateDebut();
        Date fin = bail.getDateFin();
        boolean commence = debut != null && !debut.after(aujourdHui);
        boolean nonTermine = fin == null || !fin.before(aujourdHui);
        return commence && nonTermine;
    }
}
