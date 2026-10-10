package com.edu.gestionetudiantsbackend.controller;


import com.edu.gestionetudiantsbackend.dto.TuteurRequest;
import com.edu.gestionetudiantsbackend.model.Tuteur;
import com.edu.gestionetudiantsbackend.service.TuteurService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tuteurs")
public class TuteurController {

    private final TuteurService service;

    public TuteurController(TuteurService service) {
        this.service = service;
    }

    @GetMapping
    public Page<Tuteur> list(@RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "20") int size,
                             @RequestParam(required = false) String q) {
        return service.search(q, PageRequest.of(page, Math.min(size, 100), Sort.by("nom")));
    }

    @GetMapping("/{id}")
    public Tuteur get(@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Tuteur create(@Valid @RequestBody TuteurRequest r) {
        return service.create(r);
    }

    @PutMapping("/{id}")
    public Tuteur update(@PathVariable Long id, @Valid @RequestBody TuteurRequest r) {
        return service.update(id, r);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
