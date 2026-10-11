package com.edu.gestionetudiantsbackend.launcher;

import java.awt.*;
import java.awt.image.BufferedImage;

/** Icône dans la zone de notification: "Ouvrir" / "Quitter" (le logiciel n'a pas de fenêtre) */
public final class TrayController {

    private static TrayIcon icon;

    private TrayController() {
    }

    public static void install(String url, Runnable onQuit) {
        try {
            if (GraphicsEnvironment.isHeadless() || !SystemTray.isSupported()) return;

            PopupMenu menu = new PopupMenu();
            MenuItem ouvrir = new MenuItem("Ouvrir  |  فتح");
            ouvrir.addActionListener(e -> BrowserOpener.open(url));
            MenuItem quitter = new MenuItem("Quitter  |  خروج");
            quitter.addActionListener(e -> new Thread(onQuit, "quit").start());
            menu.add(ouvrir);
            menu.addSeparator();
            menu.add(quitter);

            icon = new TrayIcon(creerIcone(), "Association El Kheir - " + url, menu);
            icon.setImageAutoSize(true);
            icon.addActionListener(e -> BrowserOpener.open(url)); // double-clic
            SystemTray.getSystemTray().add(icon);
        } catch (Exception ignored) {
            // pas d'icône: le logiciel fonctionne quand même
        }
    }

    public static void remove() {
        try {
            if (icon != null) SystemTray.getSystemTray().remove(icon);
        } catch (Exception ignored) {
        }
    }

    private static Image creerIcone() {
        int s = 64;
        BufferedImage img = new BufferedImage(s, s, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setColor(new Color(0x0f766e));
        g.fillRoundRect(2, 2, s - 4, s - 4, 20, 20);
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 42));
        FontMetrics fm = g.getFontMetrics();
        String t = "E";
        g.drawString(t, (s - fm.stringWidth(t)) / 2, (s - fm.getHeight()) / 2 + fm.getAscent());
        g.dispose();
        return img;
    }
}
