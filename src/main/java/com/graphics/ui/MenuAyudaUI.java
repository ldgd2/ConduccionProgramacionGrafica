package com.graphics.ui;

import com.graphics.core.Renderizador;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;

/**
 * Menú superpuesto de controles y ayuda dentro de la ventana.
 * Se muestra al pulsar la tecla P o al pausar el juego.
 */
public class MenuAyudaUI {
    private int texturaId = 0;
    private final int ancho = 560;
    private final int alto = 380;
    private boolean inicializado = false;

    public MenuAyudaUI() {
        texturaId = glGenTextures();
    }

    private void generarTextura() {
        if (inicializado) return;
        inicializado = true;

        BufferedImage img = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Fondo semi-transparente oscuro con borde cian neón
        g2d.setColor(new Color(15, 20, 30, 235));
        g2d.fillRoundRect(0, 0, ancho, alto, 24, 24);

        g2d.setColor(new Color(0, 220, 255, 220));
        g2d.drawRoundRect(2, 2, ancho - 5, alto - 5, 24, 24);
        g2d.drawRoundRect(4, 4, ancho - 9, alto - 9, 22, 22);

        // Título Principal
        g2d.setFont(new Font("SansSerif", Font.BOLD, 22));
        g2d.setColor(new Color(255, 215, 0));
        g2d.drawString("GUÍA DE CONTROLES Y OBJETIVO", 95, 42);

        // Línea divisoria
        g2d.setColor(new Color(0, 220, 255, 120));
        g2d.drawLine(30, 56, ancho - 30, 56);

        // Lista de controles
        g2d.setFont(new Font("SansSerif", Font.BOLD, 15));
        int startY = 85;
        int stepY = 25;

        String[][] controles = {
            {"W / S  |  Flechas", "Acelerar / Retroceder"},
            {"A / D  |  Flechas", "Girar dirección"},
            {"ESPACIO", "Freno de mano"},
            {"SHIFT", "Turbo Nitro"},
            {"C", "Alternar Cámara (Seguimiento / Aérea)"},
            {"N", "Alternar Día / Noche"},
            {"F", "Encender / Apagar Faros"},
            {"M", "Mostrar / Ocultar Minimapa"},
            {"R", "Reiniciar auto y nuevo recorrido"},
            {"P", "Abrir / Cerrar este menú de ayuda"}
        };

        for (int i = 0; i < controles.length; i++) {
            g2d.setColor(new Color(0, 240, 255));
            g2d.drawString(controles[i][0], 40, startY + i * stepY);

            g2d.setColor(new Color(230, 230, 240));
            g2d.drawString("->   " + controles[i][1], 215, startY + i * stepY);
        }

        // Línea divisoria inferior
        g2d.setColor(new Color(0, 220, 255, 120));
        g2d.drawLine(30, startY + controles.length * stepY + 5, ancho - 30, startY + controles.length * stepY + 5);

        // Sección de objetivo
        g2d.setFont(new Font("SansSerif", Font.BOLD, 14));
        g2d.setColor(new Color(255, 215, 80));
        g2d.drawString("OBJETIVO: Sigue la baliza dorada y detén el auto (< 7 km/h)", 40, 355);

        g2d.setFont(new Font("SansSerif", Font.ITALIC, 13));
        g2d.setColor(new Color(180, 255, 180));
        g2d.drawString("Presiona [P] para reanudar la conducción", 155, 375);

        g2d.dispose();

        // Subir a OpenGL
        int[] pixels = new int[ancho * alto];
        img.getRGB(0, 0, ancho, alto, pixels, 0, ancho);

        ByteBuffer buffer = BufferUtils.createByteBuffer(ancho * alto * 4);
        for (int y = 0; y < alto; y++) {
            for (int x = 0; x < ancho; x++) {
                int pixel = pixels[y * ancho + x];
                buffer.put((byte) ((pixel >> 16) & 0xFF));
                buffer.put((byte) ((pixel >> 8) & 0xFF));
                buffer.put((byte) (pixel & 0xFF));
                buffer.put((byte) ((pixel >> 24) & 0xFF));
            }
        }
        buffer.flip();

        glBindTexture(GL_TEXTURE_2D, texturaId);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, ancho, alto, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
    }

    public void dibujar(Renderizador renderizador, int pantallaAncho, int pantallaAlto) {
        generarTextura();
        renderizador.dibujarHUDCentrado(texturaId, pantallaAncho, pantallaAlto, ancho, alto);
    }
}
