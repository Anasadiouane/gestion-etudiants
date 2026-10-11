package com.edu.gestionetudiantsbackend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record TarifsRequest(
        @NotNull @PositiveOrZero BigDecimal fraisInscription,
        @NotNull @PositiveOrZero BigDecimal tarifPremier,
        @NotNull @PositiveOrZero BigDecimal tarifSuivant) {
}
