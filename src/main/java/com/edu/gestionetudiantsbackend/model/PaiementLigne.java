package com.edu.gestionetudiantsbackend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter @Setter
public class PaiementLigne {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(name = "paiement_id")
    private Paiement paiement;

    @Enumerated(EnumType.STRING) @Column(nullable = false)
    private TypePaiement type;

    /** null f l'inscription (kat-khss waliy, machi tilmid) */
    @ManyToOne
    @JoinColumn(name = "etudiant_id")
    private Etudiant etudiant;

    /** 1..12, null f l'inscription */
    private Integer mois;

    @Column(nullable = false)
    private BigDecimal montant;
}
