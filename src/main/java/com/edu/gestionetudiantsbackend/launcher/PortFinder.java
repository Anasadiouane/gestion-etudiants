package com.edu.gestionetudiantsbackend.launcher;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

public final class PortFinder {

    private PortFinder() {
    }

    /** Port préféré s'il est libre, sinon un port libre aléatoire */
    public static int choose(int preferred) {
        if (libre(preferred)) return preferred;
        try (ServerSocket s = new ServerSocket(0, 1, InetAddress.getLoopbackAddress())) {
            return s.getLocalPort();
        } catch (IOException e) {
            return preferred;
        }
    }

    static boolean libre(int port) {
        try (ServerSocket s = new ServerSocket(port, 1, InetAddress.getLoopbackAddress())) {
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
