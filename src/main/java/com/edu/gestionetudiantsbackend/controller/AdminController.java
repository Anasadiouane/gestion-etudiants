package com.edu.gestionetudiantsbackend.controller;

import com.edu.gestionetudiantsbackend.dto.PasswordRequest;
import com.edu.gestionetudiantsbackend.exception.BadRequestException;
import com.edu.gestionetudiantsbackend.exception.NotFoundException;
import com.edu.gestionetudiantsbackend.model.Admin;
import com.edu.gestionetudiantsbackend.repository.AdminRepository;
import com.edu.gestionetudiantsbackend.service.BackupService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminRepository admins;
    private final PasswordEncoder encoder;
    private final BackupService backups;

    public AdminController(AdminRepository admins, PasswordEncoder encoder, BackupService backups) {
        this.admins = admins;
        this.encoder = encoder;
        this.backups = backups;
    }

    /** Changer le mot de passe de l'admin connecté (400 si l'ancien est faux, pas 401) */
    @PostMapping("/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changerMotDePasse(@Valid @RequestBody PasswordRequest r, Authentication auth) {
        Admin a = admins.findByUsername(auth.getName())
                .orElseThrow(() -> new NotFoundException("Admin introuvable"));
        if (!encoder.matches(r.ancien(), a.getPasswordHash())) {
            throw new BadRequestException("Mot de passe actuel incorrect.");
        }
        a.setPasswordHash(encoder.encode(r.nouveau()));
        admins.save(a);
    }

    /** Télécharger une copie de la base (data.db) */
    @GetMapping("/backup")
    public ResponseEntity<byte[]> backup() throws IOException {
        byte[] data = backups.snapshot();
        String nom = "sauvegarde_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HHmm")) + ".db";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nom + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(data);
    }
}
