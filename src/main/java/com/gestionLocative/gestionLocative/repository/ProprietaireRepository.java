package com.gestionLocative.gestionLocative.repository;

import com.gestionLocative.gestionLocative.entity.Proprietaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository JPA pour l'accès aux données des propriétaires.
 * Fournit les opérations CRUD standard sur la table "proprietaire".
 */
@Repository
public interface ProprietaireRepository extends JpaRepository<Proprietaire, Long> {
}