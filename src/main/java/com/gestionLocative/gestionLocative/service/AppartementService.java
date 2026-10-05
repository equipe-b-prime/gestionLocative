package com.gestionLocative.gestionLocative.service;

import com.gestionLocative.gestionLocative.dto.AppartementRequestDto;
import com.gestionLocative.gestionLocative.dto.AppartementResponseDto;
import com.gestionLocative.gestionLocative.entity.Appartement;
import com.gestionLocative.gestionLocative.entity.Immeuble;
import com.gestionLocative.gestionLocative.entity.Proprietaire;
import com.gestionLocative.gestionLocative.repository.ImmeubleRepository;
import com.gestionLocative.gestionLocative.dto.ImmeubleResponseDto;
import com.gestionLocative.gestionLocative.repository.ProprietaireRepository;
import com.gestionLocative.gestionLocative.dto.ProprietaireResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import com.gestionLocative.gestionLocative.repository.AppartementRepository;

/**
 * Service métier gérant les appartements.
 * Fournit les opérations CRUD ainsi que les conversions entité/DTO,
 * avec résolution des relations propriétaire et immeuble par identifiant.
 */
@Service
@RequiredArgsConstructor
public class AppartementService {

    private final AppartementRepository appartementRepository;
    private final ProprietaireRepository proprietaireRepository;
    private final ImmeubleRepository immeubleRepository;

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
    public AppartementResponseDto toDto(Appartement appartement) {
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
     * Récupère la liste de tous les appartements.
     *
     * @return La liste des appartements convertis en DTO.
     */
    public List<AppartementResponseDto> getAllAppartements() {
        return appartementRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    /**
     * Récupère un appartement par son identifiant.
     *
     * @param id L'identifiant de l'appartement.
     * @return L'appartement trouvé, converti en DTO.
     * @throws RuntimeException Si l'appartement n'existe pas.
     */
    public AppartementResponseDto getAppartementById(Long id) {
        Appartement appartement = appartementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appartement non trouvé"));
        return toDto(appartement);
    }

    /**
     * Supprime un appartement par son identifiant.
     *
     * @param id L'identifiant de l'appartement à supprimer.
     * @return L'appartement supprimé, converti en DTO.
     * @throws RuntimeException Si l'appartement n'existe pas.
     */
    public AppartementResponseDto deleteById(Long id) {
        Appartement appartement = appartementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appartement non trouvé"));
        appartementRepository.deleteById(id);
        return toDto(appartement);
    }

    /**
     * Met à jour un appartement existant à partir du contenu de la requête.
     *
     * @param id L'identifiant de l'appartement à mettre à jour.
     * @param appartementRequest La requête contenant les nouvelles données et les identifiants des relations.
     * @return L'appartement mis à jour, converti en DTO.
     * @throws RuntimeException Si l'appartement, le propriétaire ou l'immeuble n'existe pas.
     */
    public AppartementResponseDto updateById(Long id, AppartementRequestDto appartementRequest) {
        Proprietaire proprietaire = proprietaireRepository.findById(appartementRequest.getProprietaireId())
                .orElseThrow(() -> new RuntimeException("Propriétaire non trouvé"));
        Immeuble immeuble = immeubleRepository.findById(appartementRequest.getImmeubleId())
                .orElseThrow(() -> new RuntimeException("Immeuble non trouvé"));
        Appartement appartement = appartementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appartement non trouvé"));
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
    public AppartementResponseDto createById(AppartementRequestDto appartementRequest) {
        Proprietaire proprietaire = proprietaireRepository.findById(appartementRequest.getProprietaireId())
                .orElseThrow(() -> new RuntimeException("Propriétaire non trouvé"));
        Immeuble immeuble = immeubleRepository.findById(appartementRequest.getImmeubleId())
                .orElseThrow(() -> new RuntimeException("Immeuble non trouvé"));
        Appartement appartement = toEntity(appartementRequest, proprietaire, immeuble);
        return toDto(appartementRepository.save(appartement));
    }
}