package com.edu.gestionetudiantsbackend.controller;

import com.edu.gestionetudiantsbackend.dto.ImportEtudiantRow;
import com.edu.gestionetudiantsbackend.dto.ImportResultat;
import com.edu.gestionetudiantsbackend.dto.ImportTuteurRow;
import com.edu.gestionetudiantsbackend.service.ImportService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/import")
public class ImportController {

    private final ImportService service;

    public ImportController(ImportService service) {
        this.service = service;
    }

    /** dryRun=true (défaut): vérifie seulement. dryRun=false: enregistre. */
    @PostMapping("/etudiants")
    public ImportResultat etudiants(@RequestBody List<ImportEtudiantRow> lignes,
                                    @RequestParam(defaultValue = "true") boolean dryRun) {
        return service.importerEtudiants(lignes, dryRun);
    }

    @PostMapping("/tuteurs")
    public ImportResultat tuteurs(@RequestBody List<ImportTuteurRow> lignes,
                                  @RequestParam(defaultValue = "true") boolean dryRun) {
        return service.importerTuteurs(lignes, dryRun);
    }
}
