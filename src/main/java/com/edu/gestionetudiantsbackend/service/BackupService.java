package com.edu.gestionetudiantsbackend.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
public class BackupService {

    private final JdbcTemplate jdbc;

    public BackupService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * Copie cohérente de la base (VACUUM INTO) pendant que le logiciel tourne.
     * Pas de @Transactional: VACUUM ne peut pas tourner dans une transaction.
     */
    public byte[] snapshot() throws IOException {
        Path dir = Path.of(System.getProperty("app.data.dir", System.getProperty("java.io.tmpdir")));
        Path tmp = dir.resolve("export-" + UUID.randomUUID() + ".db");
        try {
            String chemin = tmp.toString().replace('\\', '/').replace("'", "''");
            jdbc.execute("VACUUM INTO '" + chemin + "'");
            return Files.readAllBytes(tmp);
        } finally {
            Files.deleteIfExists(tmp);
        }
    }
}
