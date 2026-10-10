package com.graphics.modelos;

import com.graphics.conduccion.Particula;
import com.graphics.core.Pincel;
import java.util.List;

public class Semaforo extends ObjetoDestructible {
    private float tiempoCiclo = 0;
    // 0 = Rojo, 1 = Verde, 2 = Amarillo
    private int estado = 0; 
    
    // Duraciones en segundos
    private final float DURACION_ROJO = 5.0f;
    private final float DURACION_VERDE = 4.0f;
    private final float DURACION_AMARILLO = 2.0f;

    public Semaforo(float x, float z) {
        super(x, z, 0, 0.9f, 10.0f); // 10 puntos de daño al auto
    }

    @Override
    public void actualizar(float deltaTime) {
        super.actualizar(deltaTime);
        if (!roto) {
            tiempoCiclo += deltaTime;
            if (estado == 0 && tiempoCiclo >= DURACION_ROJO) {
                estado = 1;
                tiempoCiclo = 0;
            } else if (estado == 1 && tiempoCiclo >= DURACION_VERDE) {
                estado = 2;
                tiempoCiclo = 0;
            } else if (estado == 2 && tiempoCiclo >= DURACION_AMARILLO) {
                estado = 0;
                tiempoCiclo = 0;
            }
        }
    }

    @Override
    public void dibujarModelo(Pincel local) {
        local.semaforo(0, 0, estado);
    }

    @Override
    protected void generarEfectoImpacto(List<Particula> particulas) {
        for (int i = 0; i < 25; i++) {
            float pVelX = (float) (Math.random() * 14.0f - 7.0f);
            float pVelY = 4.0f + (float) (Math.random() * 9.0f);
            float pVelZ = (float) (Math.random() * 14.0f - 7.0f);
            float r = 1.0f, g = 0.8f, b = 0.2f;
            if (i % 3 == 0) { r = 1f; g = 0.1f; b = 0.1f; }
            else if (i % 3 == 1) { r = 0.1f; g = 1f; b = 0.1f; }
            particulas.add(new Particula(x, y + 2.0f, z, pVelX, pVelY, pVelZ, r, g, b, 0.35f));
        }
    }
}
