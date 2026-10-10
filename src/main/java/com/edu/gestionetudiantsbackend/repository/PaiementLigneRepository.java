package com.edu.gestionetudiantsbackend.repository;

import com.edu.gestionetudiantsbackend.model.PaiementLigne;
import com.edu.gestionetudiantsbackend.model.TypePaiement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface PaiementLigneRepository extends JpaRepository<PaiementLigne, Long> {

    /** Kol les lignes dyal waliy f mawssim */
    @Query("""
        select l from PaiementLigne l join fetch l.paiement p
        where p.tuteur.id = :tuteurId and p.annee.id = :anneeId
        """)
    List<PaiementLigne> findByTuteurAndAnnee(@Param("tuteurId") Long tuteurId,
                                             @Param("anneeId") Long anneeId);

    /** Wach waliy khless l'inscription? */
    @Query("""
        select count(l) > 0 from PaiementLigne l
        where l.paiement.tuteur.id = :tuteurId and l.paiement.annee.id = :anneeId
          and l.type = :type
        """)
    boolean existsType(@Param("tuteurId") Long tuteurId, @Param("anneeId") Long anneeId,
                       @Param("type") TypePaiement type);

    /** Paiements mensuels dyal groupe d'étudiants (l'tableau sept → juin) */
    @Query("""
        select l from PaiementLigne l join fetch l.paiement p
        where l.etudiant.id in :ids and p.annee.id = :anneeId
          and l.type = :type
        """)
    List<PaiementLigne> mensuelsPourEtudiants(@Param("ids") Collection<Long> ids,
                                              @Param("anneeId") Long anneeId,
                                              @Param("type") TypePaiement type);

    /** Chkon men had les tuteurs khless l'inscription */
    @Query("""
        select distinct p.tuteur.id from PaiementLigne l join l.paiement p
        where p.annee.id = :anneeId and p.tuteur.id in :tuteurIds
          and l.type = :type
        """)
    List<Long> tuteursAyantPayeInscription(@Param("tuteurIds") Collection<Long> tuteurIds,
                                           @Param("anneeId") Long anneeId,
                                           @Param("type") TypePaiement type);
}
