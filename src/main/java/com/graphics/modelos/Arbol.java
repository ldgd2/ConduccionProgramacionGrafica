package com.graphics.modelos;

import com.graphics.conduccion.Particula;
import com.graphics.core.Color;
import com.graphics.core.Pincel;
import java.util.List;


public class Arbol extends ObjetoDestructible {
    private final float escala;

    public Arbol(float x, float z) {
        this(x, z, 1.0f);
    }

    public Arbol(float x, float z, float escala) {
        super(x, z, (float) (Math.random() * Math.PI * 2), 1.25f * escala, 12.0f);
        this.escala = escala;
    }

    public float getEscala() {
        return escala;
    }

    @Override
    public void dibujarModelo(Pincel local) {
        float s = escala;
        // 1. Tronco firme de madera
        local.poste(0, 0, 0, 0.45f * s, 2.0f * s, Color.TRONCO);
        
        // 2. Copa inferior frondosa (verde oscuro)
        local.caja(0, 2.6f * s, 0, 2.8f * s, 1.6f * s, 2.8f * s, Color.HOJAS);
        
        // 3. Copa intermedia (verde medio con ligero desfase angular)
        local.caja(0, 3.8f * s, 0, 2.2f * s, 1.4f * s, 2.2f * s, Color.HOJAS_CLARAS, (float) Math.toRadians(45));
        
        // 4. Copa superior / cúspide
        local.caja(0, 4.8f * s, 0, 1.5f * s, 1.2f * s, 1.5f * s, Color.HOJAS);
        local.caja(0, 5.6f * s, 0, 0.8f * s, 0.8f * s, 0.8f * s, Color.HOJAS_CLARAS);
    }

    @Override
    protected void generarEfectoImpacto(List<Particula> particulas) {
        // Explosión densa de hojas verdes y astillas de tronco
        for (int i = 0; i < 40; i++) {
            float pVelX = (float) (Math.random() * 16.0f - 8.0f);
            float pVelY = 2.0f + (float) (Math.random() * 8.0f);
            float pVelZ = (float) (Math.random() * 16.0f - 8.0f);
            
            float r, g, b;
            float roll = (float) Math.random();
            if (roll < 0.45f) {
                // Hojas verdes frondosas
                r = 0.12f;
                g = 0.58f + (float) (Math.random() * 0.25f);
                b = 0.15f;
            } else if (roll < 0.75f) {
                // Hojas verde claro / brotes
                r = 0.25f;
                g = 0.75f + (float) (Math.random() * 0.20f);
                b = 0.20f;
            } else {
                // Astillas de madera / tronco
                r = 0.45f;
                g = 0.28f;
                b = 0.12f;
            }
            float tam = 0.35f + (float) (Math.random() * 0.40f);
            particulas.add(new Particula(x, y + 2.0f * escala, z, pVelX, pVelY, pVelZ, r, g, b, tam));
        }
    }
}
