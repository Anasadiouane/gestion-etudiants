package com.edu.gestionetudiantsbackend.controller;

import com.edu.gestionetudiantsbackend.dto.NouvelleAnneeRequest;
import com.edu.gestionetudiantsbackend.dto.TarifsRequest;
import com.edu.gestionetudiantsbackend.model.AnneeScolaire;
import com.edu.gestionetudiantsbackend.service.AnneeService;
import com.edu.gestionetudiantsbackend.service.SoldeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/annees")
public class AnneeController {

    private final SoldeService soldes;
    private final AnneeService service;

    public AnneeController(SoldeService soldes, AnneeService service) {
        this.soldes = soldes;
        this.service = service;
    }

    @GetMapping
    public List<AnneeScolaire> liste() {
        return service.liste();
    }

    @GetMapping("/active")
    public AnneeScolaire active() {
        return soldes.anneeActive();
    }

    @PutMapping("/active")
    public AnneeScolaire modifierActive(@Valid @RequestBody TarifsRequest r) {
        return service.modifierActive(r);
    }

    /** Body optionnel: {} kaykfi (libelle w tarifs kaytحسbo b rassohom) */
    @PostMapping("/nouvelle")
    @ResponseStatus(HttpStatus.CREATED)
    public AnneeScolaire nouvelle(@Valid @RequestBody(required = false) NouvelleAnneeRequest r) {
        return service.nouvelle(r);
    }
}
