package com.graphics.ui.components;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

public class InfoTextoUI implements ComponenteUI {
    private String zona = "";
    private boolean faros = false;
    private float hora = 0;
    private String estadoEntregas = "";
    private boolean todasEntregas = false;

    public void actualizarEstado(String zona, boolean faros, float hora, String estadoEntregas, boolean todasEntregas) {
        this.zona = zona;
        this.faros = faros;
        this.hora = hora;
        this.estadoEntregas = estadoEntregas;
        this.todasEntregas = todasEntregas;
    }

    @Override
    public void dibujar(Graphics2D g2d) {
        // Zona (Cyan brillante)
        g2d.setFont(new Font("SansSerif", Font.BOLD, 18));
        g2d.setColor(new Color(0, 240, 255));
        g2d.drawString(zona, 16, 28);
        
        // Faros y Hora en fuentes limpias
        g2d.setFont(new Font("SansSerif", Font.BOLD, 15));
        if (faros) {
            g2d.setColor(new Color(255, 230, 80));
            g2d.drawString("LUCES: ENCENDIDAS", 16, 52);
        } else {
            g2d.setColor(new Color(160, 160, 170));
            g2d.drawString("LUCES: APAGADAS", 16, 52);
        }
        
        // Hora / Ciclo (Verde Neón)
        int h = (int)(hora * 4); 
        g2d.setColor(new Color(130, 255, 170));
        g2d.drawString(String.format("HORA: %02d:00", (h + 6) % 24), 16, 74);

        // Estado de Entregas (Misiones)
        g2d.setFont(new Font("SansSerif", Font.BOLD, 15));
        if (todasEntregas) {
            g2d.setColor(new Color(80, 255, 120));
            g2d.drawString("¡MISIÓN COMPLETADA!", 16, 96);
        } else {
            g2d.setColor(new Color(255, 205, 50));
            g2d.drawString(estadoEntregas, 16, 96);
        }

        // Atajo de ayuda
        g2d.setFont(new Font("SansSerif", Font.PLAIN, 12));
        g2d.setColor(new Color(200, 200, 220, 190));
        g2d.drawString("[P] Guía de Controles", 16, 116);
    }
}
