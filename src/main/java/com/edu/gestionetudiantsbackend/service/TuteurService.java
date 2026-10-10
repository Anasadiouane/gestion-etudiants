package com.edu.gestionetudiantsbackend.service;


import com.edu.gestionetudiantsbackend.dto.TuteurRequest;
import com.edu.gestionetudiantsbackend.exception.ConflictException;
import com.edu.gestionetudiantsbackend.exception.NotFoundException;
import com.edu.gestionetudiantsbackend.model.Tuteur;
import com.edu.gestionetudiantsbackend.repository.EtudiantRepository;
import com.edu.gestionetudiantsbackend.repository.TuteurRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TuteurService {

    private final TuteurRepository tuteurs;
    private final EtudiantRepository etudiants;

    public TuteurService(TuteurRepository tuteurs, EtudiantRepository etudiants) {
        this.tuteurs = tuteurs;
        this.etudiants = etudiants;
    }

    @Transactional(readOnly = true)
    public Page<Tuteur> search(String q, Pageable pageable) {
        return tuteurs.search(blankToNull(q), pageable);
    }

    @Transactional(readOnly = true)
    public Tuteur get(Long id) {
        return tuteurs.findById(id)
                .orElseThrow(() -> new NotFoundException("Waliy introuvable: " + id));
    }

    public Tuteur create(TuteurRequest r) {
        return tuteurs.save(apply(new Tuteur(), r));
    }

    public Tuteur update(Long id, TuteurRequest r) {
        return tuteurs.save(apply(get(id), r));
    }

    public void delete(Long id) {
        Tuteur t = get(id);
        if (!etudiants.findByTuteurId(id).isEmpty()) {
            throw new ConflictException("Impossible de supprimer: ce waliy a des étudiants.");
        }
        tuteurs.delete(t);
    }

    private Tuteur apply(Tuteur t, TuteurRequest r) {
        t.setNom(r.nom().trim());
        t.setTelephone(blankToNull(r.telephone()));
        t.setDouar(blankToNull(r.douar()));
        return t;
    }

    static String blankToNull(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }
}
