package com.gestionLocative.gestionLocative.service;

import com.gestionLocative.gestionLocative.entity.Proprietaire;
import com.gestionLocative.gestionLocative.dto.ProprietaireRequestDto;
import com.gestionLocative.gestionLocative.dto.ProprietaireResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import com.gestionLocative.gestionLocative.repository.ProprietaireRepository;

/**
 * Service métier gérant les propriétaires.
 * Fournit les opérations CRUD ainsi que les conversions entité/DTO.
 */
@Service
@RequiredArgsConstructor
public class ProprietaireService {

    private final ProprietaireRepository proprietaireRepository;

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
    public ProprietaireResponseDto toDto(Proprietaire proprietaire) {
        return ProprietaireResponseDto.builder()
                .id(proprietaire.getId())
                .nom(proprietaire.getNom())
                .prenom(proprietaire.getPrenom())
                .numero(proprietaire.getNumero())
                .adresse(proprietaire.getAdresse())
                .build();
    }

    /**
     * Récupère la liste de tous les propriétaires.
     *
     * @return List<ProprietaireResponseDto> La liste des propriétaires récupérés.
     */
    public List<ProprietaireResponseDto> getAllProprietaires() {
        return proprietaireRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    /**
     * Récupère un propriétaire à partir de son identifiant.
     *
     * @param id L'identifiant du propriétaire à récupérer.
     * @return ProprietaireResponseDto Les détails du propriétaire récupéré.
     */
    public ProprietaireResponseDto getProprietaireById(Long id) {
        Proprietaire proprietaire = proprietaireRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Propriétaire non trouvé"));
        return toDto(proprietaire);
    }

    /**
     * Supprime un propriétaire à partir de son identifiant.
     *
     * @param id L'identifiant du propriétaire à supprimer.
     * @return ProprietaireResponseDto Les détails du propriétaire supprimé.
     */
    public ProprietaireResponseDto deleteById(Long id) {
        Proprietaire proprietaire = proprietaireRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Propriétaire non trouvé"));
        proprietaireRepository.deleteById(id);
        return toDto(proprietaire);
    }

    /**
     * Met à jour un propriétaire à partir de son identifiant et des nouvelles données.
     *
     * @param id L'identifiant du propriétaire à mettre à jour.
     * @param proprietaireRequest Les nouvelles données du propriétaire.
     * @return ProprietaireResponseDto Les détails du propriétaire mis à jour.
     */
    public ProprietaireResponseDto updateById(Long id, ProprietaireRequestDto proprietaireRequest) {
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
    public ProprietaireResponseDto createById(ProprietaireRequestDto proprietaireRequest) {
        Proprietaire proprietaire = toEntity(proprietaireRequest);
        return toDto(proprietaireRepository.save(proprietaire));
    }
}