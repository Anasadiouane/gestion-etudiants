package com.edu.gestionetudiantsbackend.launcher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Quand le serveur est prêt: publie le port, ouvre le navigateur, installe l'icône (tray).
 * En développement (IntelliJ), désactiver avec:  --app.launcher.enabled=false
 */
@Component
@ConditionalOnProperty(name = "app.launcher.enabled", havingValue = "true", matchIfMissing = true)
public class LauncherListener {

    private final ConfigurableApplicationContext ctx;
    private final Environment env;

    public LauncherListener(ConfigurableApplicationContext ctx, Environment env) {
        this.ctx = ctx;
        this.env = env;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onReady() {
        Integer port = env.getProperty("local.server.port", Integer.class);
        if (port == null) return;

        Path dir = Path.of(System.getProperty("app.data.dir"));
        String url = "http://localhost:" + port;

        try {
            InstanceLock.publishPort(dir, port);
        } catch (IOException e) {
            System.err.println("Impossible d'écrire app.port: " + e.getMessage());
        }
        Runtime.getRuntime().addShutdownHook(new Thread(() -> InstanceLock.cleanup(dir)));

        BrowserOpener.open(url);
        TrayController.install(url, this::quitter);
    }

    private void quitter() {
        TrayController.remove();
        int code = SpringApplication.exit(ctx, () -> 0);
        System.exit(code);
    }
}
