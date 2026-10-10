package com.edu.gestionetudiantsbackend.repository;

import com.edu.gestionetudiantsbackend.model.Paiement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaiementRepository extends JpaRepository<Paiement, Long> {
    boolean existsByNumeroBon(String numeroBon);
    Page<Paiement> findByAnneeId(Long anneeId, Pageable pageable);
    Page<Paiement> findByAnneeIdAndTuteurId(Long anneeId, Long tuteurId, Pageable pageable);
}
