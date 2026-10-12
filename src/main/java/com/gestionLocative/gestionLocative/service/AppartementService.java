package com.gestionLocative.gestionLocative.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.gestionLocative.gestionLocative.dto.AppartementRequestDto;
import com.gestionLocative.gestionLocative.dto.AppartementResponseDto;
import com.gestionLocative.gestionLocative.entity.Appartement;
import com.gestionLocative.gestionLocative.entity.Immeuble;
import com.gestionLocative.gestionLocative.entity.Proprietaire;
import com.gestionLocative.gestionLocative.repository.ImmeubleRepository;
import com.gestionLocative.gestionLocative.dto.ImmeubleResponseDto;
import com.gestionLocative.gestionLocative.repository.ProprietaireRepository;
import com.gestionLocative.gestionLocative.dto.ProprietaireResponseDto;
import com.gestionLocative.gestionLocative.dto.RequestDto;
import com.gestionLocative.gestionLocative.dto.ResponseDto;
import org.springframework.stereotype.Service;

import com.gestionLocative.gestionLocative.repository.AppartementRepository;

/**
 * Service métier gérant les appartements.
 * Fournit la création, la modification, la suppression et les conversions entité/DTO,
 * avec résolution des relations propriétaire et immeuble par identifiant.
 */
@Service
public class AppartementService extends AbstractCrudService {

    private final AppartementRepository appartementRepository;
    private final ProprietaireRepository proprietaireRepository;
    private final ImmeubleRepository immeubleRepository;

    /**
     * Constructeur injectant les repositories de l'appartement, du propriétaire et de l'immeuble.
     *
     * @param appartementRepository le repository des appartements
     * @param proprietaireRepository le repository des propriétaires
     * @param immeubleRepository le repository des immeubles
     */
    public AppartementService(AppartementRepository appartementRepository,
                              ProprietaireRepository proprietaireRepository,
                              ImmeubleRepository immeubleRepository) {
        super(appartementRepository);
        this.appartementRepository = appartementRepository;
        this.proprietaireRepository = proprietaireRepository;
        this.immeubleRepository = immeubleRepository;
    }

    /**
     * Convertit une requête d'appartement en entité.
     *
     * @param appartementRequest La requête de l'appartement.
     * @param proprietaire Le propriétaire associé.
     * @param immeuble L'immeuble associé.
     * @return L'entité Appartement construite.
     */
    public Appartement toEntity(AppartementRequestDto appartementRequest, Proprietaire proprietaire, Immeuble immeuble) {
        return Appartement.builder()
                .nom(appartementRequest.getNom())
                .surface(appartementRequest.getSurface())
                .nbPieces(appartementRequest.getNbPieces())
                .etage(appartementRequest.getEtage())
                .proprietaire(proprietaire)
                .immeuble(immeuble)
                .build();
    }

    /**
     * Convertit une entité Appartement en réponse DTO.
     *
     * @param appartement L'entité de l'appartement.
     * @return La réponse DTO de l'appartement, avec propriétaire et immeuble imbriqués.
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
                .proprietaire(ProprietaireResponseDto.builder()
                        .id(appartement.getProprietaire().getId())
                        .nom(appartement.getProprietaire().getNom())
                        .prenom(appartement.getProprietaire().getPrenom())
                        .numero(appartement.getProprietaire().getNumero())
                        .adresse(appartement.getProprietaire().getAdresse())
                        .build()
                ).immeuble(ImmeubleResponseDto.builder()
                        .id(appartement.getImmeuble().getId())
                        .nom(appartement.getImmeuble().getNom())
                        .adresse(appartement.getImmeuble().getAdresse())
                        .nbEtages(appartement.getImmeuble().getNbEtages())
                        .build()
                ).build();
    }




    /**
     * Met à jour un appartement existant à partir du contenu de la requête.
     *
     * @param id L'identifiant de l'appartement à mettre à jour.
     * @param appartementRequest La requête contenant les nouvelles données et les identifiants des relations.
     * @return L'appartement mis à jour, converti en DTO.
     * @throws RuntimeException Si l'appartement, le propriétaire ou l'immeuble n'existe pas.
     */
    @Override
    public ResponseDto update(Long id, RequestDto request) {
        AppartementRequestDto appartementRequest = (AppartementRequestDto) request;
        Proprietaire proprietaire = proprietaireRepository.findById(appartementRequest.getProprietaireId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Propriétaire non trouvé"));
        Immeuble immeuble = immeubleRepository.findById(appartementRequest.getImmeubleId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Immeuble non trouvé"));
        Appartement appartement = appartementRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appartement non trouvé"));
        appartement.setNom(appartementRequest.getNom());
        appartement.setSurface(appartementRequest.getSurface());
        appartement.setNbPieces(appartementRequest.getNbPieces());
        appartement.setEtage(appartementRequest.getEtage());
        appartement.setProprietaire(proprietaire);
        appartement.setImmeuble(immeuble);
        return toDto(appartementRepository.save(appartement));
    }

    /**
     * Crée un nouvel appartement.
     *
     * @param appartementRequest La requête contenant les données et les identifiants des relations.
     * @return L'appartement créé, converti en DTO.
     * @throws RuntimeException Si le propriétaire ou l'immeuble n'existe pas.
     */
    @Override
    public ResponseDto create(RequestDto request) {
        AppartementRequestDto appartementRequest = (AppartementRequestDto) request;
        Proprietaire proprietaire = proprietaireRepository.findById(appartementRequest.getProprietaireId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Propriétaire non trouvé"));
        Immeuble immeuble = immeubleRepository.findById(appartementRequest.getImmeubleId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Immeuble non trouvé"));
        Appartement appartement = toEntity(appartementRequest, proprietaire, immeuble);
        return toDto(appartementRepository.save(appartement));
    }
}