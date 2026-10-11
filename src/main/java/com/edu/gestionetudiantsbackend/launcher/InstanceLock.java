package com.edu.gestionetudiantsbackend.launcher;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Duration;

/**
 * Instance unique: verrou OS sur app.lock (libéré automatiquement à la fermeture/crash).
 * L'instance qui tourne publie son port dans app.port.
 */
public final class InstanceLock {

    private static FileChannel channel;
    private static FileLock lock;

    private InstanceLock() {
    }

    /** true = on est la 1ère instance; false = une autre instance tourne déjà */
    public static boolean acquire(Path dir) {
        try {
            channel = FileChannel.open(dir.resolve("app.lock"),
                    StandardOpenOption.CREATE, StandardOpenOption.WRITE);
            lock = channel.tryLock();
            if (lock == null) {
                channel.close();
                return false;
            }
            Files.deleteIfExists(dir.resolve("app.port")); // port périmé d'un ancien crash
            return true;
        } catch (OverlappingFileLockException e) {
            return false;
        } catch (IOException e) {
            return true; // impossible de verrouiller: on continue quand même
        }
    }

    public static void publishPort(Path dir, int port) throws IOException {
        Files.writeString(dir.resolve("app.port"), String.valueOf(port));
    }

    public static void cleanup(Path dir) {
        try {
            Files.deleteIfExists(dir.resolve("app.port"));
        } catch (IOException ignored) {
        }
    }

    /** Attend que la 1ère instance publie son port (elle est peut-être en cours de démarrage) */
    public static Integer waitForPort(Path dir, Duration timeout) {
        long end = System.nanoTime() + timeout.toNanos();
        Path f = dir.resolve("app.port");
        while (System.nanoTime() < end) {
            try {
                if (Files.exists(f)) {
                    String s = Files.readString(f).trim();
                    if (!s.isEmpty()) return Integer.parseInt(s);
                }
                Thread.sleep(300);
            } catch (IOException | NumberFormatException e) {
                // fichier en cours d'écriture: on réessaie
                try { Thread.sleep(300); } catch (InterruptedException ie) { return null; }
            } catch (InterruptedException e) {
                return null;
            }
        }
        return null;
    }
}
