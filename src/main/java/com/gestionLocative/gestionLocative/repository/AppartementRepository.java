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

    @Query("SELECT DISTINCT a.proprietaire FROM Appartement a WHERE a.immeuble.id = :immeubleId")
    List<Proprietaire> findProprietairesByImmeubleId(@Param("immeubleId") Long immeubleId);

    long countByImmeubleId(Long immeubleId);

    boolean existsByImmeubleId(Long immeubleId);

    Optional<Appartement> findFirstByImmeubleId(Long immeubleId);
}