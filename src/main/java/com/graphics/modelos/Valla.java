package com.graphics.modelos;

import com.graphics.core.Pincel;

public class Valla extends Objeto {
    private boolean rota = false;
    private float velX, velY, velZ;
    private float anguloRotacion = 0;
    private float anguloGiro = 0;

    public Valla(float x, float z) {
        super(x, z);
    }

    public void romper(float velAuto, float dirX, float dirZ) {
        if (!rota) {
            rota = true;
            velY = 8f; // Salta hacia arriba
            velX = dirX * velAuto * 0.5f;
            velZ = dirZ * velAuto * 0.5f;
            anguloGiro = (float) (Math.random() * 10 - 5); // Giro aleatorio
        }
    }

    @Override
    public void actualizar(float deltaTime) {
        if (rota) {
            velY -= 15f * deltaTime; // Gravedad
            x += velX * deltaTime;
            y += velY * deltaTime;
            z += velZ * deltaTime;
            anguloRotacion += anguloGiro * deltaTime;

            // Suelo falso
            if (y < 0 && (Math.abs(x) < 35 && Math.abs(z) < 35)) {
                y = 0;
                velY = -velY * 0.4f; // Rebote contra el piso
                velX *= 0.5f;
                velZ *= 0.5f;
            }
        }
    }

    @Override
    public void dibujar(Pincel p) {
        p.rotar(anguloRotacion);
        // Postes de madera
        p.poste(-1.0f, 0, 0.2f, 1.0f, MADERA);
        p.poste(1.0f, 0, 0.2f, 1.0f, MADERA);
        // Tablas horizontales
        p.caja(0, 0.3f, 0, 2.4f, 0.2f, 0.1f, MADERA_OSCURA);
        p.caja(0, 0.7f, 0, 2.4f, 0.2f, 0.1f, MADERA_OSCURA);
    }

    @Override
    public boolean esColisionable() {
        return !rota; // Si está rota ya no colisiona (permite avanzar al auto)
    }

    @Override
    public boolean colisiona(Objeto objeto) {
        if (!esColisionable()) return false;
        // Colision simple de caja mas pequeña
        float cercaX = Math.max(x - 1.5f, Math.min(objeto.getX(), x + 1.5f));
        float cercaZ = Math.max(z - 0.5f, Math.min(objeto.getZ(), z + 0.5f));
        float distanciaX = objeto.getX() - cercaX;
        float distanciaZ = objeto.getZ() - cercaZ;
        return (distanciaX * distanciaX + distanciaZ * distanciaZ) < (objeto.getRadio() * objeto.getRadio());
    }
}
