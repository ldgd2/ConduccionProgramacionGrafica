package com.graphics.core;

/**
 * Representa un color RGBA intuitivo para el sistema gráfico.
 * Permite definir colores en formato decimal (0-1), entero (0-255), hexadecimal (#RRGGBB)
 * e incluye una paleta urbana y de materiales lista para usar.
 */
public class Color {
    private final float r;
    private final float g;
    private final float b;
    private final float a;

    // ==================== PALETA BÁSICA ====================
    public static final Color BLANCO = new Color(1.0f, 1.0f, 1.0f);
    public static final Color NEGRO = new Color(0.0f, 0.0f, 0.0f);
    public static final Color GRIS = new Color(0.5f, 0.5f, 0.5f);
    public static final Color GRIS_CLARO = new Color(0.75f, 0.75f, 0.75f);
    public static final Color GRIS_OSCURO = new Color(0.2f, 0.2f, 0.2f);
    
    public static final Color ROJO = new Color(1.0f, 0.0f, 0.0f);
    public static final Color VERDE = new Color(0.0f, 1.0f, 0.0f);
    public static final Color AZUL = new Color(0.0f, 0.0f, 1.0f);
    public static final Color AMARILLO = new Color(1.0f, 1.0f, 0.0f);
    public static final Color CYAN = new Color(0.0f, 1.0f, 1.0f);
    public static final Color MAGENTA = new Color(1.0f, 0.0f, 1.0f);
    public static final Color NARANJA = new Color(1.0f, 0.5f, 0.0f);
    public static final Color VIOLETA = new Color(0.6f, 0.2f, 0.8f);
    public static final Color ORO = new Color(1.0f, 0.84f, 0.0f);

    // ==================== PALETA URBANA Y MATERIALES ====================
    public static final Color ASFALTO = new Color(0.16f, 0.19f, 0.23f);
    public static final Color ACERA = new Color(0.60f, 0.64f, 0.66f);
    public static final Color CESPED = new Color(0.15f, 0.40f, 0.20f);
    public static final Color CESPED_CLARO = new Color(0.20f, 0.50f, 0.25f);
    public static final Color HOJAS = new Color(0.10f, 0.50f, 0.20f);
    public static final Color HOJAS_CLARAS = new Color(0.15f, 0.60f, 0.25f);
    public static final Color TRONCO = new Color(0.40f, 0.25f, 0.15f);
    public static final Color MADERA = new Color(0.50f, 0.30f, 0.10f);
    public static final Color MADERA_OSCURA = new Color(0.35f, 0.20f, 0.08f);
    public static final Color LADRILLO = new Color(0.70f, 0.40f, 0.30f);
    public static final Color TECHO = new Color(0.40f, 0.20f, 0.10f);
    public static final Color VIDRIO = new Color(0.15f, 0.15f, 0.18f);
    public static final Color VIDRIO_NOCHE = new Color(0.70f, 0.90f, 1.0f);
    public static final Color AGUA = new Color(0.10f, 0.40f, 0.70f);
    public static final Color METAL = new Color(0.30f, 0.30f, 0.30f);
    public static final Color METAL_OSCURO = new Color(0.15f, 0.15f, 0.15f);
    public static final Color LLANTA = new Color(0.08f, 0.09f, 0.10f);

    // Luces y Neones
    public static final Color NEON_CYAN = new Color(0.0f, 0.8f, 1.0f);
    public static final Color NEON_ROSA = new Color(1.0f, 0.1f, 0.6f);
    public static final Color LUZ_CALIDA = new Color(1.0f, 0.9f, 0.5f);
    public static final Color LUZ_FAROLA = new Color(1.0f, 0.9f, 0.7f);
    public static final Color LUZ_FARO_AUTO = new Color(1.0f, 1.0f, 0.9f);
    public static final Color LUZ_FRENO = new Color(1.0f, 0.1f, 0.0f);
    
    // Semáforo
    public static final Color SEMAFORO_ROJO = new Color(1.0f, 0.0f, 0.0f);
    public static final Color SEMAFORO_AMARILLO = new Color(1.0f, 1.0f, 0.0f);
    public static final Color SEMAFORO_VERDE = new Color(0.0f, 1.0f, 0.0f);
    public static final Color LUZ_APAGADA = new Color(0.15f, 0.15f, 0.15f);

    // Constructores
    public Color(float r, float g, float b, float a) {
        this.r = clamp(r);
        this.g = clamp(g);
        this.b = clamp(b);
        this.a = clamp(a);
    }

    public Color(float r, float g, float b) {
        this(r, g, b, 1.0f);
    }

    public static Color rgb(float r, float g, float b) {
        return new Color(r, g, b);
    }

    public static Color rgb(int r, int g, int b) {
        return new Color(r / 255.0f, g / 255.0f, b / 255.0f);
    }

    public static Color rgba(int r, int g, int b, int a) {
        return new Color(r / 255.0f, g / 255.0f, b / 255.0f, a / 255.0f);
    }

    public static Color hex(String hex) {
        String limpio = hex.startsWith("#") ? hex.substring(1) : hex;
        if (limpio.length() == 6) {
            int r = Integer.parseInt(limpio.substring(0, 2), 16);
            int g = Integer.parseInt(limpio.substring(2, 4), 16);
            int b = Integer.parseInt(limpio.substring(4, 6), 16);
            return rgb(r, g, b);
        } else if (limpio.length() == 8) {
            int r = Integer.parseInt(limpio.substring(0, 2), 16);
            int g = Integer.parseInt(limpio.substring(2, 4), 16);
            int b = Integer.parseInt(limpio.substring(4, 6), 16);
            int a = Integer.parseInt(limpio.substring(6, 8), 16);
            return rgba(r, g, b, a);
        }
        return BLANCO;
    }

    public float r() { return r; }
    public float g() { return g; }
    public float b() { return b; }
    public float a() { return a; }

    public float getRojo() { return r; }
    public float getVerde() { return g; }
    public float getAzul() { return b; }
    public float getAlfa() { return a; }

    public Color aclarar(float factor) {
        return new Color(r + (1f - r) * factor, g + (1f - g) * factor, b + (1f - b) * factor, a);
    }

    public Color oscurecer(float factor) {
        return new Color(r * (1f - factor), g * (1f - factor), b * (1f - factor), a);
    }

    public Color mezclar(Color otro, float t) {
        return new Color(
            r + (otro.r - r) * t,
            g + (otro.g - g) * t,
            b + (otro.b - b) * t,
            a + (otro.a - a) * t
        );
    }

    private static float clamp(float v) {
        return Math.max(0.0f, Math.min(1.0f, v));
    }

    @Override
    public String toString() {
        return String.format("Color(r=%.2f, g=%.2f, b=%.2f, a=%.2f)", r, g, b, a);
    }
}
