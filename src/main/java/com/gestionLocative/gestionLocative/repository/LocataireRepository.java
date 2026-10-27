package com.gestionLocative.gestionLocative.repository;

import com.gestionLocative.gestionLocative.entity.Locataire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocataireRepository extends JpaRepository<Locataire, Long> {
    boolean existsByEmail(String email);
    boolean existsByEmailAndIdNot(String email, Long id);
}