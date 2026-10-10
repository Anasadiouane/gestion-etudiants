package com.edu.gestionetudiantsbackend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter @Setter
public class Etudiant {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank @Column(nullable = false)
    private String nom;

    @NotBlank @Column(nullable = false)
    private String prenom;

    @NotNull @Enumerated(EnumType.STRING) @Column(nullable = false)
    private Genre genre;

    /** Adresse (douar) */
    private String douar;

    /** Mostawa dirassi */
    private String niveau;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "tuteur_id")
    private Tuteur tuteur;

    /** Chhr dyal l'dkhol (1..12). Default 9 (septembre) */
    private Integer moisDebut = 9;

    /** Chhr dyal l'khrouj (1..12). null = mazal kaykml */
    private Integer moisSortie;

    /** Tarikh dyal l'archivage (kay-3awn f l'7sab dyal les mawassim) */
    private LocalDate dateSortie;

    /** false = archivé (ma kan7aydoush bach l'historique ybqa) */
    private boolean actif = true;
}
