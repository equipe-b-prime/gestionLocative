package com.gestionLocative.gestionLocative.repository;

import com.gestionLocative.gestionLocative.entity.Appartement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository JPA pour l'accès aux données des appartements.
 * Fournit les opérations CRUD standard sur la table "appartement"
 * ainsi que des requêtes dérivées.
 */
@Repository
public interface AppartementRepository extends JpaRepository<Appartement, Long> {

    /**
     * Récupère les appartements d'un immeuble, triés par étage croissant.
     *
     * @param immeubleId L'identifiant de l'immeuble.
     * @return La liste des appartements de l'immeuble, triés par étage.
     */
    /**
     * Récupère les appartements d'un immeuble.
     *
     * @param immeubleId L'identifiant de l'immeuble.
     * @return La liste des appartements de l'immeuble.
     */
    List<Appartement> findByImmeubleId(Long immeubleId);

    /**
     * Récupère les appartements d'un immeuble, triés par étage croissant.
     *
     * @param immeubleId L'identifiant de l'immeuble.
     * @return La liste des appartements de l'immeuble, triés par étage.
     */
    List<Appartement> findByImmeubleIdOrderByEtageAsc(Long immeubleId);

    /**
     * Compte les appartements d'un immeuble.
     *
     * @param immeubleId L'identifiant de l'immeuble.
     * @return le nombre d'appartements de l'immeuble.
     */
    long countByImmeubleId(Long immeubleId);

    /**
     * Vérifie si un immeuble possède au moins un appartement.
     *
     * @param immeubleId L'identifiant de l'immeuble.
     * @return true si au moins un appartement appartient à cet immeuble.
     */
    boolean existsByImmeubleId(Long immeubleId);

    @Query("SELECT a FROM Appartement a WHERE (:surfaceMin IS NULL OR a.surface >= :surfaceMin) AND (:surfaceMax IS NULL OR a.surface <= :surfaceMax) AND (:pieces IS NULL OR a.nbPieces = :pieces)")
    List<Appartement> findBySearchCriteria(
            @Param("surfaceMin") Double surfaceMin,
            @Param("surfaceMax") Double surfaceMax,
            @Param("pieces") Integer pieces
    );
}
