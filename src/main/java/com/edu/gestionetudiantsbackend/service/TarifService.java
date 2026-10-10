package com.edu.gestionetudiantsbackend.service;

import com.edu.gestionetudiantsbackend.model.AnneeScolaire;
import com.edu.gestionetudiantsbackend.model.Etudiant;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Règle dyal taman: f kol chhr, f kol 3a2ila (waliy), l'awel tilmid (7asb tartib l'tسjil)
 * kaykhless tarifPremier (100dh), w l'baqi tarifSuivant (50dh).
 * L'7sab kaytdar chhr b chhr: ila l'awel tilmid khrej, li morah kayw93 100dh mn dak chhr.
 */
public final class TarifService {

    private TarifService() {
    }

    /** etudiantId → (chhr → taman dû) */
    public static Map<Long, Map<Integer, BigDecimal>> calculer(List<Etudiant> famille, AnneeScolaire a) {
        List<Etudiant> tries = famille.stream()
                .sorted(Comparator.comparing(Etudiant::getId))
                .toList();

        Map<Long, Map<Integer, BigDecimal>> res = new LinkedHashMap<>();
        for (Etudiant e : tries) {
            res.put(e.getId(), new LinkedHashMap<>());
        }
        for (Integer m : MoisScolaire.MOIS) {
            boolean premier = true;
            for (Etudiant e : tries) {
                if (!MoisScolaire.moisDus(e).contains(m)) continue;
                res.get(e.getId()).put(m, premier ? a.getTarifPremier() : a.getTarifSuivant());
                premier = false;
            }
        }
        return res;
    }

    public static BigDecimal total(Map<Long, Map<Integer, BigDecimal>> tarifs) {
        BigDecimal t = BigDecimal.ZERO;
        for (Map<Integer, BigDecimal> m : tarifs.values()) {
            for (BigDecimal v : m.values()) {
                t = t.add(v);
            }
        }
        return t;
    }
}
