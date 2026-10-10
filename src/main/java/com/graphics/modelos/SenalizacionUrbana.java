package com.graphics.modelos;

import com.graphics.conduccion.Particula;
import com.graphics.core.Color;
import com.graphics.core.Pincel;
import java.util.List;

/**
 * Mobiliario urbano y señalización destructible:
 * - Carteles de sector (Downtown, Residencial, Catedral)
 * - Señales de tráfico (STOP, velocidad máxima)
 * - Paradas de autobús y bocas de incendio (hidrantes)
 * Al ser impactados por el vehículo, se quiebran y salen volando, quitando vida al auto.
 */
public class SenalizacionUrbana extends ObjetoDestructible {

    public enum Tipo {
        CARTEL_DOWNTOWN,
        CARTEL_RESIDENCIAL,
        CARTEL_CATEDRAL,
        SENAL_STOP,
        SENAL_VELOCIDAD,
        PARADA_AUTOBUS,
        HIDRANTE
    }

    private final Tipo tipo;

    public SenalizacionUrbana(float x, float z, Tipo tipo, float rotacion) {
        super(x, z, rotacion, calcularRadio(tipo), calcularDanio(tipo));
        this.tipo = tipo;
    }

    private static float calcularRadio(Tipo tipo) {
        switch (tipo) {
            case PARADA_AUTOBUS: return 1.6f;
            case CARTEL_DOWNTOWN:
            case CARTEL_RESIDENCIAL:
            case CARTEL_CATEDRAL: return 1.3f;
            case HIDRANTE: return 0.7f;
            default: return 0.9f;
        }
    }

    private static float calcularDanio(Tipo tipo) {
        switch (tipo) {
            case PARADA_AUTOBUS: return 14.0f;
            case CARTEL_DOWNTOWN:
            case CARTEL_RESIDENCIAL:
            case CARTEL_CATEDRAL: return 10.0f;
            case HIDRANTE: return 8.0f;
            default: return 7.0f; // STOP y señales
        }
    }

    public Tipo getTipo() {
        return tipo;
    }

    @Override
    public void dibujarModelo(Pincel local) {
        switch (tipo) {
            case CARTEL_DOWNTOWN:
                local.poste(0, 0, 0.25f, 4.5f, Color.METAL_OSCURO);
                local.caja(0, 4.0f, 0, 3.8f, 1.2f, 0.2f, Color.rgb(0.05f, 0.35f, 0.65f));
                local.caja(0, 4.0f, 0.12f, 3.5f, 0.9f, 0.05f, Color.BLANCO);
                local.caja(0, 4.0f, 0.16f, 3.2f, 0.6f, 0.05f, Color.rgb(0.05f, 0.35f, 0.65f));
                local.conBrillo(() -> {
                    local.caja(0, 3.5f, 0.18f, 3.2f, 0.15f, 0.05f, Color.AMARILLO);
                });
                break;

            case CARTEL_RESIDENCIAL:
                local.poste(0, 0, 0.25f, 4.5f, Color.METAL_OSCURO);
                local.caja(0, 4.0f, 0, 3.8f, 1.2f, 0.2f, Color.rgb(0.1f, 0.5f, 0.2f));
                local.caja(0, 4.0f, 0.12f, 3.5f, 0.9f, 0.05f, Color.BLANCO);
                local.caja(0, 4.0f, 0.16f, 3.2f, 0.6f, 0.05f, Color.rgb(0.1f, 0.5f, 0.2f));
                local.conBrillo(() -> {
                    local.caja(0, 3.5f, 0.18f, 3.2f, 0.15f, 0.05f, Color.BLANCO);
                });
                break;

            case CARTEL_CATEDRAL:
                local.poste(0, 0, 0.2f, 4.0f, Color.METAL_OSCURO);
                local.caja(0, 3.5f, 0, 3.0f, 1.0f, 0.15f, Color.rgb(0.45f, 0.25f, 0.1f));
                local.caja(0, 3.5f, 0.1f, 2.7f, 0.7f, 0.05f, Color.BLANCO);
                local.caja(0, 3.5f, 0.13f, 2.5f, 0.5f, 0.05f, Color.rgb(0.45f, 0.25f, 0.1f));
                break;

            case SENAL_STOP:
                local.poste(0, 0, 0.12f, 3.2f, Color.METAL);
                local.caja(0, 3.2f, 0, 1.1f, 1.1f, 0.08f, Color.ROJO);
                local.caja(0, 3.2f, 0, 0.9f, 0.9f, 0.09f, Color.BLANCO);
                local.caja(0, 3.2f, 0, 0.75f, 0.75f, 0.10f, Color.ROJO);
                local.caja(0, 3.2f, 0.06f, 0.55f, 0.18f, 0.02f, Color.BLANCO);
                break;

            case SENAL_VELOCIDAD:
                local.poste(0, 0, 0.12f, 3.2f, Color.METAL);
                local.caja(0, 3.2f, 0, 1.0f, 1.0f, 0.08f, Color.ROJO);
                local.caja(0, 3.2f, 0.05f, 0.8f, 0.8f, 0.05f, Color.BLANCO);
                local.caja(0, 3.2f, 0.08f, 0.45f, 0.25f, 0.02f, Color.METAL_OSCURO);
                break;

            case PARADA_AUTOBUS:
                local.poste(-1.5f, 0, -0.6f, 0.15f, 2.6f, Color.METAL_OSCURO);
                local.poste(1.5f, 0, -0.6f, 0.15f, 2.6f, Color.METAL_OSCURO);
                local.poste(-1.5f, 0, 0.6f, 0.15f, 2.6f, Color.METAL_OSCURO);
                local.poste(1.5f, 0, 0.6f, 0.15f, 2.6f, Color.METAL_OSCURO);
                local.caja(0, 2.7f, 0, 3.6f, 0.15f, 1.8f, Color.rgb(0.2f, 0.4f, 0.7f));
                local.caja(0, 1.3f, -0.6f, 2.9f, 2.2f, 0.05f, Color.VIDRIO);
                local.caja(0, 0.5f, -0.2f, 2.2f, 0.15f, 0.5f, Color.MADERA);
                break;

            case HIDRANTE:
                local.caja(0, 0.15f, 0, 0.5f, 0.3f, 0.5f, Color.ROJO);
                local.caja(0, 0.5f, 0, 0.35f, 0.6f, 0.35f, Color.ROJO);
                local.caja(0, 0.85f, 0, 0.25f, 0.2f, 0.25f, Color.METAL);
                local.caja(0, 0.5f, 0, 0.55f, 0.15f, 0.15f, Color.METAL);
                break;
        }
    }

    @Override
    protected void generarEfectoImpacto(List<Particula> particulas) {
        if (tipo == Tipo.HIDRANTE) {
            // Chorro y salpicadura de agua a presión
            for (int i = 0; i < 30; i++) {
                float pVelX = (float) (Math.random() * 8.0f - 4.0f);
                float pVelY = 6.0f + (float) (Math.random() * 10.0f);
                float pVelZ = (float) (Math.random() * 8.0f - 4.0f);
                float r = 0.2f, g = 0.6f + (float)(Math.random() * 0.3f), b = 0.95f;
                particulas.add(new Particula(x, y + 0.8f, z, pVelX, pVelY, pVelZ, r, g, b, 0.35f));
            }
        } else {
            super.generarEfectoImpacto(particulas);
        }
    }
}
