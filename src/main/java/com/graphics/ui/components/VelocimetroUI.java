package com.graphics.ui.components;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

public class VelocimetroUI implements ComponenteUI {
    private int velocidad = 0;
    private int cx, cy, r;
    
    private boolean medidorRoto = false;
    private float agujaY = 0;
    private float agujaVy = 0;
    private float anguloAgujaRotacion = 0;
    private float anguloRomper = 0;

    public VelocimetroUI(int cx, int cy, int r) {
        this.cx = cx;
        this.cy = cy;
        this.r = r;
    }

    public void reiniciar() {
        medidorRoto = false;
        agujaY = 0;
        agujaVy = 0;
        anguloAgujaRotacion = 0;
        anguloRomper = 0;
    }

    public void setVelocidad(int velocidad) {
        this.velocidad = velocidad;
        float maxNormal = 200.0f;
        float anguloDestino = (float) Math.toRadians(180 - Math.min(maxNormal, velocidad) / maxNormal * 180);
        
        if (velocidad > 200 && !medidorRoto) {
            medidorRoto = true;
            anguloRomper = anguloDestino;
            agujaVy = -5.0f; // Salta hacia arriba
        }
    }

    public boolean isRoto() {
        return medidorRoto;
    }

    @Override
    public void dibujar(Graphics2D g2d) {
        // Fondo y borde del medidor
        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.fillArc(cx - r, cy - r, r * 2, r * 2, 0, 180);
        g2d.setColor(Color.WHITE);
        g2d.drawArc(cx - r, cy - r, r * 2, r * 2, 0, 180);
        
        // Textos del medidor
        g2d.setFont(new Font("SansSerif", Font.BOLD, 12));
        g2d.drawString("0", cx - r - 15, cy + 5);
        g2d.drawString("100", cx - 12, cy - r - 5);
        g2d.drawString("200", cx + r + 5, cy + 5);
        
        // Texto velocidad digital
        g2d.setFont(new Font("SansSerif", Font.BOLD, 14));
        g2d.drawString(velocidad + " km/h", cx - 25, cy + 25);
        
        float maxNormal = 200.0f;
        float anguloDestino = (float) Math.toRadians(180 - Math.min(maxNormal, velocidad) / maxNormal * 180);

        if (medidorRoto) {
            // Físicas de caída para la aguja
            agujaVy += 1.0f; // Gravedad
            agujaY += agujaVy;
            anguloAgujaRotacion += 0.2f;
            
            Graphics2D g2d2 = (Graphics2D) g2d.create();
            g2d2.translate(cx, cy + (int)agujaY);
            g2d2.rotate(anguloAgujaRotacion);
            
            g2d2.setColor(Color.RED);
            g2d2.drawLine(0, 0, (int)(r * 0.9), 0);
            g2d2.fillOval(-3, -3, 6, 6);
            g2d2.dispose();
            
            // Texto de advertencia rojo
            if (System.currentTimeMillis() % 400 < 200) {
                g2d.setColor(Color.RED);
                g2d.setFont(new Font("SansSerif", Font.BOLD, 18));
                g2d.drawString("OVERLOAD", cx - 50, cy - 20);
            }
        } else {
            // Aguja normal
            g2d.setColor(Color.RED);
            int puntaX = cx + (int) (Math.cos(anguloDestino) * r * 0.9);
            int puntaY = cy - (int) (Math.sin(anguloDestino) * r * 0.9);
            g2d.drawLine(cx, cy, puntaX, puntaY);
            g2d.fillOval(cx - 3, cy - 3, 6, 6);
        }
    }
}
