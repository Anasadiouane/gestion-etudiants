package com.edu.gestionetudiantsbackend.dto;

import com.edu.gestionetudiantsbackend.model.Genre;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

/** tarifPersonnalise = override optionnel; ila null, l'système kay7sb 100dh l'awel w 50dh l'baqi */
public record EtudiantRequest(
        @NotBlank(message = "Le nom est obligatoire") String nom,
        @NotBlank(message = "Le prénom est obligatoire") String prenom,
        @NotNull(message = "Le genre est obligatoire") Genre genre,
        String douar,
        String niveau,
        @NotNull(message = "Le tuteur est obligatoire") Long tuteurId,
        @PositiveOrZero(message = "Le tarif doit être positif") BigDecimal tarifPersonnalise,
        @Min(1) @Max(12) Integer moisDebut,
        @Min(1) @Max(12) Integer moisSortie) {
}
