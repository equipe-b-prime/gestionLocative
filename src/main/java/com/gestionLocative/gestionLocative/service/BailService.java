package com.gestionLocative.gestionLocative.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

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
import org.springframework.stereotype.Service;

import com.gestionLocative.gestionLocative.repository.BailRepository;

/**
 * Service métier gérant les baux.
 * Fournit la création, la modification, la suppression et les conversions entité/DTO,
 * avec résolution des relations appartement et locataire par identifiant.
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
     * Convertit une entité Bail en réponse DTO.
     *
     * @param bail L'entité du bail.
     * @return La réponse DTO du bail, avec appartement et locataire imbriqués.
     */
    @Override
    public ResponseDto toDto(Object entity) {
        Bail bail = (Bail) entity;
        return BailResponseDto.builder()
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

                .build();
    }




    /**
     * Met à jour un bail existant à partir du contenu de la requête.
     *
     * @param id L'identifiant du bail à mettre à jour.
     * @param bailRequest La requête contenant les nouvelles données et les identifiants des relations.
     * @return Le bail mis à jour, converti en DTO.
     * @throws RuntimeException Si le bail, l'appartement ou le locataire n'existe pas.
     */
    @Override
    public ResponseDto update(Long id, RequestDto request) {
        BailRequestDto bailRequest = (BailRequestDto) request;
        Appartement appartement = appartementRepository.findById(bailRequest.getAppartementId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appartement non trouvé"));
        Locataire locataire = locataireRepository.findById(bailRequest.getLocataireId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Locataire non trouvé"));
        Bail bail = bailRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bail non trouvé"));
        bail.setDateDebut(bailRequest.getDateDebut());
        bail.setDateFin(bailRequest.getDateFin());
        bail.setMontantLoyer(bailRequest.getMontantLoyer());
        bail.setProvisionCharges(bailRequest.getProvisionCharges());
        bail.setDepotGarantie(bailRequest.getDepotGarantie());
        bail.setAppartement(appartement);
        bail.setLocataire(locataire);
        return toDto(bailRepository.save(bail));
    }

    /**
     * Crée un nouveau bail.
     *
     * @param bailRequest La requête contenant les données et les identifiants des relations.
     * @return Le bail créé, converti en DTO.
     * @throws RuntimeException Si l'appartement ou le locataire n'existe pas.
     */
    @Override
    public ResponseDto create(RequestDto request) {
        BailRequestDto bailRequest = (BailRequestDto) request;
        Appartement appartement = appartementRepository.findById(bailRequest.getAppartementId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appartement non trouvé"));
        Locataire locataire = locataireRepository.findById(bailRequest.getLocataireId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Locataire non trouvé"));
        Bail bail = toEntity(bailRequest, appartement, locataire);
        return toDto(bailRepository.save(bail));
    }
}