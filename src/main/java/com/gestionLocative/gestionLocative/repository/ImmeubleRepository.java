package com.gestionLocative.gestionLocative.repository;

import com.gestionLocative.gestionLocative.entity.Immeuble;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository JPA pour l'accès aux données des immeubles.
 * Fournit les opérations CRUD standard sur la table "immeuble"
 * ainsi que des requêtes dérivées.
 */
@Repository
public interface ImmeubleRepository extends JpaRepository<Immeuble, Long> {

    /**
     * Vérifie si un propriétaire possède au moins un immeuble.
     *
     * @param proprietaireId L'identifiant du propriétaire.
     * @return true si au moins un immeuble appartient à ce propriétaire.
     */
    boolean existsByProprietaireId(Long proprietaireId);

    /**
     * Récupère les immeubles d'un propriétaire.
     *
     * @param proprietaireId L'identifiant du propriétaire.
     * @return List<Immeuble> Les immeubles du propriétaire.
     */
    List<Immeuble> findByProprietaireId(Long proprietaireId);
}
