package com.edu.gestionetudiantsbackend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Reçu: numéro de bon + tarikh + total. Tafsil f PaiementLigne. */
@Entity
@Getter @Setter
public class Paiement {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String numeroBon;

    @ManyToOne(optional = false)
    @JoinColumn(name = "tuteur_id")
    private Tuteur tuteur;

    @ManyToOne(optional = false)
    @JoinColumn(name = "annee_id")
    private AnneeScolaire annee;

    @Column(nullable = false)
    private LocalDate datePaiement = LocalDate.now();

    @Column(nullable = false)
    private BigDecimal montantTotal = BigDecimal.ZERO;

    @OneToMany(mappedBy = "paiement", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PaiementLigne> lignes = new ArrayList<>();

    public void addLigne(PaiementLigne l) {
        l.setPaiement(this);
        lignes.add(l);
    }
}
