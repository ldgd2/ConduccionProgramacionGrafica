package com.graphics.modelos;

import com.graphics.core.Pincel;

public class Calle extends Objeto {
    private int fila, columna;

    public Calle(float x, float z, int fila, int columna) {
        super(x, z);
        this.fila = fila;
        this.columna = columna;
    }

    @Override
    public void dibujar(Pincel p) {
        // Líneas viales horizontales y pasos peatonales
        if (fila % 2 == 0 && columna % 2 == 1) {
            for (int desplazamiento = -3; desplazamiento <= 3; desplazamiento += 3) {
                p.caja(desplazamiento, 0.025f, 0, 1.6f, 0.03f, 0.13f, AMARILLO);
            }
            for (float paso = -3f; paso <= 3f; paso += 0.8f) {
                p.caja(-4.5f, 0.025f, paso, 0.5f, 0.03f, 0.4f, BLANCO);
                p.caja(4.5f, 0.025f, paso, 0.5f, 0.03f, 0.4f, BLANCO);
            }
        }
        // Líneas viales verticales y pasos peatonales
        if (columna % 2 == 0 && fila % 2 == 1) {
            for (int desplazamiento = -3; desplazamiento <= 3; desplazamiento += 3) {
                p.caja(0, 0.025f, desplazamiento, 0.13f, 0.03f, 1.6f, AMARILLO);
            }
            for (float paso = -3f; paso <= 3f; paso += 0.8f) {
                p.caja(paso, 0.025f, -4.5f, 0.4f, 0.03f, 0.5f, BLANCO);
                p.caja(paso, 0.025f, 4.5f, 0.4f, 0.03f, 0.5f, BLANCO);
            }
        }
    }
}
