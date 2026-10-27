package com.gestionLocative.gestionLocative.repository;

import com.gestionLocative.gestionLocative.entity.Proprietaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProprietaireRepository extends JpaRepository<Proprietaire, Long> {
    boolean existsByEmail(String email);
    boolean existsByEmailAndIdNot(String email, Long id);
}