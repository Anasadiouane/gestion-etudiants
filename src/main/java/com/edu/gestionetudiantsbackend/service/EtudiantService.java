package com.edu.gestionetudiantsbackend.service;

import com.edu.gestionetudiantsbackend.dto.EtudiantRequest;
import com.edu.gestionetudiantsbackend.exception.NotFoundException;
import com.edu.gestionetudiantsbackend.model.Etudiant;
import com.edu.gestionetudiantsbackend.model.Genre;
import com.edu.gestionetudiantsbackend.repository.EtudiantRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class EtudiantService {

    private final EtudiantRepository etudiants;
    private final TuteurService tuteurService;

    public EtudiantService(EtudiantRepository etudiants, TuteurService tuteurService) {
        this.etudiants = etudiants;
        this.tuteurService = tuteurService;
    }

    @Transactional(readOnly = true)
    public Page<Etudiant> search(Genre genre, String douar, String q, Pageable pageable) {
        return etudiants.search(genre,
                TuteurService.blankToNull(douar),
                TuteurService.blankToNull(q),
                pageable);
    }

    @Transactional(readOnly = true)
    public Etudiant get(Long id) {
        return etudiants.findById(id)
                .orElseThrow(() -> new NotFoundException("Étudiant introuvable: " + id));
    }

    @Transactional(readOnly = true)
    public List<String> douars() {
        return etudiants.findDistinctDouars();
    }

    public Etudiant create(EtudiantRequest r) {
        return etudiants.save(apply(new Etudiant(), r));
    }

    public Etudiant update(Long id, EtudiantRequest r) {
        return etudiants.save(apply(get(id), r));
    }

    /** "Suppression" = archivage: l'historique dyal paiements ybqa s7i7. */
    public void archive(Long id) {
        Etudiant e = get(id);
        e.setActif(false);
        e.setDateSortie(LocalDate.now());
        if (e.getMoisSortie() == null) {
            e.setMoisSortie(LocalDate.now().getMonthValue());
        }
        etudiants.save(e);
    }

    private Etudiant apply(Etudiant e, EtudiantRequest r) {
        e.setNom(r.nom().trim());
        e.setPrenom(r.prenom().trim());
        e.setGenre(r.genre());
        e.setDouar(TuteurService.blankToNull(r.douar()));
        e.setNiveau(TuteurService.blankToNull(r.niveau()));
        e.setTuteur(tuteurService.get(r.tuteurId()));
        e.setMoisDebut(r.moisDebut() != null ? r.moisDebut() : 9);
        e.setMoisSortie(r.moisSortie());
        return e;
    }
}
