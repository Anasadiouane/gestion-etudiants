package com.edu.gestionetudiantsbackend.service;

import com.edu.gestionetudiantsbackend.dto.ImportEtudiantRow;
import com.edu.gestionetudiantsbackend.dto.ImportResultat;
import com.edu.gestionetudiantsbackend.dto.ImportResultat.LigneResultat;
import com.edu.gestionetudiantsbackend.dto.ImportTuteurRow;
import com.edu.gestionetudiantsbackend.exception.BadRequestException;
import com.edu.gestionetudiantsbackend.model.Etudiant;
import com.edu.gestionetudiantsbackend.model.Genre;
import com.edu.gestionetudiantsbackend.model.Tuteur;
import com.edu.gestionetudiantsbackend.repository.EtudiantRepository;
import com.edu.gestionetudiantsbackend.repository.TuteurRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.*;

/**
 * Import depuis Excel (le fichier est lu par le frontend, ici on valide et on enregistre).
 * simulation=true: rien n'est écrit en base, on renvoie juste le rapport.
 * Ré-importer le même fichier est sans danger: les lignes déjà présentes sont des DOUBLONS (ignorés).
 */
@Service
public class ImportService {

    private static final int MAX_LIGNES = 5000;

    private final TuteurRepository tuteurs;
    private final EtudiantRepository etudiants;

    public ImportService(TuteurRepository tuteurs, EtudiantRepository etudiants) {
        this.tuteurs = tuteurs;
        this.etudiants = etudiants;
    }

    // ---------- helpers ----------
    static String norm(String s) {
        if (s == null) return "";
        return Normalizer.normalize(s.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("\\s+", " ");
    }

    /** 0600000000, +212600000000, 600000000 → 0600000000 */
    static String tel(String s) {
        if (s == null) return "";
        String d = s.replaceAll("\\D", "");
        if (d.startsWith("212") && d.length() == 12) d = "0" + d.substring(3);
        if (d.length() == 9) d = "0" + d;
        return d;
    }

    static String cleTuteur(String nom, String telephone) {
        return norm(nom) + "#" + tel(telephone);
    }

    static String cleEtudiant(String nom, String prenom, String cleTuteur) {
        return norm(nom) + "|" + norm(prenom) + "|" + cleTuteur;
    }

    private static String requis(String v, String champ) {
        if (v == null || v.isBlank()) throw new BadRequestException(champ + " manquant");
        return v.trim();
    }

    private static String videNull(String v) {
        return (v == null || v.isBlank()) ? null : v.trim();
    }

    private static Genre genre(String v) {
        String g = v == null ? "" : v.trim().toUpperCase(Locale.ROOT);
        if (g.equals("FILLE")) return Genre.FILLE;
        if (g.equals("GARCON")) return Genre.GARCON;
        throw new BadRequestException("Genre invalide (attendu: FILLE ou GARCON)");
    }

    private Map<String, Tuteur> tuteursParCle() {
        Map<String, Tuteur> m = new HashMap<>();
        for (Tuteur t : tuteurs.findAll()) {
            m.putIfAbsent(cleTuteur(t.getNom(), t.getTelephone()), t);
        }
        return m;
    }

    // ---------- étudiants (+ waliy créés automatiquement) ----------
    @Transactional
    public ImportResultat importerEtudiants(List<ImportEtudiantRow> lignes, boolean simulation) {
        if (lignes == null || lignes.isEmpty()) throw new BadRequestException("Fichier vide");
        if (lignes.size() > MAX_LIGNES) throw new BadRequestException("Trop de lignes (max " + MAX_LIGNES + ")");

        Map<String, Tuteur> parCle = tuteursParCle();
        Set<String> existants = new HashSet<>();
        for (Etudiant e : etudiants.findAll()) {
            existants.add(cleEtudiant(e.getNom(), e.getPrenom(),
                    cleTuteur(e.getTuteur().getNom(), e.getTuteur().getTelephone())));
        }
        Set<String> dansFichier = new HashSet<>();

        List<LigneResultat> res = new ArrayList<>();
        int aCreer = 0, doublons = 0, erreurs = 0, waliyCrees = 0;

        for (ImportEtudiantRow r : lignes) {
            try {
                String nom = requis(r.nom(), "Nom");
                String prenom = requis(r.prenom(), "Prénom");
                String waliyNom = requis(r.waliyNom(), "Nom du waliy");
                Genre genre = genre(r.genre());
                String douar = Douars.verifier(r.douar());
                if (r.tarifPersonnalise() != null && r.tarifPersonnalise().compareTo(BigDecimal.ZERO) < 0) {
                    throw new BadRequestException("Tarif négatif");
                }

                String cleW = cleTuteur(waliyNom, r.waliyTelephone());
                String cleE = cleEtudiant(nom, prenom, cleW);
                if (existants.contains(cleE)) {
                    doublons++;
                    res.add(new LigneResultat(r.ligne(), "DOUBLON", "Existe déjà"));
                    continue;
                }
                if (!dansFichier.add(cleE)) {
                    doublons++;
                    res.add(new LigneResultat(r.ligne(), "DOUBLON", "Doublon dans le fichier"));
                    continue;
                }

                Tuteur t = parCle.get(cleW);
                boolean nouveau = (t == null);
                if (nouveau) {
                    t = new Tuteur();
                    t.setNom(waliyNom);
                    t.setTelephone(videNull(r.waliyTelephone()));
                    t.setDouar(douar);
                    if (!simulation) t = tuteurs.save(t);
                    parCle.put(cleW, t);
                    waliyCrees++;
                }

                if (!simulation) {
                    Etudiant e = new Etudiant();
                    e.setNom(nom);
                    e.setPrenom(prenom);
                    e.setGenre(genre);
                    e.setDouar(douar);
                    e.setNiveau(videNull(r.niveau()));
                    e.setTuteur(t);
                    e.setTarifPersonnalise(r.tarifPersonnalise());
                    e.setMoisDebut(9);
                    etudiants.save(e);
                }
                aCreer++;
                res.add(new LigneResultat(r.ligne(), "OK", nouveau ? "Nouveau waliy" : "Waliy existant"));
            } catch (BadRequestException ex) {
                erreurs++;
                res.add(new LigneResultat(r.ligne(), "ERREUR", ex.getMessage()));
            }
        }
        return new ImportResultat(simulation, lignes.size(), aCreer, doublons, erreurs, waliyCrees, res);
    }

    // ---------- parents seulement ----------
    @Transactional
    public ImportResultat importerTuteurs(List<ImportTuteurRow> lignes, boolean simulation) {
        if (lignes == null || lignes.isEmpty()) throw new BadRequestException("Fichier vide");
        if (lignes.size() > MAX_LIGNES) throw new BadRequestException("Trop de lignes (max " + MAX_LIGNES + ")");

        Set<String> existants = new HashSet<>(tuteursParCle().keySet());
        List<LigneResultat> res = new ArrayList<>();
        int aCreer = 0, doublons = 0, erreurs = 0;

        for (ImportTuteurRow r : lignes) {
            try {
                String nom = requis(r.nom(), "Nom");
                String douar = Douars.verifier(r.douar());
                String cle = cleTuteur(nom, r.telephone());
                if (!existants.add(cle)) {
                    doublons++;
                    res.add(new LigneResultat(r.ligne(), "DOUBLON", "Existe déjà"));
                    continue;
                }
                if (!simulation) {
                    Tuteur t = new Tuteur();
                    t.setNom(nom);
                    t.setTelephone(videNull(r.telephone()));
                    t.setDouar(douar);
                    tuteurs.save(t);
                }
                aCreer++;
                res.add(new LigneResultat(r.ligne(), "OK", null));
            } catch (BadRequestException ex) {
                erreurs++;
                res.add(new LigneResultat(r.ligne(), "ERREUR", ex.getMessage()));
            }
        }
        return new ImportResultat(simulation, lignes.size(), aCreer, doublons, erreurs, aCreer, res);
    }
}
