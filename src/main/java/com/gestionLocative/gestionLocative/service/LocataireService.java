package com.gestionLocative.gestionLocative.service;

import com.gestionLocative.gestionLocative.repository.BailRepository;
import com.gestionLocative.gestionLocative.service.BailService;
import com.gestionLocative.gestionLocative.dto.BailResponseDto;
import com.gestionLocative.gestionLocative.entity.Locataire;
import com.gestionLocative.gestionLocative.dto.LocataireRequestDto;
import com.gestionLocative.gestionLocative.dto.LocataireResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import com.gestionLocative.gestionLocative.repository.LocataireRepository;

/**
 * Service métier gérant les locataires.
 * Fournit les opérations CRUD ainsi que les conversions entité/DTO.
 */
@Service
@RequiredArgsConstructor
public class LocataireService {

    private final LocataireRepository locataireRepository;
    private final BailService bailService;
    private final BailRepository bailRepository;

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
    public LocataireResponseDto toDto(Locataire locataire) {
        return LocataireResponseDto.builder()
                .id(locataire.getId())
                .nom(locataire.getNom())
                .prenom(locataire.getPrenom())
                .numero(locataire.getNumero())
                .email(locataire.getEmail())
                .build();
    }

    /**
     * Récupère la liste de tous les locataires.
     *
     * @return La liste des locataires convertis en DTO.
     */
    public List<LocataireResponseDto> getAllLocataires() {
        return locataireRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    /**
     * Récupère un locataire par son identifiant.
     *
     * @param id L'identifiant du locataire.
     * @return Le locataire trouvé, converti en DTO.
     * @throws RuntimeException Si le locataire n'existe pas.
     */
    public LocataireResponseDto getLocataireById(Long id) {
        Locataire locataire = locataireRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Locataire non trouvé"));
        return toDto(locataire);
    }

    /**
     * Supprime un locataire par son identifiant.
     *
     * @param id L'identifiant du locataire à supprimer.
     * @return Le locataire supprimé, converti en DTO.
     * @throws RuntimeException Si le locataire n'existe pas.
     */
    public LocataireResponseDto deleteById(Long id) {
        Locataire locataire = locataireRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Locataire non trouvé"));
        locataireRepository.deleteById(id);
        return toDto(locataire);
    }

    /**
     * Met à jour un locataire existant à partir du contenu de la requête.
     *
     * @param id L'identifiant du locataire à mettre à jour.
     * @param locataireRequest La requête contenant les nouvelles données.
     * @return Le locataire mis à jour, converti en DTO.
     * @throws RuntimeException Si le locataire n'existe pas.
     */
    public LocataireResponseDto updateById(Long id, LocataireRequestDto locataireRequest) {
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
    public LocataireResponseDto createById(LocataireRequestDto locataireRequest) {
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
                .map(bailService::toDto)
                .toList();
    }
}