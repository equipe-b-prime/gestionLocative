package com.gestionLocative.gestionLocative.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.gestionLocative.gestionLocative.repository.AppartementRepository;
import com.gestionLocative.gestionLocative.dto.AppartementResponseDto;
import com.gestionLocative.gestionLocative.entity.Immeuble;
import com.gestionLocative.gestionLocative.entity.Proprietaire;
import com.gestionLocative.gestionLocative.dto.ImmeubleRequestDto;
import com.gestionLocative.gestionLocative.dto.ImmeubleResponseDto;
import com.gestionLocative.gestionLocative.dto.ProprietaireResponseDto;
import com.gestionLocative.gestionLocative.dto.RequestDto;
import com.gestionLocative.gestionLocative.dto.ResponseDto;
import org.springframework.stereotype.Service;

import java.util.List;
import com.gestionLocative.gestionLocative.repository.ImmeubleRepository;

/**
 * Service métier gérant les immeubles.
 * Fournit la création, la modification, la suppression et les conversions entité/DTO.
 */
@Service
public class ImmeubleService extends AbstractCrudService {

    private final ImmeubleRepository immeubleRepository;
    private final AppartementRepository appartementRepository;

    /**
     * Constructeur injectant les repositories de l'immeuble et des appartements.
     *
     * @param immeubleRepository le repository des immeubles
     * @param appartementRepository le repository des appartements
     */
    public ImmeubleService(ImmeubleRepository immeubleRepository,
                           AppartementRepository appartementRepository) {
        super(immeubleRepository);
        this.immeubleRepository = immeubleRepository;
        this.appartementRepository = appartementRepository;
    }

    /**
     * Convertit une requête d'immeuble en entité.
     *
     * @param immeubleRequest La requête de l'immeuble.
     * @return L'entité Immeuble construite.
     */
    public Immeuble toEntity(ImmeubleRequestDto immeubleRequest) {
        return Immeuble.builder()
                .nom(immeubleRequest.getNom())
                .adresse(immeubleRequest.getAdresse())
                .nbEtages(immeubleRequest.getNbEtages())
                .build();
    }

    /**
     * Convertit une entité Immeuble en réponse DTO.
     *
     * @param immeuble L'entité de l'immeuble.
     * @return La réponse DTO de l'immeuble.
     */
    @Override
    public ResponseDto toDto(Object entity) {
        Immeuble immeuble = (Immeuble) entity;
        return ImmeubleResponseDto.builder()
                .id(immeuble.getId())
                .nom(immeuble.getNom())
                .adresse(immeuble.getAdresse())
                .nbEtages(immeuble.getNbEtages())
                .build();
    }




    /**
     * Met à jour un immeuble existant à partir du contenu de la requête.
     *
     * @param id L'identifiant de l'immeuble à mettre à jour.
     * @param immeubleRequest La requête contenant les nouvelles données.
     * @return L'immeuble mis à jour, converti en DTO.
     * @throws RuntimeException Si l'immeuble n'existe pas.
     */
    @Override
    public ResponseDto update(Long id, RequestDto request) {
        ImmeubleRequestDto immeubleRequest = (ImmeubleRequestDto) request;
        Immeuble immeuble = immeubleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Immeuble non trouvé"));
        immeuble.setNom(immeubleRequest.getNom());
        immeuble.setAdresse(immeubleRequest.getAdresse());
        immeuble.setNbEtages(immeubleRequest.getNbEtages());
        return toDto(immeubleRepository.save(immeuble));
    }

    /**
     * Crée un nouvel immeuble.
     *
     * @param immeubleRequest La requête contenant les données de l'immeuble.
     * @return L'immeuble créé, converti en DTO.
     */
    @Override
    public ResponseDto create(RequestDto request) {
        ImmeubleRequestDto immeubleRequest = (ImmeubleRequestDto) request;
        Immeuble immeuble = toEntity(immeubleRequest);
        return toDto(immeubleRepository.save(immeuble));
    }

    /**
     * Récupère les appartements d'un immeuble (requête dérivée).
     *
     * @param id L'identifiant de l'immeuble.
     * @return List<AppartementResponseDto> Les appartements de l'immeuble.
     */
    public List<AppartementResponseDto> getAppartementsByImmeuble(Long id) {
        return appartementRepository.findByImmeubleId(id).stream()
                .map(appartement -> AppartementResponseDto.builder()
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
                        ).build())
                .toList();
    }

    /**
     * Récupère les propriétaires distincts des appartements d'un immeuble
     * (via jointure, sans relation directe Immeuble → Proprietaire).
     *
     * @param id L'identifiant de l'immeuble.
     * @return List<ProprietaireResponseDto> Les propriétaires distincts.
     */
    public List<ProprietaireResponseDto> getProprietairesByImmeuble(Long id) {
        return appartementRepository.findProprietairesByImmeubleId(id).stream()
                .map(proprietaire -> ProprietaireResponseDto.builder()
                        .id(proprietaire.getId())
                        .nom(proprietaire.getNom())
                        .prenom(proprietaire.getPrenom())
                        .numero(proprietaire.getNumero())
                        .adresse(proprietaire.getAdresse())
                        .build())
                .toList();
    }
}