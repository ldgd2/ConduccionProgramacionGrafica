package com.graphics.modelos;

import com.graphics.core.Renderizador;

public class PuenteLevadizo extends Objeto implements Rampa {
    private float tiempo = 0;
    private float ancho = 10;
    private float largo = 30;
    private float direccionX;
    private float direccionZ;
    
    public PuenteLevadizo(float x, float z, float dirX, float dirZ) {
        super(x, z);
        this.direccionX = dirX;
        this.direccionZ = dirZ;
    }

    @Override
    public float getRadio() {
        return largo / 2;
    }
    
    @Override
    public boolean colisiona(Objeto otro) {
        return false; // Se pasa por encima
    }

    @Override
    public void actualizar(float deltaTime) {
        tiempo += deltaTime;
    }

    public float getAnguloActual() {
        // Ciclo de levantamiento (sube, se mantiene, baja)
        float ciclo = tiempo % 20.0f; // 20 segundos
        if (ciclo < 5) return 0; // Plano
        if (ciclo < 8) return (ciclo - 5) / 3.0f * (float)Math.toRadians(35); // Sube a 35 grados
        if (ciclo < 12) return (float)Math.toRadians(35); // Arriba
        if (ciclo < 15) return (1.0f - (ciclo - 12) / 3.0f) * (float)Math.toRadians(35); // Baja
        return 0; // Plano
    }

    @Override
    public float getAlturaEn(float px, float pz) {
        // Asume direccionZ = 1 (Alineado en Z, sube hacia +Z)
        if (direccionZ > 0) {
            float dz = pz - (this.z - largo / 2);
            if (dz >= 0 && dz <= largo && Math.abs(px - this.x) < ancho / 2) {
                float angulo = getAnguloActual();
                return (float)(Math.sin(angulo) * dz);
            }
        }
        return 0;
    }

    @Override
    public float getPendiente() {
        return (float)Math.tan(getAnguloActual());
    }

    @Override
    public void dibujar(Renderizador renderizador) {
        float angulo = getAnguloActual();
        
        // Pivot point at one end
        float cy = (float)(Math.sin(angulo) * largo / 2);
        float cz = this.z - largo / 2 + (float)(Math.cos(angulo) * largo / 2);
        
        // Color rojo para destacarlo
        renderizador.cajaRampa(this.x, cy, cz, ancho, 1.0f, largo, ROJO, 0, -angulo);
    }
}
