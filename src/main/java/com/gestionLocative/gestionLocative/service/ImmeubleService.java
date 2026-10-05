package com.gestionLocative.gestionLocative.service;

import com.gestionLocative.gestionLocative.repository.AppartementRepository;
import com.gestionLocative.gestionLocative.service.AppartementService;
import com.gestionLocative.gestionLocative.dto.AppartementResponseDto;
import com.gestionLocative.gestionLocative.entity.Immeuble;
import com.gestionLocative.gestionLocative.entity.Proprietaire;
import com.gestionLocative.gestionLocative.dto.ImmeubleRequestDto;
import com.gestionLocative.gestionLocative.dto.ImmeubleResponseDto;
import com.gestionLocative.gestionLocative.dto.ProprietaireResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import com.gestionLocative.gestionLocative.repository.ImmeubleRepository;

/**
 * Service métier gérant les immeubles.
 * Fournit les opérations CRUD ainsi que les conversions entité/DTO.
 */
@Service
@RequiredArgsConstructor
public class ImmeubleService {

    private final ImmeubleRepository immeubleRepository;
    private final AppartementService appartementService;
    private final AppartementRepository appartementRepository;

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
    public ImmeubleResponseDto toDto(Immeuble immeuble) {
        return ImmeubleResponseDto.builder()
                .id(immeuble.getId())
                .nom(immeuble.getNom())
                .adresse(immeuble.getAdresse())
                .nbEtages(immeuble.getNbEtages())
                .build();
    }

    /**
     * Récupère la liste de tous les immeubles.
     *
     * @return La liste des immeubles convertis en DTO.
     */
    public List<ImmeubleResponseDto> getAllImmeubles() {
        return immeubleRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    /**
     * Récupère un immeuble par son identifiant.
     *
     * @param id L'identifiant de l'immeuble.
     * @return L'immeuble trouvé, converti en DTO.
     * @throws RuntimeException Si l'immeuble n'existe pas.
     */
    public ImmeubleResponseDto getImmeubleById(Long id) {
        Immeuble immeuble = immeubleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Immeuble non trouvé"));
        return toDto(immeuble);
    }

    /**
     * Supprime un immeuble par son identifiant.
     *
     * @param id L'identifiant de l'immeuble à supprimer.
     * @return L'immeuble supprimé, converti en DTO.
     * @throws RuntimeException Si l'immeuble n'existe pas.
     */
    public ImmeubleResponseDto deleteById(Long id) {
        Immeuble immeuble = immeubleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Immeuble non trouvé"));
        immeubleRepository.deleteById(id);
        return toDto(immeuble);
    }

    /**
     * Met à jour un immeuble existant à partir du contenu de la requête.
     *
     * @param id L'identifiant de l'immeuble à mettre à jour.
     * @param immeubleRequest La requête contenant les nouvelles données.
     * @return L'immeuble mis à jour, converti en DTO.
     * @throws RuntimeException Si l'immeuble n'existe pas.
     */
    public ImmeubleResponseDto updateById(Long id, ImmeubleRequestDto immeubleRequest) {
        Immeuble immeuble = immeubleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Immeuble non trouvé"));
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
    public ImmeubleResponseDto createById(ImmeubleRequestDto immeubleRequest) {
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
                .map(appartementService::toDto)
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
                .map(this::toProprietaireResponse)
                .toList();
    }

    /**
     * Construit la réponse d'un propriétaire.
     *
     * @param proprietaire L'entité propriétaire.
     * @return ProprietaireResponseDto La réponse du propriétaire.
     */
    private ProprietaireResponseDto toProprietaireResponse(Proprietaire proprietaire) {
        return ProprietaireResponseDto.builder()
                .id(proprietaire.getId())
                .nom(proprietaire.getNom())
                .prenom(proprietaire.getPrenom())
                .numero(proprietaire.getNumero())
                .adresse(proprietaire.getAdresse())
                .build();
    }
}