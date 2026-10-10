package com.edu.gestionetudiantsbackend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Reçu wa7ed: inscription (optionnel) + plusieurs (tilmid, chhr).
 * Numéro de bon kaytsayb automatiquement.
 * Ila "montant" ma tktebch, kayakhod l'taman dyal chhr (wla li bqa mno ila khless chwiya).
 */
public record PaiementRequest(
        @NotNull(message = "Le waliy est obligatoire") Long tuteurId,
        LocalDate datePaiement,
        boolean inscription,
        @Positive BigDecimal montantInscription,
        @Valid List<Mensualite> mensualites) {

    public record Mensualite(
            @NotNull Long etudiantId,
            @NotNull @Min(1) @Max(12) Integer mois,
            @Positive BigDecimal montant) {
    }
}