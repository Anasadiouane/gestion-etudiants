package com.edu.gestionetudiantsbackend.repository;

import com.edu.gestionetudiantsbackend.model.AnneeScolaire;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AnneeScolaireRepository extends JpaRepository<AnneeScolaire, Long> {
    Optional<AnneeScolaire> findFirstByActiveTrue();
    List<AnneeScolaire> findAllByOrderByIdDesc();
    boolean existsByLibelle(String libelle);
}
