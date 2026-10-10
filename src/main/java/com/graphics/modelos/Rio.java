package com.graphics.modelos;

import com.graphics.core.Pincel;

public class Rio extends Objeto {
    public Rio(float x, float z) {
        super(x, z);
    }

    @Override
    public void dibujar(Pincel p) {
        p.conAgua(() -> {
            p.caja(0, -5.0f, 0, 10f, 0.5f, 10f, AGUA);
        });
    }

    @Override
    public boolean esColisionable() {
        return false; // El auto cae por aquí
    }
}
