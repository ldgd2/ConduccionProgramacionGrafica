package com.graphics.modelos;

import com.graphics.core.Pincel;

public class Farola extends Objeto {
    private float altura = 5f;

    public Farola(float x, float z) {
        super(x, z);
    }

    @Override
    public boolean esColisionable() {
        return true;
    }

    @Override
    public float getRadio() {
        return 1.0f; // Poste delgado
    }

    @Override
    public void dibujar(Pincel pincel) {
        pincel.farola(0, 0, altura);
    }

    @Override
    public boolean colisiona(Objeto objeto) {
        if (!esColisionable()) return false;
        float distX = this.x - objeto.getX();
        float distZ = this.z - objeto.getZ();
        float radioSuma = this.getRadio() + objeto.getRadio();
        return (distX * distX + distZ * distZ) < (radioSuma * radioSuma);
    }
}
