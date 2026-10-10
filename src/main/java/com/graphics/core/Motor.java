package com.graphics.core;

import com.graphics.modelos.Ciudad;
import com.graphics.modelos.AutoModelo;
import com.graphics.conduccion.Auto;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import static org.lwjgl.glfw.GLFW.*;

public class Motor {
    protected long ventana;
    protected int ancho = 1100;
    protected int alto = 760;
    
    private Renderizador renderizador;
    private com.graphics.modelos.Entorno entornoActual;
    private com.graphics.modelos.Ciudad ciudad;
    private com.graphics.modelos.PistaCarrera pista;
    private com.graphics.modelos.Monte monte;
    
    private Auto auto;
    private AutoModelo autoModelo;
    private com.graphics.conduccion.AutoNPC[] npcs = new com.graphics.conduccion.AutoNPC[3];
    private AutoModelo[] npcModelos = new AutoModelo[3];
    private java.util.List<com.graphics.conduccion.Particula> particulas = new java.util.ArrayList<>();
    private boolean camaraAerea = false;
    private com.graphics.ui.PanelUI panelUI;

    // Variables para la cámara libre con el ratón
    private float camaraYaw = 180;
    private float camaraPitch = 15;
    private double lastMouseX = -1, lastMouseY = -1;
    
    // Iluminacion
    private boolean esDeDia = true;
    private boolean farosEncendidos = true;
    
    // UI
    private boolean mostrarMinimapa = true;
    
    // Ciclo del Dia
    private float tiempoCiclo = 0;
    private float duracionDiaCompleto = 60.0f; // 60 segundos por ciclo completo
    
    private float lerp(float a, float b, float f) {
        return a + f * (b - a);
    }

    public void run() {
        GLFWErrorCallback errores = GLFWErrorCallback.createPrint(System.err);
        errores.set();
        try {
            iniciar();
            loop();
        } finally {
            limpiar();
            glfwSetErrorCallback(null);
            errores.free();
        }
    }

    private void iniciar() {
        if (!glfwInit()) throw new IllegalStateException("No se pudo iniciar GLFW");
        
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
        
        ventana = glfwCreateWindow(ancho, alto, "Ciudad Conduccion - Refactor Modular", 0, 0);
        if (ventana == 0) throw new IllegalStateException("No se pudo crear la ventana OpenGL");
        
        glfwMakeContextCurrent(ventana);
        glfwSwapInterval(1);
        GL.createCapabilities();
        
        renderizador = new Renderizador();
        renderizador.inicializar();
        panelUI = new com.graphics.ui.PanelUI();
        
        ciudad = new com.graphics.modelos.Ciudad();
        pista = new com.graphics.modelos.PistaCarrera();
        monte = new com.graphics.modelos.Monte(renderizador);
        
        entornoActual = ciudad;
        
        auto = new Auto(entornoActual);
        autoModelo = new AutoModelo(auto);
        
        // Tráfico autónomo (Solo en ciudad por ahora, pero los instanciamos)
        npcs[0] = new com.graphics.conduccion.AutoNPC(entornoActual, -80, -20, (float) Math.PI / 2); // Calle izquierda
        npcs[1] = new com.graphics.conduccion.AutoNPC(entornoActual, 80, -20, -(float) Math.PI / 2); // Calle derecha
        npcs[2] = new com.graphics.conduccion.AutoNPC(entornoActual, 20, 80, 0); // Zona residencial
        for (int i = 0; i < 3; i++) {
            npcModelos[i] = new AutoModelo(npcs[i]);
            // Modificamos color de chasis de NPCs para distinguirlos
            if (i == 0) { npcModelos[i].setColorChasis(0.2f, 0.2f, 0.8f); npcs[i].setColor(0.2f, 0.2f, 0.8f); } // Azul
            if (i == 1) { npcModelos[i].setColorChasis(0.8f, 0.2f, 0.2f); npcs[i].setColor(0.8f, 0.2f, 0.2f); } // Rojo
            if (i == 2) { npcModelos[i].setColorChasis(0.2f, 0.8f, 0.2f); npcs[i].setColor(0.2f, 0.8f, 0.2f); } // Verde
        }
        
        glfwSetInputMode(ventana, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
        
        glfwSetCursorPosCallback(ventana, (ventanaEvento, xpos, ypos) -> {
            if (lastMouseX == -1) {
                lastMouseX = xpos;
                lastMouseY = ypos;
            }
            float dx = (float) (xpos - lastMouseX);
            float dy = (float) (ypos - lastMouseY);
            lastMouseX = xpos;
            lastMouseY = ypos;
            
            camaraYaw -= dx * 0.2f;
            camaraPitch += dy * 0.2f;
            
            if (camaraPitch < -10) camaraPitch = -10;
            if (camaraPitch > 80) camaraPitch = 80;
        });

        glfwSetKeyCallback(ventana, (ventanaEvento, key, scancode, action, mods) -> {
            if (action == GLFW_PRESS) {
                if (key == GLFW_KEY_ESCAPE) glfwSetWindowShouldClose(ventana, true);
                if (key == GLFW_KEY_C) camaraAerea = !camaraAerea;
                if (key == GLFW_KEY_R) { auto.reiniciar(); panelUI.reiniciar(); }
                if (key == GLFW_KEY_F) farosEncendidos = !farosEncendidos;
                if (key == GLFW_KEY_M) mostrarMinimapa = !mostrarMinimapa;
                if (key == GLFW_KEY_1) cambiarEntorno(ciudad);
                if (key == GLFW_KEY_2) cambiarEntorno(pista);
                if (key == GLFW_KEY_3) cambiarEntorno(monte);
            }
        });
    }
    
    private void cambiarEntorno(com.graphics.modelos.Entorno nuevoEntorno) {
        entornoActual = nuevoEntorno;
        auto.setEntorno(entornoActual);
        for (int i = 0; i < 3; i++) {
            npcs[i].setEntorno(entornoActual);
            npcs[i].reiniciar();
        }
        particulas.clear();
        panelUI.reiniciar();
    }

    private void loop() {
        double tiempoAnterior = glfwGetTime();
        
        while (!glfwWindowShouldClose(ventana)) {
            glfwPollEvents();
            
            double tiempoActual = glfwGetTime();
            float deltaTime = (float) Math.min(tiempoActual - tiempoAnterior, 0.05);
            tiempoAnterior = tiempoActual;
            
            boolean adelante = glfwGetKey(ventana, GLFW_KEY_W) == GLFW_PRESS || glfwGetKey(ventana, GLFW_KEY_UP) == GLFW_PRESS;
            boolean atras = glfwGetKey(ventana, GLFW_KEY_S) == GLFW_PRESS || glfwGetKey(ventana, GLFW_KEY_DOWN) == GLFW_PRESS;
            boolean izquierda = glfwGetKey(ventana, GLFW_KEY_A) == GLFW_PRESS || glfwGetKey(ventana, GLFW_KEY_LEFT) == GLFW_PRESS;
            boolean derecha = glfwGetKey(ventana, GLFW_KEY_D) == GLFW_PRESS || glfwGetKey(ventana, GLFW_KEY_RIGHT) == GLFW_PRESS;
            boolean freno = glfwGetKey(ventana, GLFW_KEY_SPACE) == GLFW_PRESS;
            boolean turbo = glfwGetKey(ventana, GLFW_KEY_LEFT_SHIFT) == GLFW_PRESS || glfwGetKey(ventana, GLFW_KEY_RIGHT_SHIFT) == GLFW_PRESS;
            
            boolean jugadorEstabaVivo = !auto.isDestruido();
            auto.setTurbo(turbo);
            auto.actualizar(deltaTime, adelante, atras, izquierda, derecha, freno);
            
            if (turbo && Math.abs(auto.getVelocidad()) > 10.0f && !auto.isDestruido()) {
                float fX = -(float) Math.sin(auto.getAngulo());
                float fZ = -(float) Math.cos(auto.getAngulo());
                float pX = auto.getX() - fX * 2.0f;
                float pZ = auto.getZ() - fZ * 2.0f;
                
                for (int i = 0; i < 5; i++) {
                    float velX = -fX * 15f + (float) (Math.random() * 6 - 3);
                    float velY = (float) (Math.random() * 2);
                    float velZ = -fZ * 15f + (float) (Math.random() * 6 - 3);
                    // Fuego: Rojo/Naranja/Amarillo
                    particulas.add(new com.graphics.conduccion.Particula(pX, auto.getY() + 0.3f, pZ, velX, velY, velZ, 1.0f, (float)Math.random()*0.6f, 0.0f, 0.4f + (float)Math.random()*0.3f));
                }
            }
            
            if (jugadorEstabaVivo && auto.isDestruido()) {
                // El jugador acaba de morir
                float fX = -(float) Math.sin(auto.getAngulo());
                float fZ = -(float) Math.cos(auto.getAngulo());
                
                // Super explosión
                for (int i = 0; i < 60; i++) {
                    float velX = fX * auto.getVelocidad() * 0.5f + (float) (Math.random() * 30 - 15);
                    float velY = 15f + (float) (Math.random() * 25);
                    float velZ = fZ * auto.getVelocidad() * 0.5f + (float) (Math.random() * 30 - 15);
                    
                    float pR = (Math.random() > 0.5) ? 0.7f : 0.1f;
                    float pG = (Math.random() > 0.5) ? 0.1f : 0.1f;
                    float pB = (Math.random() > 0.5) ? 0.15f : 0.1f; // Rojo oscuro o gris
                    
                    float size = 0.3f + (float) Math.random() * 0.8f;
                    
                    particulas.add(new com.graphics.conduccion.Particula(auto.getX(), auto.getY() + 1.0f, auto.getZ(), velX, velY, velZ, pR, pG, pB, size));
                }
            }
            
            for (int i = 0; i < 3; i++) {
                npcs[i].actualizar(deltaTime, false, false, false, false, false);
                
                // Chequear colision del jugador con NPC para explosion
                if (!npcs[i].isDestruido()) {
                    float dx = auto.getX() - npcs[i].getX();
                    float dz = auto.getZ() - npcs[i].getZ();
                    if (dx*dx + dz*dz < 3.0f * 3.0f && Math.abs(auto.getVelocidad()) > 5.0f) { // Choca y explota
                        float fX = -(float) Math.sin(auto.getAngulo());
                        float fZ = -(float) Math.cos(auto.getAngulo());
                        npcs[i].explotar(particulas, Math.abs(auto.getVelocidad()), fX, fZ);
                        
                        // Rebote del jugador
                        // Accedemos indirectamente o ignoramos, ya que atravesarlo al destruirlo es divertido
                    }
                }
            }
            
            // Actualizar particulas y eliminar las muertas
            java.util.Iterator<com.graphics.conduccion.Particula> it = particulas.iterator();
            while(it.hasNext()) {
                com.graphics.conduccion.Particula p = it.next();
                p.actualizar(deltaTime);
                if (!p.estaViva()) it.remove();
            }
            
            entornoActual.actualizar(deltaTime);
            
            int kilometrosPorHora = Math.round(Math.abs(auto.getVelocidad()) * 3.6f);
            glfwSetWindowTitle(ventana, "Auto Modular | WASD: conducir | C: cámara | R: reiniciar | " + kilometrosPorHora + " km/h");
            
            int[] anchoReal = new int[1], altoReal = new int[1];
            glfwGetFramebufferSize(ventana, anchoReal, altoReal);
            ancho = anchoReal[0];
            alto = altoReal[0];
            
            if (ancho > 0 && alto > 0) {
                // Reloj y ciclo de día / noche (6 fases: amanecer, día, atardecer, noche)
                tiempoCiclo += deltaTime;
                if (tiempoCiclo > duracionDiaCompleto) tiempoCiclo -= duracionDiaCompleto;
                
                float fase = (tiempoCiclo / duracionDiaCompleto) * 6.0f; // 0 a 6
                
                float uDia = 1.0f;
                if (fase < 1.0f) uDia = fase; // Amanece
                else if (fase < 4.0f) uDia = 1.0f; // Día
                else if (fase < 5.0f) uDia = 1.0f - (fase - 4.0f); // Atardece
                else uDia = 0.0f; // Noche

                float skyR, skyG, skyB;
                float solR, solG, solB;
                float anguloSol;

                if (fase < 1.0f) { // Amanecer (0 -> 1)
                    float t = fase;
                    skyR = lerp(0.05f, 0.4f, t);
                    skyG = lerp(0.05f, 0.6f, t);
                    skyB = lerp(0.1f, 0.9f, t);
                    if (t > 0.3f && t < 0.8f) {
                        float b = (0.25f - Math.abs(t - 0.55f)) * 4.0f;
                        skyR += b * 0.6f;
                        skyG += b * 0.2f;
                    }
                    solR = 1.0f;
                    solG = lerp(0.4f, 1.0f, t);
                    solB = lerp(0.0f, 1.0f, t);
                    anguloSol = (t / 1.0f) * (float) (Math.PI / 6);
                } else if (fase < 4.0f) { // Día (1 -> 4)
                    float t = (fase - 1.0f) / 3.0f;
                    skyR = 0.4f; skyG = 0.6f; skyB = 0.9f;
                    solR = 1.0f; solG = 1.0f; solB = 1.0f;
                    anguloSol = (float) (Math.PI / 6) + t * (float) (4 * Math.PI / 6);
                } else if (fase < 5.0f) { // Atardecer (4 -> 5)
                    float t = fase - 4.0f;
                    if (t < 0.5f) {
                        float t2 = t * 2.0f;
                        skyR = lerp(0.4f, 0.9f, t2);
                        skyG = lerp(0.6f, 0.4f, t2);
                        skyB = lerp(0.9f, 0.2f, t2);
                    } else {
                        float t2 = (t - 0.5f) * 2.0f;
                        skyR = lerp(0.9f, 0.05f, t2);
                        skyG = lerp(0.4f, 0.05f, t2);
                        skyB = lerp(0.2f, 0.1f, t2);
                    }
                    solR = 1.0f;
                    solG = lerp(1.0f, 0.4f, t);
                    solB = lerp(1.0f, 0.0f, t);
                    anguloSol = (float) (5 * Math.PI / 6) + t * (float) (Math.PI / 6);
                } else { // Noche (5 -> 6)
                    float t = fase - 5.0f;
                    skyR = 0.05f; skyG = 0.05f; skyB = 0.1f;
                    solR = 0.0f; solG = 0.0f; solB = 0.0f;
                    anguloSol = (float) Math.PI + t * (float) Math.PI;
                }

                Color colorCielo = Color.rgb(skyR, skyG, skyB);
                Color colorSol = Color.rgb(solR, solG, solB);

                // 1. Preparar la pantalla para el nuevo fotograma con el color del cielo (sin tocar OpenGL)
                renderizador.prepararEscena(ancho, alto, colorCielo);
                renderizador.configurarTiempo((float) glfwGetTime());

                // 2. Posicionar la cámara
                float ojoX, ojoY, ojoZ, objetivoX, objetivoY, objetivoZ;
                if (camaraAerea) {
                    ojoX = -60; ojoY = 55; ojoZ = 60;
                    objetivoX = 0; objetivoY = 0; objetivoZ = 0;
                } else {
                    float distancia = 12f;
                    float pitchRad = (float) Math.toRadians(camaraPitch);
                    float yawRad = (float) Math.toRadians(camaraYaw);
                    float offsetX = distancia * (float) (Math.cos(pitchRad) * Math.sin(yawRad));
                    float offsetY = distancia * (float) Math.sin(pitchRad);
                    float offsetZ = distancia * (float) (Math.cos(pitchRad) * Math.cos(yawRad));
                    ojoX = auto.getX() + offsetX;
                    ojoY = auto.getY() + 1.5f + offsetY;
                    ojoZ = auto.getZ() + offsetZ;
                    objetivoX = auto.getX();
                    objetivoY = auto.getY() + 1.5f;
                    objetivoZ = auto.getZ();
                }
                renderizador.configurarCamara(ojoX, ojoY, ojoZ, objetivoX, objetivoY, objetivoZ);

                // 3. Iluminación natural (Sol, Luna y cielo)
                renderizador.configurarIluminacionGlobal(uDia, anguloSol, colorSol, colorCielo);
                renderizador.dibujarAstros(ojoX, ojoY, ojoZ, anguloSol, colorSol);

                // 4. Luces de vehículos y farolas de la ciudad
                float fX = -(float) Math.sin(auto.getAngulo());
                float fZ = -(float) Math.cos(auto.getAngulo());
                renderizador.configurarLuzVehiculo(0, auto.getX(), auto.getY(), auto.getZ(), fX, fZ, farosEncendidos);
                
                boolean farosNPC = (uDia < 0.3f);
                for (int i = 0; i < 3; i++) {
                    float nX = -(float) Math.sin(npcs[i].getAngulo());
                    float nZ = -(float) Math.cos(npcs[i].getAngulo());
                    renderizador.configurarLuzVehiculo(i + 1, npcs[i].getX(), npcs[i].getY(), npcs[i].getZ(), nX, nZ, farosNPC);
                }
                renderizador.configurarFarolas(entornoActual.getPosicionesFarolas());

                // 5. Dibujar la escena principal
                entornoActual.dibujar(renderizador);
                if (!auto.isDestruido()) autoModelo.dibujar(renderizador);
                for (int i = 0; i < 3; i++) {
                    if (!npcs[i].isDestruido()) npcModelos[i].dibujar(renderizador);
                }
                for (com.graphics.conduccion.Particula p : particulas) {
                    p.dibujar(renderizador);
                }

                // 6. Minimapa cenital (Totalmente encapsulado)
                if (mostrarMinimapa) {
                    renderizador.dibujarMinimapa(ancho, alto, () -> {
                        entornoActual.dibujar(renderizador);
                        if (!auto.isDestruido()) autoModelo.dibujar(renderizador);
                        for (int i = 0; i < 3; i++) {
                            if (!npcs[i].isDestruido()) npcModelos[i].dibujar(renderizador);
                        }
                        for (com.graphics.conduccion.Particula p : particulas) {
                            p.dibujar(renderizador);
                        }
                        // Marcador de objetivo / destino en la plaza
                        renderizador.marcadorMinimapa(85.0f, 20.0f, 0.0f, Color.MAGENTA);
                    });
                }

                // 7. Panel de control HUD (Velocímetro, vida, estado)
                String zona = "CIUDAD";
                if (entornoActual == pista) zona = "PISTA CARRERA";
                if (entornoActual == monte) zona = "MONTE";
                
                panelUI.actualizar(kilometrosPorHora, farosEncendidos, fase, zona, auto.getVida());
                panelUI.dibujar(renderizador, ancho, alto);
            }
            
            glfwSwapBuffers(ventana);
        }
    }

    private void limpiar() {
        if (renderizador != null) renderizador.limpiar();
        Callbacks.glfwFreeCallbacks(ventana);
        glfwDestroyWindow(ventana);
        glfwTerminate();
    }
}


