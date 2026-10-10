package com.graphics.core;

import com.graphics.modelos.Objeto;

/**
 * Pincel 3D de altísimo nivel.
 * Permite dibujar en la escena utilizando lenguaje natural ("hablando como persona"),
 * eliminando por completo la necesidad de lidiar con OpenGL, matrices, shaders o fórmulas trigonométricas.
 *
 * Soporta posicionamiento relativo al objeto, rotación automática, paleta de colores y
 * primitivas urbanas semánticas (árboles, postes, aceras, ventanas, edificios, farolas, etc.).
 */
public class Pincel {
    private final Renderizador render;
    private float ox = 0;
    private float oy = 0;
    private float oz = 0;
    private float anguloBase = 0; // En radianes (Yaw)

    public Pincel(Renderizador renderizador) {
        this.render = renderizador;
    }

    public Pincel(Renderizador renderizador, float x, float y, float z) {
        this.render = renderizador;
        this.ox = x;
        this.oy = y;
        this.oz = z;
    }

    public Pincel(Renderizador renderizador, Objeto objeto) {
        this.render = renderizador;
        if (objeto != null) {
            this.ox = objeto.getX();
            this.oy = objeto.getY();
            this.oz = objeto.getZ();
        }
    }

    // ==================== TRANSFORMACIONES Y POSICIONAMIENTO ====================

    /** Mueve el origen del pincel a una posición absoluta */
    public Pincel en(float x, float y, float z) {
        this.ox = x;
        this.oy = y;
        this.oz = z;
        return this;
    }

    /** Posiciona el origen del pincel en las coordenadas de un objeto del mundo */
    public Pincel en(Objeto objeto) {
        if (objeto != null) {
            this.ox = objeto.getX();
            this.oy = objeto.getY();
            this.oz = objeto.getZ();
        }
        return this;
    }

    /** Desplaza el origen relativamente */
    public Pincel desplazar(float dx, float dy, float dz) {
        this.ox += dx;
        this.oy += dy;
        this.oz += dz;
        return this;
    }

    /** Define el ángulo de rotación base del objeto (Yaw) en radianes */
    public Pincel rotar(float anguloRadianes) {
        this.anguloBase = anguloRadianes;
        return this;
    }

    /** Define el ángulo de rotación base del objeto en grados */
    public Pincel rotarGrados(float grados) {
        this.anguloBase = (float) Math.toRadians(grados);
        return this;
    }

    // ==================== ILUMINACIÓN Y EFECTOS ====================

    /** Activa emisión de luz pura (faros de autos, semáforos, lámparas, sol) */
    public Pincel brillar() {
        render.entero("uEmitirLuz", 1);
        return this;
    }

    /** Activa resplandor nocturno inteligente (ventanas de edificios que se iluminan al oscurecer) */
    public Pincel brillarDeNoche() {
        render.entero("uEmitirLuz", 2);
        return this;
    }

    /** Restaura la iluminación normal con sombreado difuso */
    public Pincel normal() {
        render.entero("uEmitirLuz", 0);
        return this;
    }

    /** Ejecuta un bloque de dibujo emitiendo luz pura y restaura automáticamente */
    public Pincel conBrillo(Runnable accion) {
        brillar();
        try {
            accion.run();
        } finally {
            normal();
        }
        return this;
    }

    /** Ejecuta un bloque de dibujo con resplandor nocturno y restaura automáticamente */
    public Pincel conBrilloDeNoche(Runnable accion) {
        brillarDeNoche();
        try {
            accion.run();
        } finally {
            normal();
        }
        return this;
    }

    /** Ejecuta un bloque de dibujo con shader de oleaje de agua */
    public Pincel conAgua(Runnable accion) {
        render.entero("uEsAgua", 1);
        try {
            accion.run();
        } finally {
            render.entero("uEsAgua", 0);
        }
        return this;
    }


    // ==================== PRIMITIVAS BÁSICAS ====================

    /**
     * Dibuja una caja 3D en posición local relativa al pincel con un color.
     */
    public Pincel caja(float dx, float dy, float dz, float sx, float sy, float sz, Color color) {
        return caja(dx, dy, dz, sx, sy, sz, color, 0);
    }

    /**
     * Dibuja una caja 3D en posición local con rotación propia adicional.
     */
    public Pincel caja(float dx, float dy, float dz, float sx, float sy, float sz, Color color, float anguloAdicional) {
        float rotTotal = anguloBase + anguloAdicional;
        
        // Aplicar rotación respecto al origen base si el pincel tiene rotación
        float finalX = ox;
        float finalZ = oz;
        if (anguloBase != 0) {
            float cos = (float) Math.cos(anguloBase);
            float sin = (float) Math.sin(anguloBase);
            finalX += dx * cos + dz * sin;
            finalZ += -dx * sin + dz * cos;
        } else {
            finalX += dx;
            finalZ += dz;
        }
        float finalY = oy + dy;

        render.cajaGirada(finalX, finalY, finalZ, sx, sy, sz, color.r(), color.g(), color.b(), rotTotal);
        return this;
    }

    /**
     * Dibuja un cubo perfecto centrado en la posición local dada.
     */
    public Pincel cubo(float dx, float dy, float dz, float lado, Color color) {
        return caja(dx, dy, dz, lado, lado, lado, color);
    }

    /**
     * Dibuja un piso / superficie horizontal en la posición local dada.
     */
    public Pincel piso(float dx, float dy, float dz, float ancho, float profundidad, Color color) {
        return caja(dx, dy, dz, ancho, 0.15f, profundidad, color);
    }

    public Pincel piso(float dx, float dz, float ancho, float profundidad, Color color) {
        return piso(dx, 0.05f, dz, ancho, profundidad, color);
    }

    /**
     * Dibuja una pared vertical plana.
     */
    public Pincel pared(float dx, float dy, float dz, float ancho, float alto, Color color) {
        return caja(dx, dy + alto / 2f, dz, ancho, alto, 0.2f, color);
    }

    /**
     * Dibuja un poste vertical centrado en el suelo.
     */
    public Pincel poste(float dx, float dz, float altura, Color color) {
        return poste(dx, 0, dz, 0.25f, altura, color);
    }

    public Pincel poste(float dx, float dz, float grosor, float altura, Color color) {
        return poste(dx, 0, dz, grosor, altura, color);
    }

    public Pincel poste(float dx, float dy, float dz, float grosor, float altura, Color color) {
        return caja(dx, dy + altura / 2f, dz, grosor, altura, grosor, color);
    }

    /**
     * Dibuja una rampa con inclinación yaw y pitch.
     */
    public Pincel rampa(float dx, float dy, float dz, float sx, float sy, float sz, Color color, float yaw, float pitch) {
        render.cajaRampa(ox + dx, oy + dy, oz + dz, sx, sy, sz, color.r(), color.g(), color.b(), anguloBase + yaw, pitch);
        return this;
    }

    /**
     * Dibuja un cilindro aproximado suavemente con prismas cruzados.
     */
    public Pincel cilindro(float dx, float dy, float dz, float radio, float altura, Color color) {
        float diametro = radio * 2f;
        caja(dx, dy + altura / 2f, dz, diametro, altura, diametro * 0.92f, color, 0);
        caja(dx, dy + altura / 2f, dz, diametro * 0.92f, altura, diametro, color, (float) Math.toRadians(45));
        return this;
    }

    // ==================== CONSTRUCTORES URBANOS SEMÁNTICOS ====================

    /**
     * Dibuja un árbol completo con tronco y copa frondosa.
     */
    public Pincel arbol(float dx, float dz) {
        return arbol(dx, dz, 1.0f);
    }

    public Pincel arbol(float dx, float dz, float escala) {
        // Tronco
        poste(dx, 0, dz, 0.4f * escala, 1.5f * escala, Color.TRONCO);
        // Copa baja
        caja(dx, 2.5f * escala, dz, 2.5f * escala, 1.5f * escala, 2.5f * escala, Color.HOJAS);
        // Copa alta
        caja(dx, 3.5f * escala, dz, 1.5f * escala, 1.0f * escala, 1.5f * escala, Color.HOJAS_CLARAS);
        return this;
    }

    /**
     * Dibuja un banco público de parque con asiento y espaldar de madera.
     */
    public Pincel banco(float dx, float dz) {
        // Asiento
        caja(dx, 0.5f, dz, 1.2f, 0.2f, 0.5f, Color.MADERA);
        // Espaldar
        caja(dx, 0.8f, dz - 0.2f, 1.2f, 0.5f, 0.1f, Color.MADERA);
        // Patas de metal
        poste(dx - 0.5f, 0, dz, 0.08f, 0.4f, Color.METAL);
        poste(dx + 0.5f, 0, dz, 0.08f, 0.4f, Color.METAL);
        return this;
    }

    /**
     * Dibuja una farola de alumbrado público con poste y bombilla brillante.
     */
    public Pincel farola(float dx, float dz, float altura) {
        // Base de concreto
        caja(dx, 0.2f, dz, 0.5f, 0.4f, 0.5f, Color.METAL_OSCURO);
        // Poste metálico
        poste(dx, 0.4f, dz, 0.2f, altura - 0.4f, Color.METAL);
        // Foco emisor de luz
        conBrillo(() -> {
            caja(dx, altura, dz, 0.6f, 0.3f, 0.6f, Color.LUZ_FAROLA);
        });
        return this;
    }

    public Pincel farola(float dx, float dz) {
        return farola(dx, dz, 5.0f);
    }

    /**
     * Dibuja una acera urbana rectangular.
     */
    public Pincel acera(float dx, float dz, float ancho, float largo) {
        return caja(dx, 0.15f, dz, ancho, 0.3f, largo, Color.ACERA);
    }

    /**
     * Dibuja una ventana rectangular que brilla de noche.
     */
    public Pincel ventana(float dx, float dy, float dz, float ancho, float alto, Color color) {
        conBrilloDeNoche(() -> {
            caja(dx, dy, dz, ancho, alto, 0.1f, color);
        });
        return this;
    }

    /**
     * Dibuja un semáforo completo con su poste, caja y luces sincronizadas.
     * @param estado 0 = Rojo, 1 = Verde, 2 = Amarillo
     */
    public Pincel semaforo(float dx, float dz, int estado) {
        // Poste
        poste(dx, 0, dz, 0.2f, 4.0f, Color.METAL);
        // Caja negra
        caja(dx, 4.5f, dz, 0.6f, 1.5f, 0.6f, Color.METAL_OSCURO);
        
        // Luces frontal y trasera
        conBrillo(() -> {
            Color cRojo = (estado == 0) ? Color.SEMAFORO_ROJO : Color.LUZ_APAGADA;
            Color cAmarillo = (estado == 2) ? Color.SEMAFORO_AMARILLO : Color.LUZ_APAGADA;
            Color cVerde = (estado == 1) ? Color.SEMAFORO_VERDE : Color.LUZ_APAGADA;

            caja(dx, 5.0f, dz + 0.35f, 0.4f, 0.3f, 0.1f, cRojo);
            caja(dx, 5.0f, dz - 0.35f, 0.4f, 0.3f, 0.1f, cRojo);

            caja(dx, 4.5f, dz + 0.35f, 0.4f, 0.3f, 0.1f, cAmarillo);
            caja(dx, 4.5f, dz - 0.35f, 0.4f, 0.3f, 0.1f, cAmarillo);

            caja(dx, 4.0f, dz + 0.35f, 0.4f, 0.3f, 0.1f, cVerde);
            caja(dx, 4.0f, dz - 0.35f, 0.4f, 0.3f, 0.1f, cVerde);
        });
        return this;
    }

    // ==================== FLUENT BUILDER ====================

    /**
     * Constructor fluido tipo DSL para construir figuras paso a paso.
     * Ejemplo:
     * pincel.figura().caja(2, 4, 2).en(0, 2, 0).color(Color.ROJO).pintar();
     */
    public ConstructorFigura figura() {
        return new ConstructorFigura(this);
    }

    public static class ConstructorFigura {
        private final Pincel p;
        private float fx = 0, fy = 0, fz = 0;
        private float fsx = 1, fsy = 1, fsz = 1;
        private Color fColor = Color.BLANCO;
        private float fRot = 0;
        private boolean fBrillante = false;
        private boolean fBrilloNoche = false;

        ConstructorFigura(Pincel p) {
            this.p = p;
        }

        public ConstructorFigura caja(float ancho, float alto, float profundidad) {
            this.fsx = ancho;
            this.fsy = alto;
            this.fsz = profundidad;
            return this;
        }

        public ConstructorFigura cubo(float lado) {
            return caja(lado, lado, lado);
        }

        public ConstructorFigura en(float x, float y, float z) {
            this.fx = x;
            this.fy = y;
            this.fz = z;
            return this;
        }

        public ConstructorFigura color(Color color) {
            this.fColor = color;
            return this;
        }

        public ConstructorFigura rotar(float angulo) {
            this.fRot = angulo;
            return this;
        }

        public ConstructorFigura brillar() {
            this.fBrillante = true;
            return this;
        }

        public ConstructorFigura brillarDeNoche() {
            this.fBrilloNoche = true;
            return this;
        }

        public Pincel pintar() {
            if (fBrillante) p.brillar();
            else if (fBrilloNoche) p.brillarDeNoche();

            p.caja(fx, fy, fz, fsx, fsy, fsz, fColor, fRot);

            if (fBrillante || fBrilloNoche) p.normal();
            return p;
        }
    }
}
