package com.gestionLocative.gestionLocative.repository;

import com.gestionLocative.gestionLocative.entity.Locataire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository JPA pour l'accès aux données des locataires.
 * Fournit les opérations CRUD standard sur la table "locataire"
 * ainsi que des requêtes dérivées.
 */
@Repository
public interface LocataireRepository extends JpaRepository<Locataire, Long> {

    /**
     * Vérifie si un locataire porte déjà cet email.
     *
     * @param email L'email recherché.
     * @return true si un locataire utilise déjà cet email.
     */
    boolean existsByEmail(String email);

    /**
     * Vérifie si un autre locataire (identifiant différent) porte cet email.
     *
     * @param email L'email recherché.
     * @param id L'identifiant du locataire à exclure (celui en cours de modification).
     * @return true si un autre locataire utilise déjà cet email.
     */
    boolean existsByEmailAndIdNot(String email, Long id);
}
