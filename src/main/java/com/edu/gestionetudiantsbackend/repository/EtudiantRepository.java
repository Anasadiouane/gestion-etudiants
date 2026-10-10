package com.edu.gestionetudiantsbackend.repository;

import com.edu.gestionetudiantsbackend.model.Etudiant;
import com.edu.gestionetudiantsbackend.model.Genre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EtudiantRepository extends JpaRepository<Etudiant, Long> {

    /**
     * Liste paginée + filtres:
     *  genre (filles/garçons), douar (adresse), q (recherche f smiya dyal tilmid wla waliy)
     */
    @Query("""
        select e from Etudiant e join e.tuteur t
        where e.actif = true
          and (:genre is null or e.genre = :genre)
          and (:douar is null or lower(e.douar) like lower(concat('%', :douar, '%')))
          and (:q is null or lower(e.nom)    like lower(concat('%', :q, '%'))
                          or lower(e.prenom) like lower(concat('%', :q, '%'))
                          or lower(t.nom)    like lower(concat('%', :q, '%')))
        """)
    Page<Etudiant> search(@Param("genre") Genre genre,
                          @Param("douar") String douar,
                          @Param("q") String q,
                          Pageable pageable);

    List<Etudiant> findByTuteurId(Long tuteurId);

    List<Etudiant> findByActifTrue();

    @Query("select distinct e.douar from Etudiant e where e.douar is not null order by e.douar")
    List<String> findDistinctDouars();
}
