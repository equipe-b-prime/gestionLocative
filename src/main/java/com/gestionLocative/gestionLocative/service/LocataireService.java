package com.gestionLocative.gestionLocative.service;

import com.gestionLocative.gestionLocative.repository.BailRepository;
import com.gestionLocative.gestionLocative.dto.AppartementResponseDto;
import com.gestionLocative.gestionLocative.dto.BailResponseDto;
import com.gestionLocative.gestionLocative.entity.Locataire;
import com.gestionLocative.gestionLocative.dto.ImmeubleResponseDto;
import com.gestionLocative.gestionLocative.dto.LocataireRequestDto;
import com.gestionLocative.gestionLocative.dto.LocataireResponseDto;
import com.gestionLocative.gestionLocative.dto.ProprietaireResponseDto;
import com.gestionLocative.gestionLocative.dto.RequestDto;
import com.gestionLocative.gestionLocative.dto.ResponseDto;
import org.springframework.stereotype.Service;

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
     * @param locataire L'entité du locataire.
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
     * Met à jour un locataire existant à partir du contenu de la requête.
     *
     * @param id L'identifiant du locataire à mettre à jour.
     * @param locataireRequest La requête contenant les nouvelles données.
     * @return Le locataire mis à jour, converti en DTO.
     * @throws RuntimeException Si le locataire n'existe pas.
     */
    @Override
    public ResponseDto update(Long id, RequestDto request) {
        LocataireRequestDto locataireRequest = (LocataireRequestDto) request;
        Locataire locataire = locataireRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Locataire non trouvé"));
        locataire.setNom(locataireRequest.getNom());
        locataire.setPrenom(locataireRequest.getPrenom());
        locataire.setNumero(locataireRequest.getNumero());
        locataire.setEmail(locataireRequest.getEmail());
        return toDto(locataireRepository.save(locataire));
    }

    /**
     * Crée un nouveau locataire.
     *
     * @param locataireRequest La requête contenant les données du locataire.
     * @return Le locataire créé, converti en DTO.
     */
    @Override
    public ResponseDto create(RequestDto request) {
        LocataireRequestDto locataireRequest = (LocataireRequestDto) request;
        Locataire locataire = toEntity(locataireRequest);
        return toDto(locataireRepository.save(locataire));
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

                                .proprietaire(ProprietaireResponseDto.builder()
                                        .id(bail.getAppartement().getProprietaire().getId())
                                        .nom(bail.getAppartement().getProprietaire().getNom())
                                        .prenom(bail.getAppartement().getProprietaire().getPrenom())
                                        .numero(bail.getAppartement().getProprietaire().getNumero())
                                        .adresse(bail.getAppartement().getProprietaire().getAdresse())
                                        .build())

                                .immeuble(ImmeubleResponseDto.builder()
                                        .id(bail.getAppartement().getImmeuble().getId())
                                        .nom(bail.getAppartement().getImmeuble().getNom())
                                        .adresse(bail.getAppartement().getImmeuble().getAdresse())
                                        .nbEtages(bail.getAppartement().getImmeuble().getNbEtages())
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
}