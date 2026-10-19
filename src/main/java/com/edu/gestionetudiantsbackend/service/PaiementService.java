package com.edu.gestionetudiantsbackend.service;

import com.edu.gestionetudiantsbackend.dto.PaiementRequest;
import com.edu.gestionetudiantsbackend.dto.PaiementResponse;
import com.edu.gestionetudiantsbackend.exception.BadRequestException;
import com.edu.gestionetudiantsbackend.exception.ConflictException;
import com.edu.gestionetudiantsbackend.exception.NotFoundException;
import com.edu.gestionetudiantsbackend.model.*;
import com.edu.gestionetudiantsbackend.repository.CompteurRepository;
import com.edu.gestionetudiantsbackend.repository.EtudiantRepository;
import com.edu.gestionetudiantsbackend.repository.PaiementLigneRepository;
import com.edu.gestionetudiantsbackend.repository.PaiementRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class PaiementService {

    private final PaiementRepository paiements;
    private final PaiementLigneRepository lignes;
    private final EtudiantRepository etudiants;
    private final CompteurRepository compteurs;
    private final TuteurService tuteurs;
    private final SoldeService soldes;

    public PaiementService(PaiementRepository paiements, PaiementLigneRepository lignes,
                           EtudiantRepository etudiants, CompteurRepository compteurs,
                           TuteurService tuteurs, SoldeService soldes) {
        this.paiements = paiements;
        this.lignes = lignes;
        this.etudiants = etudiants;
        this.compteurs = compteurs;
        this.tuteurs = tuteurs;
        this.soldes = soldes;
    }

    /** Numéro de bon automatique: B-00001, B-00002... (l'admin kaykteb-o f l'wrqa) */
    private String prochainBon() {
        Compteur c = compteurs.findById("bon").orElseGet(() -> {
            Compteur n = new Compteur();
            n.setNom("bon");
            n.setValeur(0);
            return n;
        });
        c.setValeur(c.getValeur() + 1);
        compteurs.save(c);
        return String.format("B-%05d", c.getValeur());
    }

    public PaiementResponse create(PaiementRequest r) {
        AnneeScolaire annee = soldes.anneeActive();
        Tuteur t = tuteurs.get(r.tuteurId());

        boolean aMensuel = r.mensualites() != null && !r.mensualites().isEmpty();
        if (!r.inscription() && !aMensuel) {
            throw new BadRequestException("Le paiement est vide: choisis l'inscription et/ou des mois.");
        }

        Paiement p = new Paiement();
        p.setNumeroBon(prochainBon());
        p.setTuteur(t);
        p.setAnnee(annee);
        p.setDatePaiement(r.datePaiement() != null ? r.datePaiement() : LocalDate.now());

        BigDecimal total = BigDecimal.ZERO;

        // 1) Inscription (30dh, dima, mرة wa7da l'kol waliy)
        if (r.inscription()) {
            if (lignes.existsType(t.getId(), annee.getId(), TypePaiement.INSCRIPTION)) {
                throw new ConflictException("L'inscription de ce waliy est déjà payée.");
            }
            PaiementLigne l = new PaiementLigne();
            l.setType(TypePaiement.INSCRIPTION);
            l.setMontant(r.montantInscription() != null ? r.montantInscription() : annee.getFraisInscription());
            p.addLigne(l);
            total = total.add(l.getMontant());
        }

        // 2) Mensualités
        if (aMensuel) {
            Map<Long, Map<Integer, BigDecimal>> tarifs = soldes.tarifs(t.getId(), annee);
            Map<String, BigDecimal> payes = new HashMap<>(soldes.payesParCellule(t.getId(), annee.getId()));

            for (PaiementRequest.Mensualite m : r.mensualites()) {
                Etudiant e = etudiants.findById(m.etudiantId())
                        .orElseThrow(() -> new NotFoundException("Étudiant introuvable: " + m.etudiantId()));
                if (!e.getTuteur().getId().equals(t.getId())) {
                    throw new ConflictException(e.getNom() + " " + e.getPrenom() + " n'appartient pas à ce waliy.");
                }
                BigDecimal tarifMois = tarifs.getOrDefault(e.getId(), Map.of()).get(m.mois());
                if (tarifMois == null) {
                    throw new ConflictException("Le mois " + m.mois() + " n'est pas dû pour "
                            + e.getNom() + " " + e.getPrenom() + ".");
                }
                String cle = SoldeService.cle(e.getId(), m.mois());
                BigDecimal deja = payes.getOrDefault(cle, BigDecimal.ZERO);
                BigDecimal montant = m.montant();
                if (montant == null) {
                    montant = tarifMois.subtract(deja);   // complément
                    if (montant.signum() <= 0) {
                        throw new ConflictException("Mois " + m.mois() + " déjà payé pour "
                                + e.getNom() + " " + e.getPrenom() + ".");
                    }
                }
                payes.put(cle, deja.add(montant));

                PaiementLigne l = new PaiementLigne();
                l.setType(TypePaiement.MENSUEL);
                l.setEtudiant(e);
                l.setMois(m.mois());
                l.setMontant(montant);
                p.addLigne(l);
                total = total.add(montant);
            }
        }

        p.setMontantTotal(total);
        return toResponse(paiements.save(p), true);
    }

    @Transactional(readOnly = true)
    public PaiementResponse get(Long id) {
        return toResponse(find(id), true);
    }

    @Transactional(readOnly = true)
    public Page<PaiementResponse> list(Long tuteurId, String q, Pageable pageable) {
        Long anneeId = soldes.anneeActive().getId();
        Page<Paiement> page = paiements.rechercher(anneeId, tuteurId, TuteurService.blankToNull(q), pageable);
        return page.map(p -> toResponse(p, false));
    }

    /** Tصحيح ghalat: kaymse7 l'reçu b kol lignes dyalo */
    public void delete(Long id) {
        paiements.delete(find(id));
    }

    private Paiement find(Long id) {
        return paiements.findById(id)
                .orElseThrow(() -> new NotFoundException("Paiement introuvable: " + id));
    }

    private PaiementResponse toResponse(Paiement p, boolean avecSolde) {
        List<PaiementResponse.Ligne> ls = p.getLignes().stream()
                .map(l -> new PaiementResponse.Ligne(
                        l.getType(),
                        l.getEtudiant() != null ? l.getEtudiant().getId() : null,
                        l.getEtudiant() != null ? l.getEtudiant().getNom() + " " + l.getEtudiant().getPrenom() : null,
                        l.getMois(),
                        l.getMontant()))
                .toList();
        return new PaiementResponse(p.getId(), p.getNumeroBon(), p.getTuteur().getId(), p.getTuteur().getNom(),
                p.getAnnee().getLibelle(), p.getDatePaiement(), p.getMontantTotal(), ls,
                avecSolde ? soldes.solde(p.getTuteur(), p.getAnnee()) : null);
    }
}
