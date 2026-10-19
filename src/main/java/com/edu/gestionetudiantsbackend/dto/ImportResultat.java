package com.edu.gestionetudiantsbackend.dto;

import java.util.List;

/** statut d'une ligne: OK (sera / a été créée), DOUBLON (existe déjà, ignorée), ERREUR */
public record ImportResultat(
        boolean simulation,
        int total,
        int aCreer,
        int doublons,
        int erreurs,
        int waliyCrees,
        List<LigneResultat> lignes) {

    public record LigneResultat(int ligne, String statut, String message) {
    }
}
