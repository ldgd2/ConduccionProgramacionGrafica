package com.graphics.modelos;

import com.graphics.core.Pincel;

public class Semaforo extends Objeto {
    private float tiempoCiclo = 0;
    // 0 = Rojo, 1 = Verde, 2 = Amarillo
    private int estado = 0; 
    
    // Duraciones en segundos
    private final float DURACION_ROJO = 5.0f;
    private final float DURACION_VERDE = 4.0f;
    private final float DURACION_AMARILLO = 2.0f;

    public Semaforo(float x, float z) {
        super(x, z);
    }

    @Override
    public void actualizar(float deltaTime) {
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

    @Override
    public void dibujar(Pincel pincel) {
        pincel.semaforo(0, 0, estado);
    }

    @Override
    public boolean esColisionable() {
        return true;
    }
    @Override
    public float getRadio() {
        return 0.5f;
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
