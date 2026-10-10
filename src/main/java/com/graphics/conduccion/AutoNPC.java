package com.graphics.conduccion;

import com.graphics.modelos.Entorno;

public class AutoNPC extends Auto {
    private float tiempoHastaCambioDir = 0;
    private int accionActual = 0; // 0 = adelante, 1 = izquierda, 2 = derecha
    private boolean destruido = false;
    private float chasisR = 0.5f, chasisG = 0.5f, chasisB = 0.5f;

    public AutoNPC(Entorno entorno, float startX, float startZ, float anguloInicial) {
        super(entorno);
        this.x = startX;
        this.z = startZ;
        this.angulo = anguloInicial;
    }

    public void setColor(float r, float g, float b) {
        this.chasisR = r;
        this.chasisG = g;
        this.chasisB = b;
    }
    
    public boolean isDestruido() {
        return destruido;
    }
    
    public void explotar(java.util.List<Particula> listaParticulas, float velocidadImpacto, float dirX, float dirZ) {
        if (destruido) return;
        destruido = true;
        
        // Generar 30 partículas de chatarra
        for (int i = 0; i < 30; i++) {
            float velX = dirX * velocidadImpacto * 0.5f + (float) (Math.random() * 20 - 10);
            float velY = 10f + (float) (Math.random() * 15);
            float velZ = dirZ * velocidadImpacto * 0.5f + (float) (Math.random() * 20 - 10);
            
            // Mitad del color del auto, mitad grises/negras
            float pR = (Math.random() > 0.5) ? chasisR : 0.2f;
            float pG = (Math.random() > 0.5) ? chasisG : 0.2f;
            float pB = (Math.random() > 0.5) ? chasisB : 0.2f;
            
            float size = 0.2f + (float) Math.random() * 0.6f;
            
            listaParticulas.add(new Particula(x, y + 1.0f, z, velX, velY, velZ, pR, pG, pB, size));
        }
    }

    @Override
    public void actualizar(float deltaTime, boolean adelante, boolean atras, boolean izquierda, boolean derecha, boolean freno) {
        if (destruido) return; // Ya no se mueve

        // Ignoramos el input del jugador y usamos IA basica
        tiempoHastaCambioDir -= deltaTime;
        
        boolean aiAdelante = true;
        boolean aiIzquierda = false;
        boolean aiDerecha = false;
        boolean aiFreno = false;

        // Comprobar si hay un obstaculo al frente
        float fX = -(float) Math.sin(angulo);
        float fZ = -(float) Math.cos(angulo);
        
        // Simulamos un auto ficticio mas adelante para ver si choca
        com.graphics.modelos.Objeto vision = new com.graphics.modelos.Objeto(x + fX * 5.0f, z + fZ * 5.0f) {
            @Override public float getRadio() { return 1.65f; }
        };
        
        boolean caminoLibre = getEntorno().puedeCircular(vision, getVelocidad(), fX, fZ) && !getEntorno().enAgua(x + fX * 5.0f, z + fZ * 5.0f);
        
        if (!caminoLibre) {
            // Sigue avanzando pero dobla bruscamente
            aiAdelante = true;
            if (accionActual != 1 && accionActual != 2) {
                accionActual = Math.random() > 0.5 ? 1 : 2; // Elegir giro
            }
            if (accionActual == 1) aiIzquierda = true;
            else aiDerecha = true;
        } else {
            // Posible giro aleatorio en intersecciones (cada cierto tiempo)
            if (tiempoHastaCambioDir <= 0) {
                accionActual = 0; // Ir recto
                tiempoHastaCambioDir = 2.0f + (float) Math.random() * 3.0f; // Sigue recto de 2 a 5 segundos
                
                // 30% chance de doblar suavemente un rato si va lento, o esquivar
                if (Math.random() < 0.3) {
                    accionActual = Math.random() > 0.5 ? 1 : 2;
                    tiempoHastaCambioDir = 0.5f + (float) Math.random() * 1.0f;
                }
            }
            
            if (accionActual == 1) aiIzquierda = true;
            else if (accionActual == 2) aiDerecha = true;
        }
        
        // Limitar la velocidad de los NPCs
        if (getVelocidad() > 8.0f) {
            aiAdelante = false;
        }
        
        super.actualizar(deltaTime, aiAdelante, false, aiIzquierda, aiDerecha, false);
    }
}
