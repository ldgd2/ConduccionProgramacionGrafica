package com.graphics.conduccion;

import com.graphics.core.Color;
import com.graphics.core.Pincel;
import com.graphics.core.Renderizador;
import java.util.ArrayList;
import java.util.List;

/**
 * Gestiona el sistema de misiones y entregas de la ciudad.
 * Cada entrega requiere llegar a la baliza en la calle y detener el vehículo.
 */
public class SistemaEntregas {

    public static class Destino {
        public final String nombre;
        public final float x;
        public final float z;

        public Destino(String nombre, float x, float z) {
            this.nombre = nombre;
            this.x = x;
            this.z = z;
        }
    }

    private final List<Destino> destinos = new ArrayList<>();
    private int indiceActual = 0;
    private boolean completado = false;
    private float tiempoAnimacion = 0;
    private static final float RADIO_ENTREGA = 5.0f;
    private static final float VELOCIDAD_MAX_ENTREGA = 2.0f; // ~7 km/h para confirmar la entrega

    public SistemaEntregas() {
        // Destinos ubicados sobre calles transitables de la ciudad ampliada
        destinos.add(new Destino("Plaza Catedral (Centro)", -40.0f, -20.0f));
        destinos.add(new Destino("Sector Residencial Este", 70.0f, -20.0f));
        destinos.add(new Destino("Avenida Parque Central", -40.0f, 40.0f));
    }

    public void reiniciar() {
        indiceActual = 0;
        completado = false;
        tiempoAnimacion = 0;
    }

    public void actualizar(float deltaTime, Auto auto, List<Particula> particulas) {
        tiempoAnimacion += deltaTime;
        if (completado) return;

        Destino actual = getDestinoActual();
        if (actual == null) return;

        float dx = auto.getX() - actual.x;
        float dz = auto.getZ() - actual.z;
        float distancia = (float) Math.sqrt(dx * dx + dz * dz);

        // Si el vehículo está dentro del radio y casi detenido
        if (distancia < RADIO_ENTREGA && Math.abs(auto.getVelocidad()) < VELOCIDAD_MAX_ENTREGA && !auto.isDestruido()) {
            // Entrega confirmada con éxito
            generarEfectoEntrega(actual.x, actual.z, particulas);
            indiceActual++;
            if (indiceActual >= destinos.size()) {
                completado = true;
            }
        }
    }

    private void generarEfectoEntrega(float x, float z, List<Particula> particulas) {
        if (particulas == null) return;
        // Explosión festiva de partículas doradas / cian
        for (int i = 0; i < 40; i++) {
            float velX = (float) (Math.random() * 12 - 6);
            float velY = 6.0f + (float) (Math.random() * 12);
            float velZ = (float) (Math.random() * 12 - 6);
            float r = 1.0f;
            float g = (float) (0.6f + Math.random() * 0.4f);
            float b = (float) (Math.random() * 0.2f);
            particulas.add(new Particula(x, 1.0f, z, velX, velY, velZ, r, g, b, 0.4f));
        }
    }

    public void dibujar(Renderizador renderizador) {
        if (completado) return;
        Destino actual = getDestinoActual();
        if (actual == null) return;

        // Baliza 3D flotante con animación de rotación y levitación
        float alturaLevitacion = 2.0f + (float) Math.sin(tiempoAnimacion * 3.0f) * 0.6f;
        float rotacion = tiempoAnimacion * 2.0f;

        renderizador.brillar();
        // Columna vertical de luz
        renderizador.cajaGirada(actual.x, alturaLevitacion + 1.5f, actual.z, 0.8f, 3.0f, 0.8f, 1.0f, 0.85f, 0.1f, rotacion);
        // Base de la baliza en el asfalto (anillo/caja plana brillante)
        renderizador.cajaGirada(actual.x, 0.1f, actual.z, RADIO_ENTREGA * 1.5f, 0.05f, RADIO_ENTREGA * 1.5f, 1.0f, 0.85f, 0.1f, -rotacion * 0.5f);
        renderizador.luzNormal();
    }

    public void dibujarMarcadorMinimapa(Renderizador renderizador) {
        if (completado) return;
        Destino actual = getDestinoActual();
        if (actual == null) return;
        // Marcador dorado brillante en el minimapa
        renderizador.marcadorMinimapa(actual.x, 2.0f, actual.z, Color.AMARILLO);
    }

    public Destino getDestinoActual() {
        if (indiceActual < destinos.size()) {
            return destinos.get(indiceActual);
        }
        return null;
    }

    public String getNombreDestinoActual() {
        Destino d = getDestinoActual();
        return (d != null) ? d.nombre : "¡Todas Completadas!";
    }

    public int getEntregasCompletadas() {
        return indiceActual;
    }

    public int getTotalEntregas() {
        return destinos.size();
    }

    public boolean isCompletado() {
        return completado;
    }
}
