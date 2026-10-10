package com.edu.gestionetudiantsbackend.dto;

import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

/** Kolchi optionnel: libelle kaytحسb b rassou (2027/2028), w les tarifs kaybqaw kif kanou */
public record NouvelleAnneeRequest(
        String libelle,
        @PositiveOrZero BigDecimal fraisInscription,
        @PositiveOrZero BigDecimal tarifPremier,
        @PositiveOrZero BigDecimal tarifSuivant) {
}