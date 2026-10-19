package com.edu.gestionetudiantsbackend.repository;

import com.edu.gestionetudiantsbackend.model.Paiement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PaiementRepository extends JpaRepository<Paiement, Long> {
    boolean existsByNumeroBon(String numeroBon);

    Page<Paiement> findByAnneeId(Long anneeId, Pageable pageable);

    Page<Paiement> findByAnneeIdAndTuteurId(Long anneeId, Long tuteurId, Pageable pageable);

    /** Recherche par nom de waliy ou numéro de bon (tri via Pageable) */
    @Query("""
        select p from Paiement p
        where p.annee.id = :anneeId
          and (:tuteurId is null or p.tuteur.id = :tuteurId)
          and (:q is null or lower(p.tuteur.nom) like lower(concat('%', :q, '%'))
                          or lower(p.numeroBon) like lower(concat('%', :q, '%')))
        """)
    Page<Paiement> rechercher(@Param("anneeId") Long anneeId,
                              @Param("tuteurId") Long tuteurId,
                              @Param("q") String q,
                              Pageable pageable);
}
