package com.edu.gestionetudiantsbackend.dto;


import com.edu.gestionetudiantsbackend.model.TypePaiement;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record PaiementResponse(
        Long id,
        String numeroBon,
        Long tuteurId,
        String tuteurNom,
        String annee,
        LocalDate datePaiement,
        BigDecimal montantTotal,
        List<Ligne> lignes,
        SoldeDto soldeApres) {

    public record Ligne(TypePaiement type, Long etudiantId, String etudiant,
                        Integer mois, BigDecimal montant) {
    }
}
