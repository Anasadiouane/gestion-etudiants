package com.edu.gestionetudiantsbackend.dto;

import java.math.BigDecimal;

/** Une ligne du fichier Excel (étudiant + son waliy). genre = FILLE | GARCON, douar = code. */
public record ImportEtudiantRow(
        int ligne,
        String nom,
        String prenom,
        String genre,
        String douar,
        String niveau,
        String waliyNom,
        String waliyTelephone,
        BigDecimal tarifPersonnalise) {
}
