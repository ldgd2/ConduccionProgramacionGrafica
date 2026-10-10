package com.graphics.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_RGBA8;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;

import com.graphics.core.Renderizador;
import com.graphics.ui.components.BarraVidaUI;
import com.graphics.ui.components.InfoTextoUI;
import com.graphics.ui.components.VelocimetroUI;

public class PanelUI {
    private int texturaId = 0;
    private int ancho = 300;
    private int alto = 180;
    
    private int lastVelocidad = -1;
    private boolean lastFaros = false;
    private String lastZona = "";
    private float lastHora = -1;
    private float lastVida = -1;
    
    // Componentes Modulares
    private BarraVidaUI barraVida;
    private InfoTextoUI infoTexto;
    private VelocimetroUI velocimetro;

    public PanelUI() {
        texturaId = glGenTextures();
        
        // Inicializar componentes
        barraVida = new BarraVidaUI(18, 150);
        infoTexto = new InfoTextoUI();
        velocimetro = new VelocimetroUI(220, 90, 50);
    }
    
    public void reiniciar() {
        velocimetro.reiniciar();
    }

    public void actualizar(int velocidad, boolean faros, float hora, String zona, float vida) {
        // Verificar si hubo cambios para evitar redibujar sin necesidad, a menos que el medidor este roto y animandose
        if (velocidad == lastVelocidad && faros == lastFaros && zona.equals(lastZona) 
            && Math.abs(hora - lastHora) < 0.1f && Math.abs(vida - lastVida) < 1.0f 
            && !velocimetro.isRoto()) {
            return; 
        }
        
        lastVelocidad = velocidad;
        lastFaros = faros;
        lastZona = zona;
        lastHora = hora;
        lastVida = vida;

        // Actualizar estados en los componentes
        infoTexto.actualizarEstado(zona, faros, hora);
        velocimetro.setVelocidad(velocidad);
        barraVida.setVida(vida);

        // Crear imagen
        BufferedImage img = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        
        // Anti-aliasing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        // Fondo semi-transparente del Panel
        g2d.setColor(new Color(20, 20, 30, 180));
        g2d.fillRoundRect(0, 0, ancho, alto, 20, 20);
        g2d.setColor(new Color(255, 105, 180, 200));
        g2d.drawRoundRect(0, 0, ancho - 1, alto - 1, 20, 20);
        g2d.drawRoundRect(1, 1, ancho - 3, alto - 3, 20, 20);

        // Dibujar cada componente (Patron Composicion / Hub)
        infoTexto.dibujar(g2d);
        velocimetro.dibujar(g2d);
        barraVida.dibujar(g2d);
        
        g2d.dispose();

        // Convertir a ByteBuffer
        int[] pixels = new int[ancho * alto];
        img.getRGB(0, 0, ancho, alto, pixels, 0, ancho);

        ByteBuffer buffer = BufferUtils.createByteBuffer(ancho * alto * 4);
        for (int y = 0; y < alto; y++) {
            for (int x = 0; x < ancho; x++) {
                int pixel = pixels[y * ancho + x];
                buffer.put((byte) ((pixel >> 16) & 0xFF)); // Red
                buffer.put((byte) ((pixel >> 8) & 0xFF));  // Green
                buffer.put((byte) (pixel & 0xFF));         // Blue
                buffer.put((byte) ((pixel >> 24) & 0xFF)); // Alpha
            }
        }
        buffer.flip();

        // Subir a OpenGL
        glBindTexture(GL_TEXTURE_2D, texturaId);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, ancho, alto, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
    }

    public void dibujar(Renderizador renderizador, int pantallaAncho, int pantallaAlto) {
        renderizador.dibujarHUD(texturaId, pantallaAncho, pantallaAlto, ancho, alto);
    }
}
