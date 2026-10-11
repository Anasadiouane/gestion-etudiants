package com.edu.gestionetudiantsbackend.service;

import com.edu.gestionetudiantsbackend.exception.BadRequestException;

import java.util.List;

/**
 * Les douars autorisés (code stocké en base). Les noms FR/AR sont affichés par le frontend:
 *  LAMNIZLA = Lamnizla / لمنيزلة        TALBORT = Talbort / تلبورت
 *  TALAINT  = Talaïnt / تالعينت          TAMDA   = Tamda / تامدا
 *  IGHZER_AGHANIME = Ighzer Aghanime / إغزر أغانيم
 */
public final class Douars {

    public static final List<String> CODES =
            List.of("LAMNIZLA", "TALBORT", "TALAINT", "TAMDA", "IGHZER_AGHANIME");

    private Douars() {
    }

    /** null/vide → null; code inconnu → 400 */
    public static String verifier(String douar) {
        if (douar == null || douar.isBlank()) return null;
        String d = douar.trim();
        if (!CODES.contains(d)) {
            throw new BadRequestException("Douar invalide: " + d + " (valeurs permises: " + CODES + ")");
        }
        return d;
    }
}
