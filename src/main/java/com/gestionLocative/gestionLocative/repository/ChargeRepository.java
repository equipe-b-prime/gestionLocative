package com.gestionLocative.gestionLocative.repository;

import com.gestionLocative.gestionLocative.entity.Charge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository JPA pour l'accès aux données des charges d'un immeuble.
 * Fournit les opérations CRUD standard sur la table "charge".
 */
@Repository
public interface ChargeRepository extends JpaRepository<Charge, Long> {
}