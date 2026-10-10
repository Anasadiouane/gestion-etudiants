package com.edu.gestionetudiantsbackend.repository;

import com.edu.gestionetudiantsbackend.model.Tuteur;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TuteurRepository extends JpaRepository<Tuteur, Long> {

    @Query("""
        select t from Tuteur t
        where (:q is null or lower(t.nom) like lower(concat('%', :q, '%'))
                          or t.telephone like concat('%', :q, '%'))
        """)
    Page<Tuteur> search(@Param("q") String q, Pageable pageable);
}
