package com.gestionLocative.gestionLocative.repository;

import com.gestionLocative.gestionLocative.entity.BailFinancierView;
import org.springframework.data.repository.Repository;

import java.util.Optional;

/**
 * Repository en lecture seule sur la vue v_bail_financier.
 */
public interface BailFinancierRepository extends Repository<BailFinancierView, Long> {

    Optional<BailFinancierView> findByBailId(Long bailId);
}