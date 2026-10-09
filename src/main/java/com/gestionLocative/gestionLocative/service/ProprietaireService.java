package com.gestionLocative.gestionLocative.service;

import com.gestionLocative.gestionLocative.entity.Proprietaire;
import com.gestionLocative.gestionLocative.dto.ProprietaireRequestDto;
import com.gestionLocative.gestionLocative.dto.ProprietaireResponseDto;
import com.gestionLocative.gestionLocative.dto.RequestDto;
import com.gestionLocative.gestionLocative.dto.ResponseDto;
import org.springframework.stereotype.Service;

import com.gestionLocative.gestionLocative.repository.ProprietaireRepository;

/**
 * Service métier gérant les propriétaires.
 * Fournit la création, la modification, la suppression et les conversions entité/DTO.
 */
@Service
public class ProprietaireService extends AbstractCrudService {

    private final ProprietaireRepository proprietaireRepository;

    /**
     * Constructeur injectant le repository des propriétaires.
     *
     * @param proprietaireRepository le repository des propriétaires
     */
    public ProprietaireService(ProprietaireRepository proprietaireRepository) {
        super(proprietaireRepository);
        this.proprietaireRepository = proprietaireRepository;
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
                .adresse(proprietaireRequest.getAdresse())
                .build();
    }

    /**
     * Convertit une entité Proprietaire en réponse DTO.
     *
     * @param proprietaire L'entité du propriétaire.
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
                .adresse(proprietaire.getAdresse())
                .build();
    }




    /**
     * Met à jour un propriétaire à partir de son identifiant et des nouvelles données.
     *
     * @param id L'identifiant du propriétaire à mettre à jour.
     * @param proprietaireRequest Les nouvelles données du propriétaire.
     * @return ProprietaireResponseDto Les détails du propriétaire mis à jour.
     */
    @Override
    public ResponseDto update(Long id, RequestDto request) {
        ProprietaireRequestDto proprietaireRequest = (ProprietaireRequestDto) request;
        Proprietaire proprietaire = proprietaireRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Propriétaire non trouvé"));
        proprietaire.setNom(proprietaireRequest.getNom());
        proprietaire.setPrenom(proprietaireRequest.getPrenom());
        proprietaire.setNumero(proprietaireRequest.getNumero());
        proprietaire.setAdresse(proprietaireRequest.getAdresse());
        return toDto(proprietaireRepository.save(proprietaire));
    }

    /**
     * Crée un nouveau propriétaire à partir des données fournies.
     *
     * @param proprietaireRequest Les données du propriétaire à créer.
     * @return ProprietaireResponseDto Les détails du propriétaire créé.
     */
    @Override
    public ResponseDto create(RequestDto request) {
        ProprietaireRequestDto proprietaireRequest = (ProprietaireRequestDto) request;
        Proprietaire proprietaire = toEntity(proprietaireRequest);
        return toDto(proprietaireRepository.save(proprietaire));
    }
}