package com.gestionLocative.gestionLocative.repository;

import com.gestionLocative.gestionLocative.entity.LocataireSituationView;
import org.springframework.data.repository.Repository;

import java.util.Optional;

/**
 * Repository en lecture seule sur la vue v_locataire_situation.
 */
public interface LocataireSituationRepository extends Repository<LocataireSituationView, Long> {

    Optional<LocataireSituationView> findByLocataireId(Long locataireId);
}