package com.gestionLocative.gestionLocative.repository;

import com.gestionLocative.gestionLocative.entity.Paiement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository JPA pour l'accès aux données des paiements de loyer.
 * Fournit les opérations CRUD standard sur la table "paiement".
 */
@Repository
public interface PaiementRepository extends JpaRepository<Paiement, Long> {
}