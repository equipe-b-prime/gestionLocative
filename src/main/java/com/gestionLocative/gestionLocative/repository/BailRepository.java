package com.gestionLocative.gestionLocative.repository;

import com.gestionLocative.gestionLocative.entity.Bail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface BailRepository extends JpaRepository<Bail, Long> {

    List<Bail> findByLocataireId(Long locataireId);

    List<Bail> findByAppartementId(Long appartementId);

    List<Bail> findByAppartementIdOrderByDateDebutDesc(Long appartementId);

    boolean existsByAppartementId(Long appartementId);

    @Query("SELECT b FROM Bail b WHERE b.appartement.id = :appartementId " +
           "AND ((b.dateDebut < :dateFin) AND (b.dateFin IS NULL OR b.dateFin > :dateDebut))")
    List<Bail> findConflictingBails(@Param("appartementId") Long appartementId,
                                    @Param("dateDebut") Date dateDebut,
                                    @Param("dateFin") Date dateFin);
}