package com.gestionLocative.gestionLocative.repository;

import com.gestionLocative.gestionLocative.entity.Appartement;
import com.gestionLocative.gestionLocative.entity.Bail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository JPA pour l'accès aux données des baux.
 */
@Repository
public interface BailRepository extends JpaRepository<Bail, Long> {

    /** Récupère les baux d'un locataire. */
    List<Bail> findByLocataireId(Long locataireId);

    /** Récupère les baux d'un appartement. */
    List<Bail> findByAppartementId(Long appartementId);

    /** Récupère les baux d'un appartement triés par date de début décroissante. */
    List<Bail> findByAppartementIdOrderByDateDebutDesc(Long appartementId);

    /** Vérifie si un appartement a déjà fait l'objet d'au moins un bail. */
    boolean existsByAppartementId(Long appartementId);

    /** Récupère les baux résiliés d'un appartement. */
    List<Bail> findByAppartementIdAndDateClotureNotNull(Long appartementId);

    /**
     * Récupère les appartements libres à une date donnée.
     * Un appartement est libre si aucun bail non résilié ne couvre la date demandée.
     */
    @Query("SELECT DISTINCT a FROM Appartement a " +
           "WHERE NOT EXISTS (" +
           "    SELECT b FROM Bail b " +
           "    WHERE b.appartement.id = a.id " +
           "    AND b.dateDebut <= :date " +
           "    AND (b.dateFin IS NULL OR b.dateFin >= :date) " +
           "    AND b.dateCloture IS NULL" +
           ")")
    List<Appartement> findLibresByDate(@Param("date") Date date);

    /**
     * Récupère les baux d'un immeuble.
     */
    @Query("SELECT b FROM Bail b WHERE b.appartement.immeuble.id = :immeubleId")
    List<Bail> findByImmeubleId(@Param("immeubleId") Long immeubleId);

    /**
     * Compte les baux en cours d'un immeuble.
     */
    @Query("SELECT COUNT(b) FROM Bail b WHERE b.appartement.immeuble.id = :immeubleId AND b.dateDebut <= :maintenant AND (b.dateFin IS NULL OR b.dateFin >= :maintenant)")
    long countBauxEnCoursParImmeuble(@Param("immeubleId") Long immeubleId, @Param("maintenant") LocalDateTime maintenant);
}
