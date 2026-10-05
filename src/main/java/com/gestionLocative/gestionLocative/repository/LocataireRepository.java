package com.gestionLocative.gestionLocative.repository;

import com.gestionLocative.gestionLocative.entity.Locataire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository JPA pour l'accès aux données des locataires.
 * Fournit les opérations CRUD standard sur la table "locataire".
 */
@Repository
public interface LocataireRepository extends JpaRepository<Locataire, Long> {
}