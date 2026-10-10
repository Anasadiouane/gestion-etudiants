package com.edu.gestionetudiantsbackend.service;


import com.edu.gestionetudiantsbackend.model.Etudiant;

import java.util.List;

/** Mawssim: septembre → juin (10 mois) */
public final class MoisScolaire {

    public static final List<Integer> MOIS = List.of(9, 10, 11, 12, 1, 2, 3, 4, 5, 6);

    private MoisScolaire() {
    }

    private static int index(Integer mois) {
        return mois == null ? -1 : MOIS.indexOf(mois);
    }

    /**
     * Les mois li kay7ssb 3lihom tilmid:
     *  - mn chhr dyal l'dkhol (inclus)
     *  - tal chhr dyal l'khrouj (EXCLU: mn dak chhr ma ba9ach kay7ssb)
     */
    public static List<Integer> moisDus(Etudiant e) {
        int start = Math.max(index(e.getMoisDebut()), 0);
        int idxSortie = index(e.getMoisSortie());
        int end = (e.getMoisSortie() == null || idxSortie < 0) ? MOIS.size() : idxSortie;
        if (end <= start) return List.of();
        return MOIS.subList(start, end);
    }
}
