package com.gestionLocative.gestionLocative.repository;

import com.gestionLocative.gestionLocative.entity.Bail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Repository
public interface BailRepository extends JpaRepository<Bail, Long> {

    List<Bail> findByLocataireId(Long locataireId);

    List<Bail> findByAppartementId(Long appartementId);

    List<Bail> findByAppartementIdOrderByDateDebutDesc(Long appartementId);
    List<Bail> findByAppartementIdOrderByDateDebutAsc(Long appartementId);

    boolean existsByAppartementId(Long appartementId);

    @Query("SELECT b FROM Bail b WHERE b.appartement.id = :appartementId " +
           "AND ((b.dateDebut < :dateFin) AND (b.dateFin IS NULL OR b.dateFin > :dateDebut))")
    List<Bail> findConflictingBails(@Param("appartementId") Long appartementId,
                                    @Param("dateDebut") Date dateDebut,
                                    @Param("dateFin") Date dateFin);

    @Query("SELECT COUNT(b) FROM Bail b JOIN b.appartement a WHERE a.immeuble.id = :immeubleId AND b.dateDebut <= :maintenant AND (b.dateFin IS NULL OR b.dateFin >= :maintenant)")
    long countBauxEnCoursParImmeuble(@Param("immeubleId") Long immeubleId, @Param("maintenant") LocalDateTime maintenant);

    /** F11/F14 : baux (autres que {@code excludeId}) qui recouvrent la période donnée. */
    @Query("SELECT b FROM Bail b WHERE b.appartement.id = :appartementId AND b.id <> :excludeId "
         + "AND b.dateDebut < :dateFin AND (b.dateFin IS NULL OR b.dateFin > :dateDebut)")
    List<Bail> findConflictingBailsExcluding(@Param("appartementId") Long appartementId,
                                             @Param("dateDebut") Date dateDebut,
                                             @Param("dateFin") Date dateFin,
                                             @Param("excludeId") Long excludeId);

    /** F16 : baux dont la date de fin est dépassée et qui ne sont pas encore clôturés. */
    @Query("SELECT b FROM Bail b WHERE b.dateFin < :aujourdhui "
         + "AND (b.statut IS NULL OR b.statut = com.gestionLocative.gestionLocative.entity.StatutBail.ACTIF)")
    List<Bail> findBauxATerminer(@Param("aujourdhui") Date aujourdhui);
}
