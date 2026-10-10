package com.edu.gestionetudiantsbackend.service;

import com.edu.gestionetudiantsbackend.dto.SoldeDetailDto;
import com.edu.gestionetudiantsbackend.dto.SoldeDto;
import com.edu.gestionetudiantsbackend.exception.ConflictException;
import com.edu.gestionetudiantsbackend.model.*;
import com.edu.gestionetudiantsbackend.repository.AnneeScolaireRepository;
import com.edu.gestionetudiantsbackend.repository.EtudiantRepository;
import com.edu.gestionetudiantsbackend.repository.PaiementLigneRepository;
import com.edu.gestionetudiantsbackend.repository.TuteurRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Service
@Transactional(readOnly = true)
public class SoldeService {

    private final AnneeScolaireRepository annees;
    private final EtudiantRepository etudiants;
    private final TuteurRepository tuteurs;
    private final PaiementLigneRepository lignes;
    private final TuteurService tuteurService;

    public SoldeService(AnneeScolaireRepository annees, EtudiantRepository etudiants,
                        TuteurRepository tuteurs, PaiementLigneRepository lignes,
                        TuteurService tuteurService) {
        this.annees = annees;
        this.etudiants = etudiants;
        this.tuteurs = tuteurs;
        this.lignes = lignes;
        this.tuteurService = tuteurService;
    }

    public AnneeScolaire anneeActive() {
        return annees.findFirstByActiveTrue()
                .orElseThrow(() -> new ConflictException("Aucune année scolaire active."));
    }

    public static String cle(Long etudiantId, Integer mois) {
        return etudiantId + ":" + mois;
    }

    /** Majmou3 li tkhlless l'kol (tilmid, chhr) */
    public Map<String, BigDecimal> payesParCellule(Long tuteurId, Long anneeId) {
        Map<String, BigDecimal> m = new HashMap<>();
        for (PaiementLigne l : lignes.findByTuteurAndAnnee(tuteurId, anneeId)) {
            if (l.getType() == TypePaiement.MENSUEL && l.getEtudiant() != null) {
                m.merge(cle(l.getEtudiant().getId(), l.getMois()), l.getMontant(), BigDecimal::add);
            }
        }
        return m;
    }

    /** Tlamid li kay-ndkhlo f l'7sab dyal had l'mawssim */
    public List<Etudiant> concernes(Long tuteurId, AnneeScolaire a) {
        LocalDate debut = debutAnnee(a);
        return etudiants.findByTuteurId(tuteurId).stream()
                .filter(e -> e.isActif()
                        || (e.getDateSortie() != null && debut != null && !e.getDateSortie().isBefore(debut)))
                .toList();
    }

    /** Taman dû dyal kol tilmid f kol chhr (100dh l'awel, 50dh l'baqi) */
    public Map<Long, Map<Integer, BigDecimal>> tarifs(Long tuteurId, AnneeScolaire a) {
        return TarifService.calculer(concernes(tuteurId, a), a);
    }

    private static LocalDate debutAnnee(AnneeScolaire a) {
        try {
            return LocalDate.of(Integer.parseInt(a.getLibelle().substring(0, 4)), 9, 1);
        } catch (RuntimeException ex) {
            return null;
        }
    }

    public SoldeDto solde(Tuteur t, AnneeScolaire a) {
        List<Etudiant> es = concernes(t.getId(), a);

        BigDecimal du = es.isEmpty() ? BigDecimal.ZERO : a.getFraisInscription();
        du = du.add(TarifService.total(TarifService.calculer(es, a)));

        BigDecimal paye = BigDecimal.ZERO;
        for (PaiementLigne l : lignes.findByTuteurAndAnnee(t.getId(), a.getId())) {
            paye = paye.add(l.getMontant());
        }

        BigDecimal diff = du.subtract(paye);
        BigDecimal reste = diff.max(BigDecimal.ZERO);
        BigDecimal credit = diff.negate().max(BigDecimal.ZERO);
        boolean insc = lignes.existsType(t.getId(), a.getId(), TypePaiement.INSCRIPTION);

        return new SoldeDto(t.getId(), t.getNom(), t.getTelephone(), t.getDouar(),
                es.size(), du, paye, reste, credit, insc, reste.signum() == 0);
    }

    public SoldeDto solde(Long tuteurId) {
        return solde(tuteurService.get(tuteurId), anneeActive());
    }

    public SoldeDetailDto detail(Long tuteurId) {
        Tuteur t = tuteurService.get(tuteurId);
        AnneeScolaire a = anneeActive();
        Map<String, BigDecimal> payes = payesParCellule(tuteurId, a.getId());
        List<Etudiant> es = concernes(tuteurId, a);
        Map<Long, Map<Integer, BigDecimal>> tarifs = TarifService.calculer(es, a);

        List<SoldeDetailDto.EtudiantSolde> liste = new ArrayList<>();
        for (Etudiant e : es) {
            Map<Integer, BigDecimal> tarifsE = tarifs.getOrDefault(e.getId(), Map.of());
            List<Integer> dus = MoisScolaire.moisDus(e);
            List<Integer> impayes = new ArrayList<>();
            BigDecimal reste = BigDecimal.ZERO;
            for (Map.Entry<Integer, BigDecimal> en : tarifsE.entrySet()) {
                BigDecimal p = payes.getOrDefault(cle(e.getId(), en.getKey()), BigDecimal.ZERO);
                BigDecimal manque = en.getValue().subtract(p);
                if (manque.signum() > 0) {
                    impayes.add(en.getKey());
                    reste = reste.add(manque);
                }
            }
            liste.add(new SoldeDetailDto.EtudiantSolde(e.getId(), e.getNom() + " " + e.getPrenom(),
                    tarifsE, dus, impayes, reste));
        }
        return new SoldeDetailDto(solde(t, a), liste);
    }

    /** Liste dyal awliya2 + wad3iyathom. impayesSeulement=true → ghir li 3ndhom reste */
    public Page<SoldeDto> soldes(String q, boolean impayesSeulement, Pageable pageable) {
        AnneeScolaire a = anneeActive();
        List<SoldeDto> all = new ArrayList<>();
        for (Tuteur t : tuteurs.search(TuteurService.blankToNull(q), Pageable.unpaged(Sort.by("nom")))) {
            SoldeDto s = solde(t, a);
            if (s.nbEtudiants() == 0) continue;
            if (impayesSeulement && s.aJour()) continue;
            all.add(s);
        }
        int from = (int) Math.min(pageable.getOffset(), all.size());
        int to = Math.min(from + pageable.getPageSize(), all.size());
        return new PageImpl<>(all.subList(from, to), pageable, all.size());
    }
}
