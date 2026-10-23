package com.gestionLocative.gestionLocative.repository;

import com.gestionLocative.gestionLocative.entity.Proprietaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository JPA pour l'accès aux données des propriétaires.
 * Fournit les opérations CRUD standard sur la table "proprietaire"
 * ainsi que des requêtes dérivées.
 */
@Repository
public interface ProprietaireRepository extends JpaRepository<Proprietaire, Long> {

    /**
     * Vérifie si un propriétaire porte déjà cet email.
     *
     * @param email L'email recherché.
     * @return true si un propriétaire utilise déjà cet email.
     */
    boolean existsByEmail(String email);

    /**
     * Vérifie si un autre propriétaire (identifiant différent) porte cet email.
     *
     * @param email L'email recherché.
     * @param id L'identifiant du propriétaire à exclure (celui en cours de modification).
     * @return true si un autre propriétaire utilise déjà cet email.
     */
    boolean existsByEmailAndIdNot(String email, Long id);
}
