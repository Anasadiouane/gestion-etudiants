package com.edu.gestionetudiantsbackend.dto;

import com.edu.gestionetudiantsbackend.model.Genre;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/** Ligne dyal tableau: tilmid × chhr (9,10,11,12,1,...,6) */
public record TableauLigne(
        Long etudiantId,
        String nom,
        String prenom,
        Genre genre,
        String douar,
        Long tuteurId,
        String tuteurNom,
        boolean inscriptionPayee,
        List<Integer> moisDus,
        Map<Integer, BigDecimal> tarifs,
        Map<Integer, Cellule> mois) {

    /** montant mkhlless f had chhr + numéros de bon */
    public record Cellule(BigDecimal montant, List<String> bons) {
    }
}
