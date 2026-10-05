package com.gestionLocative.gestionLocative.repository;

import com.gestionLocative.gestionLocative.entity.Appartement;
import com.gestionLocative.gestionLocative.entity.Proprietaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository JPA pour l'accès aux données des appartements.
 * Fournit les opérations CRUD standard sur la table "appartement"
 * ainsi que des requêtes personnalisées avec jointures.
 */
@Repository
public interface AppartementRepository extends JpaRepository<Appartement, Long> {

    /**
     * Récupère les appartements d'un immeuble.
     *
     * @param immeubleId L'identifiant de l'immeuble.
     * @return List<Appartement> Les appartements de l'immeuble.
     */
    List<Appartement> findByImmeubleId(Long immeubleId);

    /**
     * Récupère les propriétaires distincts des appartements d'un immeuble
     * (le propriétaire est porté par l'appartement dans le modèle).
     *
     * @param immeubleId L'identifiant de l'immeuble.
     * @return List<Proprietaire> Les propriétaires distincts.
     */
    @Query("SELECT DISTINCT a.proprietaire FROM Appartement a WHERE a.immeuble.id = :immeubleId")
    List<Proprietaire> findProprietairesByImmeubleId(@Param("immeubleId") Long immeubleId);
}