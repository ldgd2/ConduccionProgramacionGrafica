package com.graphics.core;

import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL33.*;

public class Renderizador {
    private int programa;
    private int vao;
    private int vbo;
    private final Map<String, Integer> uniforms = new HashMap<>();

    public void inicializar() {
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        crearPrograma();
        crearCubo();
    }

    public void usarPrograma() {
        glUseProgram(programa);
        glBindVertexArray(vao);
    }

    // ==================== CONTROL DE ESCENA Y PANTALLA (SIN OPENGL) ====================

    /**
     * Prepara el lienzo para un nuevo fotograma: ajusta la resolución y limpia la pantalla
     * con el color del cielo especificado.
     */
    public void prepararEscena(int ancho, int alto, Color colorCielo) {
        glViewport(0, 0, ancho, alto);
        glClearColor(colorCielo.r(), colorCielo.g(), colorCielo.b(), 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        usarPrograma();
        entero("uMapa", 0);
        decimal("uAspecto", (float) ancho / Math.max(1, alto));
    }

    /**
     * Posiciona la cámara en el mundo 3D y define hacia dónde está mirando.
     */
    public void configurarCamara(float ojoX, float ojoY, float ojoZ, float objetivoX, float objetivoY, float objetivoZ) {
        vector("uOjo", ojoX, ojoY, ojoZ);
        vector("uObjetivo", objetivoX, objetivoY, objetivoZ);
    }

    /**
     * Actualiza el reloj interno para efectos continuos como el oleaje del agua.
     */
    public void configurarTiempo(float segundos) {
        decimal("uTiempo", segundos);
    }

    /**
     * Configura la iluminación global del mundo (Sol, Luna y bóveda celeste).
     */
    public void configurarIluminacionGlobal(float faseDia, float anguloSol, Color colorSol, Color colorCielo) {
        decimal("uDia", faseDia);
        vector("uDirSol", (float) Math.cos(anguloSol), (float) Math.sin(anguloSol), -0.5f);
        vector("uDirLuna", (float) Math.cos(anguloSol + Math.PI), (float) Math.sin(anguloSol + Math.PI), -0.5f);
        vector("uColorSol", colorSol.r(), colorSol.g(), colorSol.b());
        vector("uColorCielo", colorCielo.r(), colorCielo.g(), colorCielo.b());
        entero("uEmitirLuz", 0);
    }

    /**
     * Dibuja los astros (Sol brillante y Luna) orbitando alrededor de la cámara.
     */
    public void dibujarAstros(float ojoX, float ojoY, float ojoZ, float anguloSol, Color colorSol) {
        float radioCielo = 200.0f;
        float solX = ojoX + radioCielo * (float) Math.cos(anguloSol);
        float solY = ojoY + radioCielo * (float) Math.sin(anguloSol);
        float solZ = ojoZ - 100.0f;

        float lunaX = ojoX + radioCielo * (float) Math.cos(anguloSol + Math.PI);
        float lunaY = ojoY + radioCielo * (float) Math.sin(anguloSol + Math.PI);
        float lunaZ = ojoZ - 100.0f;

        brillar();
        if (solY > -20.0f) {
            cubo(solX, solY, solZ, 25.0f, colorSol);
        }
        if (lunaY > -20.0f) {
            cubo(lunaX, lunaY, lunaZ, 18.0f, Color.rgb(0.8f, 0.8f, 0.9f));
        }
        luzNormal();
    }

    /**
     * Configura los focos y faros delanteros/traseros de un vehículo (jugador o NPC).
     */
    public void configurarLuzVehiculo(int indice, float x, float y, float z, float dirX, float dirZ, boolean encendidos) {
        if (indice < 0 || indice >= 4) return;
        vector("uPosAutos[" + indice + "]", x, y, z);
        vector("uDirAutos[" + indice + "]", dirX, 0.0f, dirZ);
        decimal("uFarosAutos[" + indice + "]", encendidos ? 1.0f : 0.0f);
    }

    /**
     * Pasa las posiciones de las farolas del mapa al sistema de iluminación.
     */
    public void configurarFarolas(float[] posiciones) {
        if (posiciones.length == 96) {
            vectorArray("uFarolas", posiciones);
        } else {
            float[] buffer = new float[96];
            System.arraycopy(posiciones, 0, buffer, 0, Math.min(posiciones.length, 96));
            vectorArray("uFarolas", buffer);
        }
    }

    /**
     * Dibuja una sub-vista aérea (minimapa) en la esquina superior sin requerir llamadas directas a OpenGL.
     */
    public void dibujarMinimapa(int pantallaAncho, int pantallaAlto, Runnable contenido) {
        int sizeMini = pantallaAlto / 3;
        int miniX = pantallaAncho - sizeMini - 20;
        int miniY = pantallaAlto - sizeMini - 20;

        glViewport(miniX, miniY, sizeMini, sizeMini);
        glClear(GL_DEPTH_BUFFER_BIT);

        entero("uMapa", 1);
        decimal("uAspecto", 1.0f);
        decimal("uDia", 1.0f);

        // Apagar faros temporalmente para el mapa cenital
        for (int i = 0; i < 4; i++) {
            decimal("uFarosAutos[" + i + "]", 0.0f);
        }

        contenido.run();

        entero("uMapa", 0);
        glViewport(0, 0, pantallaAncho, pantallaAlto);
    }

    /**
     * Dibuja un marcador cúbico llamativo en el minimapa (ej: objetivo o destino).
     */
    public void marcadorMinimapa(float x, float y, float z, Color color) {
        brillar();
        caja(x, y, z, 4.0f, 4.0f, 4.0f, color);
        luzNormal();
    }

    /**
     * Dibuja la interfaz de usuario en dos dimensiones (HUD) en la pantalla.
     */
    public void dibujarHUD(int texturaId, int pantallaAncho, int pantallaAlto, float hudAncho, float hudAlto) {
        glViewport(0, 0, pantallaAncho, pantallaAlto);
        entero("uHUD", 1);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, texturaId);
        entero("texHUD", 0);

        float hudAspect = hudAncho / hudAlto;
        float screenAspect = (float) pantallaAncho / Math.max(1, pantallaAlto);
        float sizeY = 0.4f;
        float sizeX = sizeY * hudAspect / screenAspect;

        vector("uOjo", 0, 0, 0);
        caja(-1.0f + sizeX / 2 + 0.05f, -1.0f + sizeY / 2 + 0.05f, 0, sizeX, sizeY, 0.001f, Color.BLANCO);

        entero("uHUD", 0);
    }


    public void dibujarHUDCentrado(int texturaId, int pantallaAncho, int pantallaAlto, float hudAncho, float hudAlto) {
        glViewport(0, 0, pantallaAncho, pantallaAlto);
        entero("uHUD", 1);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, texturaId);
        entero("texHUD", 0);

        float hudAspect = hudAncho / hudAlto;
        float screenAspect = (float) pantallaAncho / Math.max(1, pantallaAlto);
        float sizeY = 0.75f;
        float sizeX = sizeY * hudAspect / screenAspect;

        vector("uOjo", 0, 0, 0);
        caja(0, 0, 0, sizeX, sizeY, 0.001f, Color.BLANCO);

        entero("uHUD", 0);
    }

    // ==================== PRIMITIVAS 3D (CAJAS Y RAMPAS) ====================
    public void caja(float x, float y, float z, float sx, float sy, float sz, float r, float g, float b) {
        cajaGirada(x, y, z, sx, sy, sz, r, g, b, 0);
    }

    public void caja(float x, float y, float z, float sx, float sy, float sz, Color color) {
        cajaGirada(x, y, z, sx, sy, sz, color.r(), color.g(), color.b(), 0);
    }

    public void cajaGirada(float x, float y, float z, float sx, float sy, float sz, float r, float g, float b, float angulo) {
        cajaRampa(x, y, z, sx, sy, sz, r, g, b, angulo, 0);
    }

    public void cajaGirada(float x, float y, float z, float sx, float sy, float sz, Color color, float angulo) {
        cajaRampa(x, y, z, sx, sy, sz, color.r(), color.g(), color.b(), angulo, 0);
    }

    public void cajaRampa(float x, float y, float z, float sx, float sy, float sz, float r, float g, float b, float yaw, float pitch) {
        vector("uPos", x, y, z);
        vector("uEscala", sx, sy, sz);
        vector("uColor", r, g, b);
        decimal("uGiro", yaw);
        decimal("uPitch", pitch);
        glDrawArrays(GL_TRIANGLES, 0, 36);
    }

    public void cajaRampa(float x, float y, float z, float sx, float sy, float sz, Color color, float yaw, float pitch) {
        cajaRampa(x, y, z, sx, sy, sz, color.r(), color.g(), color.b(), yaw, pitch);
    }

    public void cubo(float x, float y, float z, float lado, Color color) {
        caja(x, y, z, lado, lado, lado, color);
    }


    public void brillar() {
        entero("uEmitirLuz", 1);
    }

    public void brillarDeNoche() {
        entero("uEmitirLuz", 2);
    }

    public void luzNormal() {
        entero("uEmitirLuz", 0);
    }


    public Pincel pincel() {
        return new Pincel(this);
    }

    public Pincel en(float x, float y, float z) {
        return new Pincel(this, x, y, z);
    }

    public Pincel en(com.graphics.modelos.Objeto objeto) {
        return new Pincel(this, objeto);
    }


    
    public int crearMalla(float[] vertices) {
        int m_vao = glGenVertexArrays();
        glBindVertexArray(m_vao);
        int m_vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, m_vbo);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
        int bytesPorVertice = 6 * Float.BYTES;
        glVertexAttribPointer(0, 3, GL_FLOAT, false, bytesPorVertice, 0L);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, bytesPorVertice, 3L * Float.BYTES);
        glEnableVertexAttribArray(1);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(vao); // Restaurar VAO del cubo
        return m_vao;
    }

    public void dibujarMalla(int m_vao, int numVertices, float r, float g, float b) {
        vector("uPos", 0, 0, 0);
        vector("uEscala", 1, 1, 1);
        vector("uColor", r, g, b);
        decimal("uGiro", 0);
        decimal("uPitch", 0);
        
        glBindVertexArray(m_vao);
        glDrawArrays(GL_TRIANGLES, 0, numVertices);
        glBindVertexArray(vao); // Volver al VAO del cubo por defecto
    }

    public void dibujarMalla(int m_vao, int numVertices, Color color) {
        dibujarMalla(m_vao, numVertices, color.r(), color.g(), color.b());
    }

    public int uniform(String nombre) {
        if (!uniforms.containsKey(nombre)) {
            int ubicacion = glGetUniformLocation(programa, nombre);
            uniforms.put(nombre, ubicacion);
        }
        return uniforms.get(nombre);
    }

    public void vector(String nombre, float x, float y, float z) {
        glUniform3f(uniform(nombre), x, y, z);
    }

    public void decimal(String nombre, float valor) {
        glUniform1f(uniform(nombre), valor);
    }

    public void entero(String nombre, int valor) {
        glUniform1i(uniform(nombre), valor);
    }

    public void vectorArray(String nombre, float[] valores) {
        glUniform3fv(uniform(nombre), valores);
    }

    public int cargarTextura(String path) {
        try {
            BufferedImage img = ImageIO.read(new File(path));
            int width = img.getWidth();
            int height = img.getHeight();

            int[] pixels = new int[width * height];
            img.getRGB(0, 0, width, height, pixels, 0, width);

            ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * 4);
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int pixel = pixels[y * width + x];
                    buffer.put((byte) ((pixel >> 16) & 0xFF)); // Red
                    buffer.put((byte) ((pixel >> 8) & 0xFF));  // Green
                    buffer.put((byte) (pixel & 0xFF));         // Blue
                    buffer.put((byte) ((pixel >> 24) & 0xFF)); // Alpha
                }
            }
            buffer.flip();

            int textureID = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, textureID);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);

            return textureID;
        } catch (Exception e) {
            System.err.println("Error cargando textura: " + path);
            e.printStackTrace();
            return 0;
        }
    }

    private String vertexShader() {
        return """
            #version 330 core
            layout (location = 0) in vec3 aPos;
            layout (location = 1) in vec3 aNormal;
            uniform vec3 uPos;
            uniform vec3 uEscala;
            uniform vec3 uOjo;
            uniform vec3 uObjetivo;
            uniform float uGiro;
            uniform float uPitch;
            uniform float uAspecto;
            uniform int uMapa;
            uniform int uHUD;
            uniform int uEsAgua;
            uniform float uTiempo;
            out vec3 vMundo;
            out vec3 vNormal;
            out vec2 vHUD_UV;

            void main() {
                // Rotacion en Y (Yaw)
                float cY = cos(uGiro);
                float sY = sin(uGiro);
                mat3 giroY = mat3(
                    cY, 0.0, -sY,
                    0.0, 1.0, 0.0,
                    sY, 0.0, cY
                );
                
                // Rotacion en X (Pitch)
                float cX = cos(uPitch);
                float sX = sin(uPitch);
                mat3 giroX = mat3(
                    1.0, 0.0, 0.0,
                    0.0, cX, sX,
                    0.0, -sX, cX
                );
                
                mat3 rotTotal = giroY * giroX;
                vMundo = rotTotal * (aPos * uEscala) + uPos;
                
                if (uEsAgua == 1) {
                    vMundo.y += sin(vMundo.x * 2.0 + uTiempo * 3.0) * 0.15 + cos(vMundo.z * 1.5 + uTiempo * 2.0) * 0.15;
                }
                
                vNormal = normalize(rotTotal * (aNormal / uEscala));

                if (uHUD == 1) {
                    gl_Position = vec4(aPos.x * uEscala.x + uPos.x, aPos.y * uEscala.y + uPos.y, 0.0, 1.0);
                    vHUD_UV = vec2(aPos.x + 0.5, 0.5 - aPos.y);
                } else if (uMapa == 1) {
                    float pantallaX = vMundo.x / 115.0;
                    float pantallaY = -vMundo.z / 115.0;
                    float profundidad = -vMundo.y / 100.0;
                    gl_Position = vec4(pantallaX, pantallaY, profundidad, 1.0);
                } else {
                    vec3 frente = normalize(uObjetivo - uOjo);
                    vec3 derecha = normalize(cross(frente, vec3(0.0, 1.0, 0.0)));
                    vec3 arriba = cross(derecha, frente);
                    vec3 diferencia = vMundo - uOjo;
                    float vistaX = dot(diferencia, derecha);
                    float vistaY = dot(diferencia, arriba);
                    float vistaZ = -dot(diferencia, frente);
                    float factor = 1.0 / tan(radians(55.0) * 0.5);
                    float cerca = 0.1;
                    float lejos = 250.0;
                    float clipX = vistaX * factor / uAspecto;
                    float clipY = vistaY * factor;
                    float clipZ = (lejos + cerca) / (cerca - lejos) * vistaZ;
                    clipZ += 2.0 * lejos * cerca / (cerca - lejos);
                    gl_Position = vec4(clipX, clipY, clipZ, -vistaZ);
                }
            }
            """;
    }

    private String fragmentShader() {
        return """
            #version 330 core
            in vec3 vMundo;
            in vec3 vNormal;
            in vec2 vHUD_UV;

            uniform vec3 uColor;
            uniform float uDia;
            uniform vec3 uPosAutos[4];
            uniform vec3 uDirAutos[4];
            uniform float uFarosAutos[4];
            uniform vec3 uFarolas[32];
            uniform vec3 uOjo;
            uniform int uEmitirLuz;
            uniform int uHUD;
            uniform sampler2D texHUD;
            
            // Nuevas variables de iluminacion dinamica
            uniform vec3 uDirSol;
            uniform vec3 uDirLuna;
            uniform vec3 uColorSol;
            uniform vec3 uColorCielo;

            out vec4 color;

            void main() {
                if (uHUD == 1) {
                    vec4 cHUD = texture(texHUD, vHUD_UV);
                    if (cHUD.a < 0.1) discard; // Transparencia
                    color = cHUD;
                    return;
                }

                if (uEmitirLuz == 1) {
                    color = vec4(uColor, 1.0);
                    return;
                } else if (uEmitirLuz == 2) {
                    float resplandor = smoothstep(0.6, 0.1, uDia); // brilla en la noche
                    vec3 colorBase = uColor * 0.1; // vidrio oscuro de dia
                    vec3 emisivo = uColor * resplandor * 1.2;
                    color = vec4(colorBase + emisivo, 1.0);
                    return;
                }

                vec3 n = normalize(vNormal);
                
                // Ambiente
                vec3 luzAmbiental = uColorCielo * mix(0.15, 0.6, uDia) + vec3(0.05);

                // Sol (Direccional)
                float difusaSol = max(dot(n, normalize(uDirSol)), 0.0);
                vec3 luzSol = uColorSol * difusaSol * uDia;
                
                // Luna
                float difusaLuna = max(dot(n, normalize(uDirLuna)), 0.0);
                vec3 luzLuna = vec3(0.2, 0.25, 0.4) * difusaLuna * (1.0 - uDia);

                // Farolas (Puntuales en calles, avenidas e intersecciones)
                vec3 luzFarolas = vec3(0.0);
                float factorNocheFarola = smoothstep(0.55, 0.15, uDia); // Totalmente apagadas de día, encendidas de noche
                if (factorNocheFarola > 0.001) {
                    for(int i = 0; i < 32; i++) {
                        vec3 posFarola = uFarolas[i];
                        if (posFarola.y > -10.0) {
                            vec3 delta = posFarola - vMundo;
                            float distancia = length(delta);
                            if (distancia < 45.0) {
                                float atenuacion = 1.0 / (1.0 + 0.035 * distancia + 0.002 * distancia * distancia);
                                vec3 dirLuz = normalize(delta);
                                float difusa = max(dot(n, dirLuz), 0.0);
                                float rebote = clamp(1.0 - distancia / 25.0, 0.0, 1.0) * 0.10;
                                vec3 colorFarola = vec3(1.0, 0.85, 0.62); // Iluminación cálida y nítida sin saturación
                                luzFarolas += colorFarola * (difusa + rebote) * atenuacion * factorNocheFarola;
                            }
                        }
                    }
                }

                // Focos de los autos (Spotlight doble delantero + luces traseras rojas)
                vec3 luzAuto = vec3(0.0);
                for(int i = 0; i < 4; i++) {
                    if (uFarosAutos[i] > 0.5) {
                        vec3 posA = uPosAutos[i];
                        vec3 dirA = normalize(uDirAutos[i]);
                        vec3 derechaA = vec3(-dirA.z, 0.0, dirA.x);
                        
                        // Foco delantero izquierdo
                        vec3 posFocoIzq = posA + vec3(0.0, 0.6, 0.0) + dirA * 2.0 - derechaA * 0.7;
                        vec3 dirLuzIzq = normalize(posFocoIzq - vMundo);
                        float distIzq = length(posFocoIzq - vMundo);
                        float ateIzq = 1.0 / (1.0 + 0.01 * distIzq + 0.002 * distIzq * distIzq);
                        float thetaIzq = dot(-dirLuzIzq, dirA);
                        float intIzq = clamp((thetaIzq - 0.75) / 0.15, 0.0, 1.0);
                        float difIzq = max(dot(n, dirLuzIzq), 0.0);
                        luzAuto += vec3(1.5, 1.4, 1.3) * difIzq * ateIzq * intIzq;

                        // Foco delantero derecho
                        vec3 posFocoDer = posA + vec3(0.0, 0.6, 0.0) + dirA * 2.0 + derechaA * 0.7;
                        vec3 dirLuzDer = normalize(posFocoDer - vMundo);
                        float distDer = length(posFocoDer - vMundo);
                        float ateDer = 1.0 / (1.0 + 0.01 * distDer + 0.002 * distDer * distDer);
                        float thetaDer = dot(-dirLuzDer, dirA);
                        float intDer = clamp((thetaDer - 0.75) / 0.15, 0.0, 1.0);
                        float difDer = max(dot(n, dirLuzDer), 0.0);
                        luzAuto += vec3(1.5, 1.4, 1.3) * difDer * ateDer * intDer;
                        
                        // Faros traseros rojos
                        vec3 posTrasIzq = posA + vec3(0.0, 0.6, 0.0) - dirA * 2.0 - derechaA * 0.7;
                        vec3 dirTrasIzq = normalize(posTrasIzq - vMundo);
                        float distTrasIzq = length(posTrasIzq - vMundo);
                        float ateTrasIzq = 1.0 / (1.0 + 0.05 * distTrasIzq + 0.01 * distTrasIzq * distTrasIzq);
                        float thetaTrasIzq = dot(-dirTrasIzq, -dirA);
                        float intTrasIzq = clamp((thetaTrasIzq - 0.5) / 0.3, 0.0, 1.0);
                        luzAuto += vec3(1.0, 0.1, 0.0) * max(dot(n, dirTrasIzq), 0.0) * ateTrasIzq * intTrasIzq * 0.6;
                        
                        vec3 posTrasDer = posA + vec3(0.0, 0.6, 0.0) - dirA * 2.0 + derechaA * 0.7;
                        vec3 dirTrasDer = normalize(posTrasDer - vMundo);
                        float distTrasDer = length(posTrasDer - vMundo);
                        float ateTrasDer = 1.0 / (1.0 + 0.05 * distTrasDer + 0.01 * distTrasDer * distTrasDer);
                        float thetaTrasDer = dot(-dirTrasDer, -dirA);
                        float intTrasDer = clamp((thetaTrasDer - 0.5) / 0.3, 0.0, 1.0);
                        luzAuto += vec3(1.0, 0.1, 0.0) * max(dot(n, dirTrasDer), 0.0) * ateTrasDer * intTrasDer * 0.6;
                    }
                }

                vec3 luzTotal = luzAmbiental + luzSol + luzLuna + luzFarolas + luzAuto;
                color = vec4(uColor * luzTotal, 1.0);
            }
            """;
    }

    private int compilar(int tipo, String fuente) {
        int shader = glCreateShader(tipo);
        glShaderSource(shader, fuente);
        glCompileShader(shader);
        if (glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE) {
            String error = glGetShaderInfoLog(shader);
            glDeleteShader(shader);
            throw new IllegalStateException(error);
        }
        return shader;
    }

    private void crearPrograma() {
        int shaderVertices = compilar(GL_VERTEX_SHADER, vertexShader());
        int shaderFragmentos = 0;
        try {
            shaderFragmentos = compilar(GL_FRAGMENT_SHADER, fragmentShader());
            programa = glCreateProgram();
            glAttachShader(programa, shaderVertices);
            glAttachShader(programa, shaderFragmentos);
            glLinkProgram(programa);
            if (glGetProgrami(programa, GL_LINK_STATUS) == GL_FALSE) {
                throw new IllegalStateException(glGetProgramInfoLog(programa));
            }
        } finally {
            glDeleteShader(shaderVertices);
            if (shaderFragmentos != 0) {
                glDeleteShader(shaderFragmentos);
            }
        }
    }

    private void crearCubo() {
        float[] vertices = {
            -0.5f, -0.5f, 0.5f, 0, 0, 1, 0.5f, -0.5f, 0.5f, 0, 0, 1, 0.5f, 0.5f, 0.5f, 0, 0, 1, 0.5f, 0.5f, 0.5f, 0, 0, 1, -0.5f, 0.5f, 0.5f, 0, 0, 1, -0.5f, -0.5f, 0.5f, 0, 0, 1,
            -0.5f, -0.5f, -0.5f, 0, 0, -1, -0.5f, 0.5f, -0.5f, 0, 0, -1, 0.5f, 0.5f, -0.5f, 0, 0, -1, 0.5f, 0.5f, -0.5f, 0, 0, -1, 0.5f, -0.5f, -0.5f, 0, 0, -1, -0.5f, -0.5f, -0.5f, 0, 0, -1,
            -0.5f, 0.5f, 0.5f, -1, 0, 0, -0.5f, 0.5f, -0.5f, -1, 0, 0, -0.5f, -0.5f, -0.5f, -1, 0, 0, -0.5f, -0.5f, -0.5f, -1, 0, 0, -0.5f, -0.5f, 0.5f, -1, 0, 0, -0.5f, 0.5f, 0.5f, -1, 0, 0,
            0.5f, 0.5f, 0.5f, 1, 0, 0, 0.5f, -0.5f, 0.5f, 1, 0, 0, 0.5f, -0.5f, -0.5f, 1, 0, 0, 0.5f, -0.5f, -0.5f, 1, 0, 0, 0.5f, 0.5f, -0.5f, 1, 0, 0, 0.5f, 0.5f, 0.5f, 1, 0, 0,
            -0.5f, 0.5f, -0.5f, 0, 1, 0, -0.5f, 0.5f, 0.5f, 0, 1, 0, 0.5f, 0.5f, 0.5f, 0, 1, 0, 0.5f, 0.5f, 0.5f, 0, 1, 0, 0.5f, 0.5f, -0.5f, 0, 1, 0, -0.5f, 0.5f, -0.5f, 0, 1, 0,
            -0.5f, -0.5f, -0.5f, 0, -1, 0, 0.5f, -0.5f, -0.5f, 0, -1, 0, 0.5f, -0.5f, 0.5f, 0, -1, 0, 0.5f, -0.5f, 0.5f, 0, -1, 0, -0.5f, -0.5f, 0.5f, 0, -1, 0, -0.5f, -0.5f, -0.5f, 0, -1, 0
        };
        vao = glGenVertexArrays();
        glBindVertexArray(vao);
        vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
        int bytesPorVertice = 6 * Float.BYTES;
        glVertexAttribPointer(0, 3, GL_FLOAT, false, bytesPorVertice, 0L);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, bytesPorVertice, 3L * Float.BYTES);
        glEnableVertexAttribArray(1);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    public void limpiar() {
        if (programa != 0) glDeleteProgram(programa);
        if (vbo != 0) glDeleteBuffers(vbo);
        if (vao != 0) glDeleteVertexArrays(vao);
    }
}
