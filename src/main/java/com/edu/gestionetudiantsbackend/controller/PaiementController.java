package com.edu.gestionetudiantsbackend.controller;

import com.edu.gestionetudiantsbackend.dto.*;
import com.edu.gestionetudiantsbackend.model.Genre;
import com.edu.gestionetudiantsbackend.service.PaiementService;
import com.edu.gestionetudiantsbackend.service.SoldeService;
import com.edu.gestionetudiantsbackend.service.TableauService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/paiements")
public class PaiementController {

    private final PaiementService paiements;
    private final SoldeService soldes;
    private final TableauService tableau;

    public PaiementController(PaiementService paiements, SoldeService soldes, TableauService tableau) {
        this.paiements = paiements;
        this.soldes = soldes;
        this.tableau = tableau;
    }

    /** Enregistrer un reçu (numéro de bon + inscription + mois) */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaiementResponse create(@Valid @RequestBody PaiementRequest r) {
        return paiements.create(r);
    }

    @GetMapping
    public Page<PaiementResponse> list(@RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "20") int size,
                                       @RequestParam(required = false) Long tuteurId,
                                       @RequestParam(required = false) String q,
                                       @RequestParam(defaultValue = "date") String tri,
                                       @RequestParam(defaultValue = "desc") String ordre) {
        return paiements.list(tuteurId, q, PageRequest.of(page, Math.min(size, 100), tri(tri, ordre)));
    }

    /** tri = parent | date | bon | total ; ordre = asc | desc */
    private static Sort tri(String tri, String ordre) {
        Sort.Direction d = "asc".equalsIgnoreCase(ordre) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort parDate = Sort.by(Sort.Direction.DESC, "datePaiement", "id");
        return switch (tri) {
            case "parent" -> Sort.by(d, "tuteur.nom").and(parDate);
            case "bon" -> Sort.by(d, "numeroBon");
            case "total" -> Sort.by(d, "montantTotal").and(parDate);
            default -> Sort.by(d, "datePaiement", "id");
        };
    }

    @GetMapping("/{id}")
    public PaiementResponse get(@PathVariable Long id) {
        return paiements.get(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        paiements.delete(id);
    }

    /** Tableau tilmid × chhr (9 → 6) m3a montant + numéros de bon */
    @GetMapping("/tableau")
    public Page<TableauLigne> tableau(@RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "20") int size,
                                      @RequestParam(required = false) Genre genre,
                                      @RequestParam(required = false) String douar,
                                      @RequestParam(required = false) String q) {
        return tableau.tableau(genre, douar, q,
                PageRequest.of(page, Math.min(size, 100), Sort.by("nom", "prenom")));
    }

    /** Awliya2: dû / payé / reste / crédit. impayes=true → ghir li mazal 3ndhom reste */
    @GetMapping("/soldes")
    public Page<SoldeDto> soldes(@RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "20") int size,
                                 @RequestParam(required = false) String q,
                                 @RequestParam(defaultValue = "false") boolean impayes) {
        return soldes.soldes(q, impayes, PageRequest.of(page, Math.min(size, 100)));
    }

    /** Tafsil dyal waliy: chhal 3la kol tilmid w chhor li bqaw */
    @GetMapping("/soldes/{tuteurId}")
    public SoldeDetailDto detail(@PathVariable Long tuteurId) {
        return soldes.detail(tuteurId);
    }
}
