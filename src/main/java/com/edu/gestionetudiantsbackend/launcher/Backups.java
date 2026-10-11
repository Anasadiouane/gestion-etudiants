package com.edu.gestionetudiantsbackend.launcher;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;

/** Sauvegarde automatique de data.db: 1 copie par jour (au démarrage), on garde les 30 dernières */
public final class Backups {

    private static final int GARDER = 30;

    private Backups() {
    }

    public static void faire(Path dir) {
        try {
            Path db = dir.resolve("data.db");
            if (!Files.exists(db)) return;
            Path dossier = dir.resolve("backups");
            Files.createDirectories(dossier);

            String jour = LocalDate.now().toString();
            try (Stream<Path> s = Files.list(dossier)) {
                if (s.anyMatch(p -> p.getFileName().toString().startsWith("data_" + jour))) return;
            }
            String nom = "data_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HHmm")) + ".db";
            Files.copy(db, dossier.resolve(nom), StandardCopyOption.REPLACE_EXISTING);

            List<Path> tous;
            try (Stream<Path> s = Files.list(dossier)) {
                tous = s.filter(p -> p.getFileName().toString().startsWith("data_"))
                        .sorted().toList();
            }
            for (int i = 0; i < tous.size() - GARDER; i++) {
                Files.deleteIfExists(tous.get(i));
            }
        } catch (IOException ignored) {
            // la sauvegarde ne doit jamais empêcher le démarrage
        }
    }
}
