package com.edu.gestionetudiantsbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.nio.file.Files;
import java.nio.file.Path;

@SpringBootApplication
public class GestionEtudiantsBackendApplication {

    public static void main(String[] args) throws Exception {
        // Base de données f %LOCALAPPDATA%\GestionEtudiants
        String base = System.getenv("LOCALAPPDATA");
        if (base == null || base.isBlank()) base = System.getProperty("user.home");
        Path dir = Path.of(base, "GestionEtudiants");
        Files.createDirectories(dir);
        System.setProperty("app.data.dir", dir.toString().replace('\\', '/'));

        SpringApplication.run(GestionEtudiantsBackendApplication.class, args);
    }
}