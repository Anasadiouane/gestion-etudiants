package com.edu.gestionetudiantsbackend;

import com.edu.gestionetudiantsbackend.launcher.Backups;
import com.edu.gestionetudiantsbackend.launcher.BrowserOpener;
import com.edu.gestionetudiantsbackend.launcher.InstanceLock;
import com.edu.gestionetudiantsbackend.launcher.PortFinder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

@SpringBootApplication
public class GestionEtudiantsBackendApplication {

    private static final int PORT_PREFERE = 8081;

    public static void main(String[] args) throws Exception {
        // Données: %LOCALAPPDATA%\GestionEtudiants (jamais dans Program Files)
        String base = System.getenv("LOCALAPPDATA");
        if (base == null || base.isBlank()) base = System.getProperty("user.home");
        Path dir = Path.of(base, "GestionEtudiants");
        Files.createDirectories(dir);
        System.setProperty("app.data.dir", dir.toString().replace('\\', '/'));

        // Instance unique: si le logiciel tourne déjà, on ouvre juste le navigateur
        if (!InstanceLock.acquire(dir)) {
            Integer port = InstanceLock.waitForPort(dir, Duration.ofSeconds(60));
            if (port != null) {
                System.setProperty("java.awt.headless", "false");
                BrowserOpener.open("http://localhost:" + port);
            }
            System.exit(0);
            return;
        }

        Backups.faire(dir); // copie quotidienne de data.db (30 jours)

        System.setProperty("java.awt.headless", "false"); // nécessaire pour l'icône + navigateur
        System.setProperty("server.port", String.valueOf(PortFinder.choose(PORT_PREFERE)));

        SpringApplication.run(GestionEtudiantsBackendApplication.class, args);
    }
}
