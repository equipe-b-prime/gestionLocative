package com.gestionLocative.gestionLocative.repository;

import com.gestionLocative.gestionLocative.entity.Bail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository JPA pour l'accès aux données des baux.
 * Fournit les opérations CRUD standard sur la table "bail"
 * ainsi que des requêtes dérivées.
 */
@Repository
public interface BailRepository extends JpaRepository<Bail, Long> {

    /**
     * Récupère les baux d'un locataire.
     *
     * @param locataireId L'identifiant du locataire.
     * @return List<Bail> Les baux du locataire.
     */
    List<Bail> findByLocataireId(Long locataireId);

    /**
     * Récupère les baux d'un appartement.
     *
     * @param appartementId L'identifiant de l'appartement.
     * @return List<Bail> Les baux de l'appartement.
     */
    List<Bail> findByAppartementId(Long appartementId);
}