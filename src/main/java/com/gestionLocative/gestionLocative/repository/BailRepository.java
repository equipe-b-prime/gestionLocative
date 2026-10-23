package com.gestionLocative.gestionLocative.repository;

import com.gestionLocative.gestionLocative.entity.Bail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository JPA pour l'accès aux données des baux.
 * Fournit les opérations CRUD standard sur la table "bail"
 * ainsi que des requêtes dérivées.
 */
@Repository
public interface BailRepository extends JpaRepository<Bail, Long> {

    /**
     * Récupère les baux d'un locataire.
     *
     * @param locataireId L'identifiant du locataire.
     * @return Liste<Bail> Les baux du locataire.
     */
    List<Bail> findByLocataireId(Long locataireId);

    /**
     * Récupère les baux d'un appartement, triés par date de début décroissante.
     *
     * @param appartementId L'identifiant de l'appartement.
     * @return List<Bail> Les baux de l'appartement, triés du plus récent au plus ancien.
     */
    List<Bail> findByAppartementId(Long appartementId);

    /**
     * Récupère les baux d'un appartement, triés par date de début décroissante.
     *
     * @param appartementId L'identifiant de l'appartement.
     * @return List<Bail> Les baux de l'appartement, triés du plus récent au plus ancien.
     */
    List<Bail> findByAppartementIdOrderByDateDebutDesc(Long appartementId);

    /**
     * Compte les baux en cours d'un immeuble.
     * Un bail est en cours si sa date de début est passée et que sa date de fin est nulle ou future.
     *
     * @param immeubleId L'identifiant de l'immeuble.
     * @param maintenant La date et heure de référence.
     * @return Le nombre de baux en cours de l'immeuble.
     */
    @Query("SELECT COUNT(b) FROM Bail b WHERE b.appartement.id = :immeubleId AND b.dateDebut <= :maintenant AND (b.dateFin IS NULL OR b.dateFin >= :maintenant)")
    long countBauxEnCoursParImmeuble(@Param("immeubleId") Long immeubleId, @Param("maintenant") LocalDateTime maintenant);

    /**
     * Vérifie si un appartement a déjà fait l'objet d'au moins un bail.
     *
     * @param appartementId L'identifiant de l'appartement.
     * @return true si au moins un bail existe pour cet appartement.
     */
    boolean existsByAppartementId(Long appartementId);
}
