package com.gestionLocative.gestionLocative.repository;

import com.gestionLocative.gestionLocative.entity.Quittance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository JPA pour l'accès aux données des quittances de loyer.
 * Fournit les opérations CRUD standard sur la table "quittance".
 */
@Repository
public interface QuittanceRepository extends JpaRepository<Quittance, Long> {
}