package com.graphics.ui.components;

import java.awt.Color;
import java.awt.Graphics2D;

public class BarraVidaUI implements ComponenteUI {
    private float vida = 100;
    private int x, y;

    public BarraVidaUI(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setVida(float vida) {
        this.vida = vida;
    }

    @Override
    public void dibujar(Graphics2D g2d) {
        g2d.setColor(Color.WHITE);
        g2d.drawRect(x, y, 250, 15);
        if (vida > 60) g2d.setColor(Color.GREEN);
        else if (vida > 30) g2d.setColor(Color.ORANGE);
        else g2d.setColor(Color.RED);
        g2d.fillRect(x + 1, y + 1, (int)(249 * Math.max(0, vida) / 100.0f), 14);
    }
}
