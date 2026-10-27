package com.gestionLocative.gestionLocative.repository;

import com.gestionLocative.gestionLocative.entity.Immeuble;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImmeubleRepository extends JpaRepository<Immeuble, Long> {
    List<Immeuble> findByProprietaireId(Long proprietaireId);
    boolean existsByProprietaireId(Long proprietaireId);
}