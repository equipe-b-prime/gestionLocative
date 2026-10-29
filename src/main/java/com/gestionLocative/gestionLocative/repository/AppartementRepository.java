package com.gestionLocative.gestionLocative.repository;

import com.gestionLocative.gestionLocative.entity.Appartement;
import com.gestionLocative.gestionLocative.entity.Proprietaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AppartementRepository extends JpaRepository<Appartement, Long> {

    List<Appartement> findByImmeubleId(Long immeubleId);

    /** F6 : appartements d'un immeuble triés par étage croissant. */
    List<Appartement> findByImmeubleIdOrderByEtageAscSurfaceAsc(Long immeubleId);

    /** F9 : recherche multicritères (critères optionnels, filtrage et pagination en base). */
    @Query("SELECT a FROM Appartement a WHERE (:surfaceMin IS NULL OR a.surface >= :surfaceMin) "
         + "AND (:surfaceMax IS NULL OR a.surface <= :surfaceMax) "
         + "AND (:pieces IS NULL OR a.nbPieces = :pieces)")
    org.springframework.data.domain.Page<Appartement> rechercher(
            @Param("surfaceMin") java.math.BigDecimal surfaceMin,
            @Param("surfaceMax") java.math.BigDecimal surfaceMax,
            @Param("pieces") Integer pieces,
            org.springframework.data.domain.Pageable pageable);

    @Query("SELECT DISTINCT a.proprietaire FROM Appartement a WHERE a.immeuble.id = :immeubleId")
    List<Proprietaire> findProprietairesByImmeubleId(@Param("immeubleId") Long immeubleId);

    long countByImmeubleId(Long immeubleId);

    boolean existsByImmeubleId(Long immeubleId);

    Optional<Appartement> findFirstByImmeubleId(Long immeubleId);

    /** F17 : appartements qu'aucun bail ne couvre à la date donnée (dateFin exclue, comme pour le chevauchement). */
    @Query("SELECT a FROM Appartement a WHERE NOT EXISTS (SELECT b.id FROM Bail b WHERE b.appartement = a "
         + "AND b.dateDebut <= :date AND (b.dateFin IS NULL OR b.dateFin > :date)) "
         + "ORDER BY a.immeuble.id, a.etage, a.surface")
    List<Appartement> findLibres(@Param("date") java.util.Date date);
}
