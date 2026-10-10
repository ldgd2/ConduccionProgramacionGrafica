package com.graphics.modelos;

import com.graphics.conduccion.Particula;
import com.graphics.core.Color;
import com.graphics.core.Pincel;
import java.util.List;

/**
 * Banco urbano de madera y hierro fundido, ubicado en aceras, plazas y parques.
 * Al ser chocado por el vehículo, se quiebra y sale volando en pedazos,
 * quitando una pequeña porción de vida al auto.
 */
public class Banca extends ObjetoDestructible {

    public Banca(float x, float z) {
        this(x, z, 0);
    }

    public Banca(float x, float z, float rotacion) {
        super(x, z, rotacion, 1.1f, 6.0f); // 6 puntos de daño
    }

    @Override
    public void dibujarModelo(Pincel p) {
        Color madera = Color.MADERA;
        Color metal = Color.METAL_OSCURO;

        // Patas de hierro fundido
        p.caja(-0.85f, 0.25f, 0, 0.12f, 0.5f, 0.65f, metal);
        p.caja(0.85f, 0.25f, 0, 0.12f, 0.5f, 0.65f, metal);

        // Apoyabrazos
        p.caja(-0.85f, 0.60f, 0, 0.10f, 0.25f, 0.55f, metal);
        p.caja(0.85f, 0.60f, 0, 0.10f, 0.25f, 0.55f, metal);

        // Listones de madera del asiento
        p.caja(0, 0.50f, -0.15f, 2.0f, 0.08f, 0.18f, madera);
        p.caja(0, 0.50f, 0.05f, 2.0f, 0.08f, 0.18f, madera);
        p.caja(0, 0.50f, 0.25f, 2.0f, 0.08f, 0.18f, madera);

        // Listones del respaldo
        p.caja(0, 0.75f, -0.28f, 2.0f, 0.15f, 0.06f, madera);
        p.caja(0, 0.95f, -0.28f, 2.0f, 0.15f, 0.06f, madera);
    }

    @Override
    protected void generarEfectoImpacto(List<Particula> particulas) {
        // Astillas de madera y chispas
        for (int i = 0; i < 20; i++) {
            float pVelX = (float) (Math.random() * 12.0f - 6.0f);
            float pVelY = 3.0f + (float) (Math.random() * 6.0f);
            float pVelZ = (float) (Math.random() * 12.0f - 6.0f);
            float r = 0.55f + (float) (Math.random() * 0.2f);
            float g = 0.35f + (float) (Math.random() * 0.15f);
            float b = 0.15f;
            particulas.add(new Particula(x, y + 0.6f, z, pVelX, pVelY, pVelZ, r, g, b, 0.3f));
        }
    }
}
