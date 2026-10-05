package com.gestionLocative.gestionLocative.repository;

import com.gestionLocative.gestionLocative.entity.TypeConstat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository JPA pour l'accès aux données des types de constat.
 * Fournit les opérations CRUD standard sur la table "type_constat".
 */
@Repository
public interface TypeConstatRepository extends JpaRepository<TypeConstat, Long> {
}