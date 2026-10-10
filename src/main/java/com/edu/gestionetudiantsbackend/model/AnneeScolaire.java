package com.edu.gestionetudiantsbackend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter @Setter
public class AnneeScolaire {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Matalan "2026/2027" */
    @Column(nullable = false, unique = true)
    private String libelle;

    /** 30dh: kay-tkhlless dima, mرة wa7da l'kol waliy f l'mawssim */
    @Column(nullable = false)
    private BigDecimal fraisInscription = new BigDecimal("30");

    /** Taman dyal chhr l'awel tilmid f l'3a2ila (100dh) */
    private BigDecimal tarifPremier = new BigDecimal("100");

    /** Taman dyal chhr l'tlamid l'akhrin f nafs l'3a2ila (50dh) */
    private BigDecimal tarifSuivant = new BigDecimal("50");

    /** Ghir mawssim wa7ed ykon actif */
    private boolean active = true;

    // Fallback l'lignes l9dima li ma 3ndhomch had l'colonnes
    public BigDecimal getTarifPremier() {
        return tarifPremier != null ? tarifPremier : new BigDecimal("100");
    }

    public BigDecimal getTarifSuivant() {
        return tarifSuivant != null ? tarifSuivant : new BigDecimal("50");
    }
}
