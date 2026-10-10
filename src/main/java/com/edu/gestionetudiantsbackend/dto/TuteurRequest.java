package com.edu.gestionetudiantsbackend.dto;

import jakarta.validation.constraints.NotBlank;

public record TuteurRequest(
        @NotBlank(message = "Le nom est obligatoire") String nom,
        String telephone,
        String douar) {
}
