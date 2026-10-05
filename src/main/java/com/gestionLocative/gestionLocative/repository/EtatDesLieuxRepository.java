package com.gestionLocative.gestionLocative.repository;

import com.gestionLocative.gestionLocative.entity.EtatDesLieux;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository JPA pour l'accès aux données des états des lieux.
 * Fournit les opérations CRUD standard sur la table "Etat_les_lieux".
 */
@Repository
public interface EtatDesLieuxRepository extends JpaRepository<EtatDesLieux, Long> {
}