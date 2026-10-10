package com.graphics.modelos;

import com.graphics.core.Pincel;

public class Puente extends Objeto {
    private int fila;
    private int columna;

    public Puente(float x, float z, int fila, int columna) {
        super(x, z);
        this.fila = fila;
        this.columna = columna;
    }

    @Override
    public void dibujar(Pincel p) {
        // Asfalto del puente
        p.caja(0, 0.05f, 0, 10f, 0.4f, 10f, ASFALTO);
        
        // Líneas viales amarillas
        for (int desplazamiento = -3; desplazamiento <= 3; desplazamiento += 3) {
            p.caja(desplazamiento, 0.26f, 0, 1.6f, 0.03f, 0.13f, AMARILLO);
        }

        // Barandas metálicas laterales
        p.caja(0, 1.0f, -4.5f, 10.0f, 1.5f, 0.2f, GRIS);
        p.caja(0, 1.0f, 4.5f, 10.0f, 1.5f, 0.2f, GRIS);
    }

    @Override
    public boolean esColisionable() {
        return false; // El auto puede pasar por encima sin chocar con la base (las barandas podrían requerir colisón, pero lo dejamos simple)
    }
}
