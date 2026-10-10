package com.graphics.modelos;

import com.graphics.core.Color;
import com.graphics.core.Pincel;
import com.graphics.core.Renderizador;

public abstract class Objeto {
    // ==================== PALETA DE COLORES DIRECTA ====================
    // Permite usar tanto color.LUZ_CALIDA, Color.LUZ_CALIDA, o directamente LUZ_CALIDA
    public static final Color color = Color.BLANCO;

    protected static final Color BLANCO = Color.BLANCO;
    protected static final Color NEGRO = Color.NEGRO;
    protected static final Color ROJO = Color.ROJO;
    protected static final Color VERDE = Color.VERDE;
    protected static final Color AZUL = Color.AZUL;
    protected static final Color AMARILLO = Color.AMARILLO;
    protected static final Color ORO = Color.ORO;
    protected static final Color GRIS = Color.GRIS;
    protected static final Color GRIS_CLARO = Color.GRIS_CLARO;
    protected static final Color GRIS_OSCURO = Color.GRIS_OSCURO;
    protected static final Color MAGENTA = Color.MAGENTA;

    protected static final Color ASFALTO = Color.ASFALTO;
    protected static final Color ACERA = Color.ACERA;
    protected static final Color CESPED = Color.CESPED;
    protected static final Color CESPED_CLARO = Color.CESPED_CLARO;
    protected static final Color HOJAS = Color.HOJAS;
    protected static final Color TRONCO = Color.TRONCO;
    protected static final Color MADERA = Color.MADERA;
    protected static final Color MADERA_OSCURA = Color.MADERA_OSCURA;
    protected static final Color LADRILLO = Color.LADRILLO;
    protected static final Color TECHO = Color.TECHO;
    protected static final Color VIDRIO = Color.VIDRIO;
    protected static final Color VIDRIO_NOCHE = Color.VIDRIO_NOCHE;
    protected static final Color NEON_CYAN = Color.NEON_CYAN;
    protected static final Color LUZ_CALIDA = Color.LUZ_CALIDA;
    protected static final Color LUZ_FAROLA = Color.LUZ_FAROLA;
    protected static final Color METAL = Color.METAL;
    protected static final Color METAL_OSCURO = Color.METAL_OSCURO;
    protected static final Color AGUA = Color.AGUA;

   
    protected static Color rgb(float r, float g, float b) { return Color.rgb(r, g, b); }
    protected static Color rgb(int r, int g, int b) { return Color.rgb(r, g, b); }
    protected static Color rgba(int r, int g, int b, int a) { return Color.rgba(r, g, b, a); }
    protected static Color hex(String codigoHex) { return Color.hex(codigoHex); }

    protected float x;
    protected float y = 0;
    protected float z;

    public Objeto(float x, float z) {
        this.x = x;
        this.z = z;
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public float getZ() { return z; }
    
    public void setX(float x) { this.x = x; }
    public void setZ(float z) { this.z = z; }

    public float getRadio() {
        return 0f;
    }

    public void actualizar(float deltaTime) {
    }

    /**
     * Dibuja el objeto delegando por defecto en dibujar(Pincel)
     */
    public void dibujar(Renderizador renderizador) {
        dibujar(renderizador.en(this));
    }

    public void dibujar(com.graphics.core.Pincel pincel) {

    }

    public com.graphics.core.Pincel pincel(Renderizador r) {
        return r.en(this);
    }


    public boolean esColisionable() {
        return false;
    }

    public float getRadioColisionX() {
        return 3.5f;
    }

    public float getRadioColisionZ() {
        return 3.5f;
    }

    public boolean colisiona(Objeto objeto) {
        if (!esColisionable()) return false;
        
        float rx = getRadioColisionX();
        float rz = getRadioColisionZ();
        float cercaX = Math.max(x - rx, Math.min(objeto.getX(), x + rx));
        float cercaZ = Math.max(z - rz, Math.min(objeto.getZ(), z + rz));
        float distanciaX = objeto.getX() - cercaX;
        float distanciaZ = objeto.getZ() - cercaZ;
        return (distanciaX * distanciaX + distanciaZ * distanciaZ) < (objeto.getRadio() * objeto.getRadio());
    }
}
