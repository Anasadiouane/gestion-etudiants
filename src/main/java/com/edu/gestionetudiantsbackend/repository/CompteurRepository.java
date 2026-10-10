package com.edu.gestionetudiantsbackend.repository;

import com.edu.gestionetudiantsbackend.model.Compteur;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompteurRepository extends JpaRepository<Compteur, String> {
}
