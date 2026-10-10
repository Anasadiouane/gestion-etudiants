package com.edu.gestionetudiantsbackend.controller;


import com.edu.gestionetudiantsbackend.dto.EtudiantRequest;
import com.edu.gestionetudiantsbackend.model.Etudiant;
import com.edu.gestionetudiantsbackend.model.Genre;
import com.edu.gestionetudiantsbackend.service.EtudiantService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/etudiants")
public class EtudiantController {

    private final EtudiantService service;

    public EtudiantController(EtudiantService service) {
        this.service = service;
    }

    /** GET /api/etudiants?page=0&size=20&genre=FILLE&douar=xxx&q=ahmed */
    @GetMapping
    public Page<Etudiant> list(@RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "20") int size,
                               @RequestParam(required = false) Genre genre,
                               @RequestParam(required = false) String douar,
                               @RequestParam(required = false) String q) {
        return service.search(genre, douar, q,
                PageRequest.of(page, Math.min(size, 100), Sort.by("nom", "prenom")));
    }

    /** Liste des douars (pour le filtre) */
    @GetMapping("/douars")
    public List<String> douars() {
        return service.douars();
    }

    @GetMapping("/{id}")
    public Etudiant get(@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Etudiant create(@Valid @RequestBody EtudiantRequest r) {
        return service.create(r);
    }

    @PutMapping("/{id}")
    public Etudiant update(@PathVariable Long id, @Valid @RequestBody EtudiantRequest r) {
        return service.update(id, r);
    }

    /** Archivage (machi suppression dure) */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void archive(@PathVariable Long id) {
        service.archive(id);
    }
}
