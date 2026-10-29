package com.gestionLocative.gestionLocative.repository;

import com.gestionLocative.gestionLocative.entity.BailHistorique;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BailHistoriqueRepository extends JpaRepository<BailHistorique, Long> {
    List<BailHistorique> findByBailIdOrderByDateOperationAsc(Long bailId);
}
