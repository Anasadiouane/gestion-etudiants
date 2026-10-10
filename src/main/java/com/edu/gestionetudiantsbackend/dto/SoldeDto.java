package com.edu.gestionetudiantsbackend.dto;

import java.math.BigDecimal;

/** Wad3iya dyal waliy f l'mawssim: dû / payé / reste / crédit */
public record SoldeDto(
        Long tuteurId,
        String tuteurNom,
        String telephone,
        String douar,
        int nbEtudiants,
        BigDecimal du,
        BigDecimal paye,
        BigDecimal reste,
        BigDecimal credit,
        boolean inscriptionPayee,
        boolean aJour) {
}
