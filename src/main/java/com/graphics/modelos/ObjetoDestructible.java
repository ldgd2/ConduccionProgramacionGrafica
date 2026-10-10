package com.graphics.modelos;

import com.graphics.conduccion.Particula;
import com.graphics.core.Pincel;
import java.util.List;

/**
 * Base para mobiliario urbano y elementos destructibles:
 * semáforos, farolas, bancas, árboles, vallas, carteles, paradas de autobús e hidrantes.
 * Al chocar con el vehículo:
 * 1. Ejecuta una animación de impacto saliendo disparado con rotación y reduciendo su escala.
 * 2. Genera una explosión de partículas, astillas o chispas.
 * 3. Al culminar la animación de 1 segundo, DESAPARECE por completo de la calle.
 * 4. Resta vida al auto ("danioAlAuto").
 * 5. Se restaura íntegramente al reiniciar el juego con 'R'.
 */
public abstract class ObjetoDestructible extends Objeto {
    protected boolean roto = false;
    protected boolean desaparecido = false;
    protected float tiempoDestruccion = 0;
    protected final float DURACION_DESTRUCCION = 0.50f; // 0.50s de animación de destrucción antes de desaparecer

    protected final float origX;
    protected final float origY;
    protected final float origZ;
    protected final float rotacionBase;

    protected float velX, velY, velZ;
    protected float rotY;
    protected float velRotY;

    protected float danioAlAuto;
    protected float radioImpacto;

    public ObjetoDestructible(float x, float z, float rotacionBase, float radioImpacto, float danioAlAuto) {
        super(x, z);
        this.origX = x;
        this.origY = 0;
        this.origZ = z;
        this.rotacionBase = rotacionBase;
        this.rotY = rotacionBase;
        this.radioImpacto = radioImpacto;
        this.danioAlAuto = danioAlAuto;
    }

    public boolean isRoto() {
        return roto;
    }

    public boolean isDesaparecido() {
        return desaparecido;
    }

    public float getDanioAlAuto() {
        return danioAlAuto;
    }

    public float getRadioImpacto() {
        return radioImpacto;
    }

    public void reiniciar() {
        this.roto = false;
        this.desaparecido = false;
        this.tiempoDestruccion = 0;
        this.x = origX;
        this.y = origY;
        this.z = origZ;
        this.velX = 0;
        this.velY = 0;
        this.velZ = 0;
        this.rotY = rotacionBase;
        this.velRotY = 0;
    }

    public void romper(float velAuto, float dirX, float dirZ, List<Particula> particulas) {
        if (roto) return;
        roto = true;
        this.tiempoDestruccion = 0;
        this.desaparecido = false;

        float fuerza = Math.max(Math.abs(velAuto), 8.0f);
        // Empuje horizontal en la trayectoria del vehículo (no salta al cielo)
        velX = dirX * fuerza * 0.70f + (float) (Math.random() * 2.0f - 1.0f);
        velZ = dirZ * fuerza * 0.70f + (float) (Math.random() * 2.0f - 1.0f);
        // Pequeño sacudón vertical al quebrarse de la base
        velY = 1.0f + (float) (Math.random() * 1.0f);

        // Giro violento de desintegración
        velRotY = (float) (Math.random() * 22.0f - 11.0f);

        if (particulas != null) {
            generarEfectoImpacto(particulas);
        }
    }

    protected void generarEfectoImpacto(List<Particula> particulas) {
        for (int i = 0; i < 24; i++) {
            float pVelX = (float) (Math.random() * 12.0f - 6.0f);
            float pVelY = 2.5f + (float) (Math.random() * 6.0f);
            float pVelZ = (float) (Math.random() * 12.0f - 6.0f);
            float r = 1.0f;
            float g = 0.5f + (float) (Math.random() * 0.4f);
            float b = 0.1f;
            float tam = 0.3f + (float) (Math.random() * 0.35f);
            particulas.add(new Particula(x, y + 1.2f, z, pVelX, pVelY, pVelZ, r, g, b, tam));
        }
    }

    @Override
    public void actualizar(float deltaTime) {
        if (roto && !desaparecido) {
            tiempoDestruccion += deltaTime;
            if (tiempoDestruccion >= DURACION_DESTRUCCION) {
                desaparecido = true;
                return;
            }

            velY -= 14.0f * deltaTime; // Gravedad
            x += velX * deltaTime;
            y += velY * deltaTime;
            z += velZ * deltaTime;
            rotY += velRotY * deltaTime;

            // Fricción rápida para detener desplazamiento
            velX *= Math.max(0f, 1.0f - 3.5f * deltaTime);
            velZ *= Math.max(0f, 1.0f - 3.5f * deltaTime);

            if (y < 0) {
                y = 0;
                velY = 0;
            }
        }
    }

    @Override
    public boolean esColisionable() {
        return false;
    }

    @Override
    public void dibujar(Pincel p) {
        if (desaparecido) return; // ¡Desaparece totalmente al destruirse!

        Pincel local = p.en(x, y, z).rotar(rotY);
        if (roto) {
            // Animación de destrucción: escala decrece rápidamente a 0
            float progreso = Math.min(1.0f, tiempoDestruccion / DURACION_DESTRUCCION);
            float factor = Math.max(0.0f, 1.0f - progreso);
            local.escalar(factor);
        }
        dibujarModelo(local);
        local.escalar(1.0f); // Restaura escala
    }

    public abstract void dibujarModelo(Pincel local);
}
