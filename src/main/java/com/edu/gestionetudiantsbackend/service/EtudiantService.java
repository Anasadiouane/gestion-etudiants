package com.edu.gestionetudiantsbackend.service;

import com.edu.gestionetudiantsbackend.dto.EtudiantRequest;
import com.edu.gestionetudiantsbackend.exception.ConflictException;
import com.edu.gestionetudiantsbackend.exception.NotFoundException;
import com.edu.gestionetudiantsbackend.model.AnneeScolaire;
import com.edu.gestionetudiantsbackend.model.Etudiant;
import com.edu.gestionetudiantsbackend.model.Genre;
import com.edu.gestionetudiantsbackend.repository.EtudiantRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class EtudiantService {

    private final EtudiantRepository etudiants;
    private final TuteurService tuteurService;
    private final SoldeService soldes;

    public EtudiantService(EtudiantRepository etudiants, TuteurService tuteurService, SoldeService soldes) {
        this.etudiants = etudiants;
        this.tuteurService = tuteurService;
        this.soldes = soldes;
    }

    @Transactional(readOnly = true)
    public Page<Etudiant> search(Genre genre, String douar, String q, Pageable pageable) {
        Page<Etudiant> page = etudiants.search(genre,
                TuteurService.blankToNull(douar),
                TuteurService.blankToNull(q),
                pageable);
        remplirTarifs(page.getContent());
        return page;
    }

    /**
     * Calcule le tarif mensuel actuel de chaque étudiant (100 / 50 / tarif perso)
     * pour le mois courant (ou le premier mois dû si on est en juillet-août).
     */
    private void remplirTarifs(Collection<Etudiant> liste) {
        AnneeScolaire annee;
        try {
            annee = soldes.anneeActive();
        } catch (ConflictException ex) {
            return; // pas d'année active: on n'affiche pas de tarif
        }
        int mois = LocalDate.now().getMonthValue();
        Map<Long, Map<Long, Map<Integer, BigDecimal>>> parWaliy = new HashMap<>();
        for (Etudiant e : liste) {
            Map<Long, Map<Integer, BigDecimal>> tarifs =
                    parWaliy.computeIfAbsent(e.getTuteur().getId(), id -> soldes.tarifs(id, annee));
            Map<Integer, BigDecimal> m = tarifs.get(e.getId());
            if (m == null || m.isEmpty()) continue;
            BigDecimal t = m.get(mois);
            e.setTarifActuel(t != null ? t : m.values().iterator().next());
        }
    }

    @Transactional(readOnly = true)
    public Etudiant get(Long id) {
        Etudiant e = etudiants.findById(id)
                .orElseThrow(() -> new NotFoundException("Étudiant introuvable: " + id));
        remplirTarifs(List.of(e));
        return e;
    }

    @Transactional(readOnly = true)
    public List<String> douars() {
        return Douars.CODES;
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
        e.setDouar(Douars.verifier(r.douar()));
        e.setNiveau(TuteurService.blankToNull(r.niveau()));
        e.setTuteur(tuteurService.get(r.tuteurId()));
        e.setTarifPersonnalise(r.tarifPersonnalise());
        e.setMoisDebut(r.moisDebut() != null ? r.moisDebut() : 9);
        e.setMoisSortie(r.moisSortie());
        return e;
    }
}
