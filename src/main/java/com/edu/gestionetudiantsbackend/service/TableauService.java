package com.edu.gestionetudiantsbackend.service;

import com.edu.gestionetudiantsbackend.dto.TableauLigne;
import com.edu.gestionetudiantsbackend.model.*;
import com.edu.gestionetudiantsbackend.repository.EtudiantRepository;
import com.edu.gestionetudiantsbackend.repository.PaiementLigneRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
@Transactional(readOnly = true)
public class TableauService {

    private final EtudiantRepository etudiants;
    private final PaiementLigneRepository lignes;
    private final SoldeService soldes;

    public TableauService(EtudiantRepository etudiants, PaiementLigneRepository lignes, SoldeService soldes) {
        this.etudiants = etudiants;
        this.lignes = lignes;
        this.soldes = soldes;
    }

    public Page<TableauLigne> tableau(Genre genre, String douar, String q, Pageable pageable) {
        AnneeScolaire annee = soldes.anneeActive();
        Page<Etudiant> page = etudiants.search(genre,
                TuteurService.blankToNull(douar), TuteurService.blankToNull(q), pageable);
        if (page.isEmpty()) {
            return new PageImpl<>(List.of(), pageable, page.getTotalElements());
        }

        List<Long> ids = page.getContent().stream().map(Etudiant::getId).toList();
        List<Long> tuteurIds = page.getContent().stream().map(e -> e.getTuteur().getId()).distinct().toList();

        // Tarifs dyal kol 3a2ila (100dh l'awel, 50dh l'baqi)
        Map<Long, Map<Long, Map<Integer, BigDecimal>>> tarifsParTuteur = new HashMap<>();
        for (Long tid : tuteurIds) {
            tarifsParTuteur.put(tid, soldes.tarifs(tid, annee));
        }

        // (etudiantId → chhr → lignes)
        Map<Long, Map<Integer, List<PaiementLigne>>> parEtudiant = new HashMap<>();
        for (PaiementLigne l : lignes.mensuelsPourEtudiants(ids, annee.getId(), TypePaiement.MENSUEL)) {
            parEtudiant.computeIfAbsent(l.getEtudiant().getId(), k -> new HashMap<>())
                    .computeIfAbsent(l.getMois(), k -> new ArrayList<>())
                    .add(l);
        }
        Set<Long> inscrits = new HashSet<>(
                lignes.tuteursAyantPayeInscription(tuteurIds, annee.getId(), TypePaiement.INSCRIPTION));

        return page.map(e -> {
            Map<Integer, List<PaiementLigne>> parMois = parEtudiant.getOrDefault(e.getId(), Map.of());
            Map<Integer, TableauLigne.Cellule> cellules = new LinkedHashMap<>();
            for (Integer m : MoisScolaire.MOIS) {
                List<PaiementLigne> ls = parMois.getOrDefault(m, List.of());
                BigDecimal montant = ls.stream().map(PaiementLigne::getMontant)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                List<String> bons = ls.stream().map(l -> l.getPaiement().getNumeroBon()).distinct().toList();
                cellules.put(m, new TableauLigne.Cellule(montant, bons));
            }
            Map<Integer, BigDecimal> tarifs = tarifsParTuteur
                    .getOrDefault(e.getTuteur().getId(), Map.of())
                    .getOrDefault(e.getId(), Map.of());
            return new TableauLigne(e.getId(), e.getNom(), e.getPrenom(), e.getGenre(), e.getDouar(),
                    e.getTuteur().getId(), e.getTuteur().getNom(),
                    inscrits.contains(e.getTuteur().getId()),
                    MoisScolaire.moisDus(e), tarifs, cellules);
        });
    }
}
