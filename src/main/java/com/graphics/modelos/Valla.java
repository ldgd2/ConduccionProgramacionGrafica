package com.graphics.modelos;

import com.graphics.core.Pincel;
import com.graphics.conduccion.Particula;
import java.util.List;

public class Valla extends ObjetoDestructible {

    public Valla(float x, float z) {
        super(x, z, 0, 1.4f, 6.0f);
    }

    public void romper(float velAuto, float dirX, float dirZ) {
        romper(velAuto, dirX, dirZ, null);
    }

    @Override
    public void dibujarModelo(Pincel p) {
        // Postes de madera
        p.poste(-1.0f, 0, 0.2f, 1.0f, MADERA);
        p.poste(1.0f, 0, 0.2f, 1.0f, MADERA);
        // Franja de advertencia blanca y roja/madera
        p.caja(0, 0.35f, 0, 2.4f, 0.25f, 0.1f, ROJO);
        p.caja(0, 0.35f, 0.06f, 0.8f, 0.22f, 0.05f, BLANCO);
        p.caja(0, 0.75f, 0, 2.4f, 0.25f, 0.1f, BLANCO);
        p.caja(0, 0.75f, 0.06f, 0.8f, 0.22f, 0.05f, ROJO);
    }

    @Override
    protected void generarEfectoImpacto(List<Particula> particulas) {
        for (int i = 0; i < 20; i++) {
            float pVelX = (float) (Math.random() * 12.0f - 6.0f);
            float pVelY = 3.0f + (float) (Math.random() * 7.0f);
            float pVelZ = (float) (Math.random() * 12.0f - 6.0f);
            float r = (Math.random() > 0.5) ? 0.9f : 0.6f;
            float g = (r > 0.8f) ? 0.1f : 0.4f;
            float b = (r > 0.8f) ? 0.1f : 0.2f;
            particulas.add(new Particula(x, y + 0.5f, z, pVelX, pVelY, pVelZ, r, g, b, 0.35f));
        }
    }
}
