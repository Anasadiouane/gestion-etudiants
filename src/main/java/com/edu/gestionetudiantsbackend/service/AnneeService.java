package com.edu.gestionetudiantsbackend.service;

import com.edu.gestionetudiantsbackend.dto.NouvelleAnneeRequest;
import com.edu.gestionetudiantsbackend.exception.BadRequestException;
import com.edu.gestionetudiantsbackend.exception.ConflictException;
import com.edu.gestionetudiantsbackend.model.AnneeScolaire;
import com.edu.gestionetudiantsbackend.model.Etudiant;
import com.edu.gestionetudiantsbackend.repository.AnneeScolaireRepository;
import com.edu.gestionetudiantsbackend.repository.EtudiantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class AnneeService {

    private final AnneeScolaireRepository annees;
    private final EtudiantRepository etudiants;

    public AnneeService(AnneeScolaireRepository annees, EtudiantRepository etudiants) {
        this.annees = annees;
        this.etudiants = etudiants;
    }

    @Transactional(readOnly = true)
    public List<AnneeScolaire> liste() {
        return annees.findAllByOrderByIdDesc();
    }

    /**
     * Nouveau mawssim:
     *  - l'mawssim l9dim kayوlli inactif (l'historique dyalo ybqa)
     *  - les tlamid actifs kay-bqaw w kay-bdaw mn septembre (mois_debut=9, bla sortie)
     *  - les tlamid archivés kay-bqaw archivés
     *  - les tarifs kaybqaw kif kanou ila ma t3awdoch
     */
    public AnneeScolaire nouvelle(NouvelleAnneeRequest r) {
        AnneeScolaire ancienne = annees.findFirstByActiveTrue().orElse(null);

        String libelle = (r != null && r.libelle() != null && !r.libelle().isBlank())
                ? r.libelle().trim() : prochainLibelle(ancienne);
        if (annees.existsByLibelle(libelle)) {
            throw new ConflictException("Le mawssim " + libelle + " existe déjà.");
        }

        AnneeScolaire n = new AnneeScolaire();
        n.setLibelle(libelle);
        n.setFraisInscription(choisir(r == null ? null : r.fraisInscription(),
                ancienne == null ? null : ancienne.getFraisInscription(), new BigDecimal("30")));
        n.setTarifPremier(choisir(r == null ? null : r.tarifPremier(),
                ancienne == null ? null : ancienne.getTarifPremier(), new BigDecimal("100")));
        n.setTarifSuivant(choisir(r == null ? null : r.tarifSuivant(),
                ancienne == null ? null : ancienne.getTarifSuivant(), new BigDecimal("50")));
        n.setActive(true);

        if (ancienne != null) {
            ancienne.setActive(false);
            annees.save(ancienne);
        }
        annees.save(n);

        List<Etudiant> actifs = etudiants.findByActifTrue();
        for (Etudiant e : actifs) {
            e.setMoisDebut(9);
            e.setMoisSortie(null);
            e.setDateSortie(null);
        }
        etudiants.saveAll(actifs);
        return n;
    }

    private static BigDecimal choisir(BigDecimal demande, BigDecimal ancien, BigDecimal defaut) {
        if (demande != null) return demande;
        return ancien != null ? ancien : defaut;
    }

    private static String prochainLibelle(AnneeScolaire ancienne) {
        if (ancienne == null) {
            throw new BadRequestException("Donne le libellé du mawssim (ex: 2026/2027).");
        }
        try {
            String[] parts = ancienne.getLibelle().split("/");
            int a = Integer.parseInt(parts[0].trim()) + 1;
            int b = Integer.parseInt(parts[1].trim()) + 1;
            return a + "/" + b;
        } catch (RuntimeException ex) {
            throw new BadRequestException("Impossible de déduire le prochain libellé: donne-le manuellement.");
        }
    }
}
