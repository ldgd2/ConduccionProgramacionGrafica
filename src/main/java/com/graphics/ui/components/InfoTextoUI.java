package com.graphics.ui.components;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

public class InfoTextoUI implements ComponenteUI {
    private String zona = "";
    private boolean faros = false;
    private float hora = 0;

    public void actualizarEstado(String zona, boolean faros, float hora) {
        this.zona = zona;
        this.faros = faros;
        this.hora = hora;
    }

    @Override
    public void dibujar(Graphics2D g2d) {
        g2d.setFont(new Font("SansSerif", Font.BOLD, 22));
        
        // Zona (Cyan)
        g2d.setColor(new Color(0, 255, 255));
        g2d.drawString(zona, 20, 35);
        
        // Faros (Amarillo/Gris)
        if (faros) {
            g2d.setColor(Color.YELLOW);
            g2d.drawString("LUCES: ON", 20, 70);
        } else {
            g2d.setColor(Color.GRAY);
            g2d.drawString("LUCES: OFF", 20, 70);
        }
        
        // Hora / Ciclo (Verde Claro)
        int h = (int)(hora * 4); // Fake hora
        g2d.setColor(new Color(150, 255, 150));
        g2d.drawString(String.format("HORA: %02d:00", (h + 6) % 24), 20, 105);
    }
}
