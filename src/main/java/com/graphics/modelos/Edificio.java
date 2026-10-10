package com.graphics.modelos;

import com.graphics.core.Color;
import com.graphics.core.Pincel;

public class Edificio extends Objeto {
    private float altura;
    private float rojo, azul;
    private boolean esDowntown;
    private int seed;

    public Edificio(float x, float z, float altura, float rojo, float azul) {
        super(x, z);
        this.altura = altura;
        this.rojo = rojo;
        this.azul = azul;
        this.esDowntown = altura >= 12f;
        this.seed = (int) (Math.abs(x * 37 + z * 13) % 100);
    }

    @Override
    public void dibujar(Pincel p) {
        // Acera
        p.acera(0, 0, 10, 10);
        
        // Fachada principal del edificio
        Color colorFachada = Color.rgb(rojo, 0.40f, azul);
        p.caja(0, altura / 2 + 0.3f, 0, 7, altura, 7, colorFachada);
        
        // Ventanas y detalles con resplandor nocturno
        p.conBrilloDeNoche(() -> {
            if (esDowntown) {
                // Cyberpunk Neon Lines y grandes cristaleras
                p.caja(0, altura / 2 + 0.3f, 3.55f, 0.3f, altura * 0.8f, 0.1f, Color.NEON_CYAN);
                p.caja(0, altura / 2 + 0.3f, -3.55f, 0.3f, altura * 0.8f, 0.1f, Color.NEON_CYAN);
                
                for (int i = 0; i < altura / 2.5f; i++) {
                    if ((seed + i) % 3 != 0) {
                        float altV = i * 2.5f + 2.0f;
                        p.caja(3.55f, altV, 0, 0.1f, 0.6f, 5f, Color.VIDRIO_NOCHE);
                        p.caja(-3.55f, altV, 0, 0.1f, 0.6f, 5f, Color.VIDRIO_NOCHE);
                    }
                }
            } else {
                // Residencial cálido - ventanas esparcidas
                Color luzVentana = Color.LUZ_CALIDA;
                for (int i = 0; i < altura / 1.5f; i++) {
                    for (int j = -1; j <= 1; j++) {
                        if ((seed + i * 3 + j) % 2 == 0) {
                            float altV = i * 1.5f + 1.2f;
                            float offsetX = j * 2.0f;
                            p.caja(offsetX, altV, 3.55f, 0.8f, 0.8f, 0.1f, luzVentana);
                            p.caja(offsetX, altV, -3.55f, 0.8f, 0.8f, 0.1f, luzVentana);
                            p.caja(3.55f, altV, offsetX, 0.1f, 0.8f, 0.8f, luzVentana);
                            p.caja(-3.55f, altV, offsetX, 0.1f, 0.8f, 0.8f, luzVentana);
                        }
                    }
                }
            }
        });
        
        // Tejado / Cornisa oscura
        p.caja(0, altura + 0.45f, 0, 7.3f, 0.3f, 7.3f, Color.rgb(0.20f, 0.26f, 0.32f));
    }

    @Override
    public boolean esColisionable() {
        return true;
    }
}

