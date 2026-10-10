package com.graphics.modelos;

import com.graphics.conduccion.Particula;
import com.graphics.core.Pincel;
import java.util.List;

/**
 * Poste de luz / Farola urbana de gran alcance e imponente presencia:
 * - Altura ampliada a 13.0 metros con base reforzada y mástil esbelto de doble tramo.
 * - Brazo arqueado en voladizo extendido 3.2 metros sobre la calzada vehicular a 11.5m de elevación.
 * - Cabezal de luminaria grande de alta luminosidad pero calibrado sin sobre-saturación.
 * - Sistema día/noche inteligente: se apaga totalmente de día y se ilumina de noche.
 * - Extiende ObjetoDestructible: puede ser impactado a velocidad, arrancándose del suelo
 *   y saliendo volando, dañando el auto y apagando su luminaria.
 */
public class Farola extends ObjetoDestructible {
    private final boolean dobleBrazo;

    public Farola(float x, float z) {
        this(x, z, 0, false);
    }

    public Farola(float x, float z, float rotacion) {
        this(x, z, rotacion, false);
    }

    public Farola(float x, float z, float rotacion, boolean dobleBrazo) {
        super(x, z, rotacion, 1.2f, 12.0f); // 12 puntos de daño al auto
        this.dobleBrazo = dobleBrazo;
    }

    /** Coordenada X exacta donde cuelga el foco emisor sobre la calzada */
    public float getPosicionLuzX() {
        if (roto) return x;
        if (dobleBrazo) return x;
        return x + 3.2f * (float) Math.sin(rotY);
    }

    /** Coordenada Y de elevación de la luminaria (11.4 metros de altura; se apaga si está derribada) */
    public float getPosicionLuzY() {
        if (roto) return -100f; // Farola derribada: luz apagada
        return y + 11.4f;
    }

    /** Coordenada Z exacta donde cuelga el foco emisor sobre la calzada */
    public float getPosicionLuzZ() {
        if (roto) return z;
        if (dobleBrazo) return z;
        return z + 3.2f * (float) Math.cos(rotY);
    }

    public boolean esDobleBrazo() {
        return dobleBrazo;
    }

    @Override
    public void dibujarModelo(Pincel local) {
        // 1. Base robusta ampliada de concreto y placa de acero en la acera
        local.caja(0, 0.35f, 0, 1.20f, 0.70f, 1.20f, METAL_OSCURO);
        local.caja(0, 0.85f, 0, 0.80f, 0.30f, 0.80f, METAL_OSCURO);

        // 2. Mástil vertical estilizado y muy alto de 13.0 metros de altura total
        local.poste(0, 1.0f, 0, 0.32f, 5.5f, METAL); // Tramo inferior (hasta 6.5m)
        local.caja(0, 6.6f, 0, 0.46f, 0.25f, 0.46f, METAL_OSCURO); // Anillo central / registro
        local.poste(0, 6.8f, 0, 0.22f, 5.5f, METAL); // Tramo superior (hasta 12.3m)
        local.caja(0, 12.4f, 0, 0.36f, 0.25f, 0.36f, METAL_OSCURO); // Corona superior
        local.caja(0, 12.6f, 0, 0.12f, 0.15f, 0.12f, rgb(0.2f, 0.4f, 0.85f)); // Sensor fotocélula

        // 3. Brazo curvado en voladizo hacia la calle (+Z local, 3.2 metros de alcance)
        dibujarBrazoYLuminaria(local, 1.0f);

        // 4. Si es farola doble (avenidas y puentes), dibujar brazo opuesto (-Z local)
        if (dobleBrazo) {
            dibujarBrazoYLuminaria(local, -1.0f);
        }
    }

    private void dibujarBrazoYLuminaria(Pincel local, float sentidoZ) {
        float s = sentidoZ;

        // Estructura tubular curvada en voladizo alto (gran extensión hacia el carril vehicular)
        local.caja(0, 12.2f, 1.0f * s, 0.18f, 0.18f, 2.0f, METAL);
        local.caja(0, 11.9f, 2.2f * s, 0.18f, 0.18f, 1.2f, METAL);
        local.caja(0, 11.6f, 2.9f * s, 0.18f, 0.18f, 0.6f, METAL);
        
        // Tirante diagonal de soporte estructural robusto
        float anguloTirante = (s > 0) ? (float) Math.toRadians(-26) : (float) Math.toRadians(26);
        local.caja(0, 11.2f, 1.0f * s, 0.12f, 0.12f, 1.8f, METAL, anguloTirante);

        // Carcasa de luminaria urbana grande (estilo cobra-head de autopista moderna)
        local.caja(0, 11.55f, 3.2f * s, 1.0f, 0.26f, 1.7f, METAL_OSCURO);
        local.caja(0, 11.72f, 3.1f * s, 0.7f, 0.12f, 1.2f, METAL);

        // Foco emisor:
        // Si está intacta: conBrilloDeNoche (SE APAGA DE DÍA y brilla de noche)
        // Si está rota/derribada: cristal apagado
        if (!roto) {
            local.conBrilloDeNoche(() -> {
                // Núcleo luminoso blanco-cálido incandescente
                local.caja(0, 11.40f, 3.2f * s, 0.75f, 0.12f, 1.4f, rgb(1.0f, 0.95f, 0.82f));
                // Difusor ámbar/dorado cálido
                local.caja(0, 11.35f, 3.2f * s, 1.05f, 0.08f, 1.7f, rgb(1.0f, 0.85f, 0.50f));
            });
        } else {
            local.caja(0, 11.40f, 3.2f * s, 0.75f, 0.12f, 1.4f, METAL_OSCURO);
        }
    }

    @Override
    protected void generarEfectoImpacto(List<Particula> particulas) {
        for (int i = 0; i < 28; i++) {
            float pVelX = (float) (Math.random() * 16.0f - 8.0f);
            float pVelY = 5.0f + (float) (Math.random() * 10.0f);
            float pVelZ = (float) (Math.random() * 16.0f - 8.0f);
            float r = 1.0f;
            float g = 0.8f + (float) (Math.random() * 0.2f);
            float b = 0.3f;
            particulas.add(new Particula(x, y + 2.8f, z, pVelX, pVelY, pVelZ, r, g, b, 0.4f));
        }
    }
}
