package com.graphics.conduccion;

import com.graphics.modelos.Entorno;
import java.util.List;

/**
 * Vehículo autónomo de tráfico de la ciudad.
 * Sigue de forma autónoma circuitos cerrados a lo largo de las calles principales,
 * manteniendo velocidad constante, orientación correcta y realizando giros limpios
 * en las intersecciones sin atravesar edificios ni salir del mapa.
 */
public class AutoNPC extends Auto {
    private final float[][] waypoints;
    private int indiceWaypoint = 0;
    private final float velocidadCrucero;
    private boolean destruido = false;
    private float chasisR = 0.5f, chasisG = 0.5f, chasisB = 0.5f;

    private final float startX;
    private final float startZ;
    private final float startAngulo;

    public AutoNPC(Entorno entorno, float[][] rutaWaypoints, float velCrucero, float r, float g, float b) {
        super(entorno);
        this.waypoints = rutaWaypoints;
        this.velocidadCrucero = velCrucero;
        this.chasisR = r;
        this.chasisG = g;
        this.chasisB = b;

        this.startX = waypoints[0][0];
        this.startZ = waypoints[0][1];
        
        // Calcular ángulo inicial hacia el segundo waypoint
        float dx = waypoints[1][0] - waypoints[0][0];
        float dz = waypoints[1][1] - waypoints[0][1];
        this.startAngulo = (float) Math.atan2(-dx, -dz);

        reiniciar();
    }

    public void setColor(float r, float g, float b) {
        this.chasisR = r;
        this.chasisG = g;
        this.chasisB = b;
    }

    public float getChasisR() { return chasisR; }
    public float getChasisG() { return chasisG; }
    public float getChasisB() { return chasisB; }

    @Override
    public boolean isDestruido() {
        return destruido;
    }

    @Override
    public void reiniciar() {
        this.x = startX;
        this.y = 0;
        this.z = startZ;
        this.angulo = startAngulo;
        this.indiceWaypoint = 1;
        this.destruido = false;
    }

    public void explotar(List<Particula> listaParticulas, float velocidadImpacto, float dirX, float dirZ) {
        if (destruido) return;
        destruido = true;

        if (listaParticulas != null) {
            for (int i = 0; i < 35; i++) {
                float velX = dirX * velocidadImpacto * 0.4f + (float) (Math.random() * 16 - 8);
                float velY = 8.0f + (float) (Math.random() * 14);
                float velZ = dirZ * velocidadImpacto * 0.4f + (float) (Math.random() * 16 - 8);

                float pR = (Math.random() > 0.5) ? chasisR : 0.2f;
                float pG = (Math.random() > 0.5) ? chasisG : 0.2f;
                float pB = (Math.random() > 0.5) ? chasisB : 0.2f;
                float size = 0.25f + (float) Math.random() * 0.5f;

                listaParticulas.add(new Particula(x, y + 0.8f, z, velX, velY, velZ, pR, pG, pB, size));
            }
        }
    }

    @Override
    public void actualizar(float deltaTime, boolean adelante, boolean atras, boolean izquierda, boolean derecha, boolean freno) {
        if (destruido) return;

        // Objetivo actual
        float targetX = waypoints[indiceWaypoint][0];
        float targetZ = waypoints[indiceWaypoint][1];

        float dx = targetX - x;
        float dz = targetZ - z;
        float dist = (float) Math.sqrt(dx * dx + dz * dz);

        // Si llegó cerca del waypoint actual, avanzar al siguiente
        if (dist < 3.5f) {
            indiceWaypoint = (indiceWaypoint + 1) % waypoints.length;
            targetX = waypoints[indiceWaypoint][0];
            targetZ = waypoints[indiceWaypoint][1];
            dx = targetX - x;
            dz = targetZ - z;
        }

        // Ángulo deseado hacia el waypoint (en nuestro sistema: frente = -sin(angulo), -cos(angulo))
        float anguloDeseado = (float) Math.atan2(-dx, -dz);

        // Diferencia de ángulo normalizada en [-PI, PI]
        float diff = anguloDeseado - angulo;
        while (diff > Math.PI) diff -= 2 * Math.PI;
        while (diff < -Math.PI) diff += 2 * Math.PI;

        // Giro suave y natural
        float velGiro = 3.5f * deltaTime;
        if (Math.abs(diff) < velGiro) {
            angulo = anguloDeseado;
        } else {
            angulo += Math.signum(diff) * velGiro;
        }

        // Avance en la dirección actual
        float fX = -(float) Math.sin(angulo);
        float fZ = -(float) Math.cos(angulo);
        x += fX * velocidadCrucero * deltaTime;
        z += fZ * velocidadCrucero * deltaTime;
        y = getEntorno() != null ? getEntorno().getAlturaSuelo(x, z) : 0;
    }

    @Override
    public float getVelocidad() {
        return destruido ? 0 : velocidadCrucero;
    }
}
