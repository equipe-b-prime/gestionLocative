package com.gestionLocative.gestionLocative.repository;

import com.gestionLocative.gestionLocative.entity.Immeuble;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository JPA pour l'accès aux données des immeubles.
 * Fournit les opérations CRUD standard sur la table "immeuble".
 */
@Repository
public interface ImmeubleRepository extends JpaRepository<Immeuble, Long> {
}