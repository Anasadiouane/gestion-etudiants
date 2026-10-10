package com.edu.gestionetudiantsbackend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

/** Compteur persistant (numéro de bon automatique) */
@Entity
@Getter @Setter
public class Compteur {
    @Id
    private String nom;
    private long valeur;
}
