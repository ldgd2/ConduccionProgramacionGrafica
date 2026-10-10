package com.graphics.modelos;

import com.graphics.core.Color;
import com.graphics.core.Pincel;

public class Parque extends Objeto {
    public Parque(float x, float z) {
        super(x, z);
    }

    @Override
    public void dibujar(Pincel p) {
        // Base / Acera principal
        p.acera(0, 0, 10, 10);
        
        // 4 Cuadrantes de césped
        p.piso(-2.5f, 0.35f, -2.5f, 4.0f, 4.0f, Color.CESPED);
        p.piso(2.5f, 0.35f, -2.5f, 4.0f, 4.0f, Color.CESPED);
        p.piso(-2.5f, 0.35f, 2.5f, 4.0f, 4.0f, Color.CESPED);
        p.piso(2.5f, 0.35f, 2.5f, 4.0f, 4.0f, Color.CESPED);
        
        // Monumento central
        p.caja(0, 0.5f, 0, 2.0f, 0.8f, 2.0f, Color.GRIS_CLARO);
        p.poste(0, 0.9f, 0, 0.5f, 2.0f, Color.METAL);
        
    }

    @Override
    public boolean esColisionable() {
        return false; // El parque es transitable libremente por el vehículo
    }
}
