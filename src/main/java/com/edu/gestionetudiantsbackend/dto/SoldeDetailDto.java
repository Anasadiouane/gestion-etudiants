package com.edu.gestionetudiantsbackend.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record SoldeDetailDto(SoldeDto solde, List<EtudiantSolde> etudiants) {

    /** tarifs = taman dyal kol chhr dû; moisImpayes = mois li mazal ma tkhllsoch kamlin */
    public record EtudiantSolde(Long etudiantId, String nomComplet, Map<Integer, BigDecimal> tarifs,
                                List<Integer> moisDus, List<Integer> moisImpayes, BigDecimal reste) {
    }
}
