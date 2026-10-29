package com.gestionLocative.gestionLocative.repository;

import com.gestionLocative.gestionLocative.entity.Revision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface RevisionRepository extends JpaRepository<Revision, Long> {

    List<Revision> findByBailIdOrderByDateEffetAsc(Long bailId);

    Optional<Revision> findTopByBailIdOrderByDateEffetDesc(Long bailId);

    boolean existsByBailIdAndDateEffet(Long bailId, Date dateEffet);
}