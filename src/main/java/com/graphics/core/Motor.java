package com.graphics.core;

import com.graphics.modelos.Ciudad;
import com.graphics.modelos.AutoModelo;
import com.graphics.conduccion.Auto;
import com.graphics.conduccion.SistemaEntregas;
import com.graphics.ui.MenuAyudaUI;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import static org.lwjgl.glfw.GLFW.*;

public class Motor {
    protected long ventana;
    protected int ancho = 1100;
    protected int alto = 760;
    
    private Renderizador renderizador;
    private com.graphics.modelos.Ciudad ciudad;
    
    private Auto auto;
    private AutoModelo autoModelo;
    private com.graphics.conduccion.AutoNPC[] npcs = new com.graphics.conduccion.AutoNPC[3];
    private AutoModelo[] npcModelos = new AutoModelo[3];
    private java.util.List<com.graphics.conduccion.Particula> particulas = new java.util.ArrayList<>();
    private boolean camaraAerea = false;
    private com.graphics.ui.PanelUI panelUI;
    private MenuAyudaUI menuAyuda;
    private SistemaEntregas sistemaEntregas;

    // Variables para la cámara libre con el ratón
    private float camaraYaw = 180;
    private float camaraPitch = 15;
    private double lastMouseX = -1, lastMouseY = -1;
    
    // Iluminacion y Controles
    private boolean esDeDia = true;
    private boolean farosEncendidos = true;
    private boolean mostrarMinimapa = true;
    private boolean mostrarAyuda = false;
    
    // Ciclo del Dia
    private float tiempoCiclo = 15.0f; // Empieza a pleno día
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
        
        ventana = glfwCreateWindow(ancho, alto, "Ciudad y Conducción", 0, 0);
        if (ventana == 0) throw new IllegalStateException("No se pudo crear la ventana OpenGL");
        
        glfwMakeContextCurrent(ventana);
        glfwSwapInterval(1);
        GL.createCapabilities();
        
        renderizador = new Renderizador();
        renderizador.inicializar();
        panelUI = new com.graphics.ui.PanelUI();
        menuAyuda = new MenuAyudaUI();
        sistemaEntregas = new SistemaEntregas();
        
        ciudad = new com.graphics.modelos.Ciudad();
        
        auto = new Auto(ciudad);
        autoModelo = new AutoModelo(auto);
        
        // Tráfico autónomo: Rutas cerradas por las calles de la gran ciudad
        // NPC 0 (Azul): Circuito Oeste / Downtown en calles
        float[][] ruta0 = {{-80f, -80f}, {-80f, 10f}, {-40f, 10f}, {-40f, -80f}};
        // NPC 1 (Rojo): Circuito Este / Residencial en calles
        float[][] ruta1 = {{40f, -80f}, {70f, -80f}, {70f, 40f}, {40f, 40f}};
        // NPC 2 (Verde): Gran Circuito Trans-urbano cruzando los puentes sobre el río
        float[][] ruta2 = {{-40f, -20f}, {40f, -20f}, {40f, 70f}, {-40f, 70f}};

        npcs[0] = new com.graphics.conduccion.AutoNPC(ciudad, ruta0, 8.0f, 0.2f, 0.2f, 0.85f);
        npcs[1] = new com.graphics.conduccion.AutoNPC(ciudad, ruta1, 7.5f, 0.85f, 0.2f, 0.2f);
        npcs[2] = new com.graphics.conduccion.AutoNPC(ciudad, ruta2, 8.5f, 0.2f, 0.8f, 0.2f);

        for (int i = 0; i < 3; i++) {
            npcModelos[i] = new AutoModelo(npcs[i]);
            npcModelos[i].setColorChasis(npcs[i].getChasisR(), npcs[i].getChasisG(), npcs[i].getChasisB());
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
                if (key == GLFW_KEY_R) {
                    auto.reiniciar();
                    sistemaEntregas.reiniciar();
                    ciudad.reiniciar();
                    for (int i = 0; i < 3; i++) npcs[i].reiniciar();
                    particulas.clear();
                    panelUI.reiniciar();
                }
                if (key == GLFW_KEY_N) {
                    esDeDia = !esDeDia;
                    tiempoCiclo = esDeDia ? 15.0f : 48.0f;
                }
                if (key == GLFW_KEY_F) farosEncendidos = !farosEncendidos;
                if (key == GLFW_KEY_M) mostrarMinimapa = !mostrarMinimapa;
                if (key == GLFW_KEY_P) mostrarAyuda = !mostrarAyuda;
            }
        });
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
            
            // Actualizar entregas
            sistemaEntregas.actualizar(deltaTime, auto, particulas);
            
            // Procesar choques destructibles contra semáforos, farolas, bancas y señales
            ciudad.verificarImpactosDestructibles(auto, particulas);
            
            if (turbo && Math.abs(auto.getVelocidad()) > 10.0f && !auto.isDestruido()) {
                float fX = -(float) Math.sin(auto.getAngulo());
                float fZ = -(float) Math.cos(auto.getAngulo());
                float pX = auto.getX() - fX * 2.0f;
                float pZ = auto.getZ() - fZ * 2.0f;
                
                for (int i = 0; i < 5; i++) {
                    float velX = -fX * 15f + (float) (Math.random() * 6 - 3);
                    float velY = (float) (Math.random() * 2);
                    float velZ = -fZ * 15f + (float) (Math.random() * 6 - 3);
                    particulas.add(new com.graphics.conduccion.Particula(pX, auto.getY() + 0.3f, pZ, velX, velY, velZ, 1.0f, (float)Math.random()*0.6f, 0.0f, 0.4f + (float)Math.random()*0.3f));
                }
            }
            
            if (jugadorEstabaVivo && auto.isDestruido()) {
                float fX = -(float) Math.sin(auto.getAngulo());
                float fZ = -(float) Math.cos(auto.getAngulo());
                for (int i = 0; i < 60; i++) {
                    float velX = fX * auto.getVelocidad() * 0.5f + (float) (Math.random() * 30 - 15);
                    float velY = 15f + (float) (Math.random() * 25);
                    float velZ = fZ * auto.getVelocidad() * 0.5f + (float) (Math.random() * 30 - 15);
                    float pR = (Math.random() > 0.5) ? 0.7f : 0.1f;
                    float pG = (Math.random() > 0.5) ? 0.1f : 0.1f;
                    float pB = (Math.random() > 0.5) ? 0.15f : 0.1f;
                    float size = 0.3f + (float) Math.random() * 0.8f;
                    particulas.add(new com.graphics.conduccion.Particula(auto.getX(), auto.getY() + 1.0f, auto.getZ(), velX, velY, velZ, pR, pG, pB, size));
                }
            }
            
            // Actualizar tráfico autónomo
            for (int i = 0; i < 3; i++) {
                npcs[i].actualizar(deltaTime, false, false, false, false, false);
                
                // Colisión con NPC
                if (!npcs[i].isDestruido()) {
                    float dx = auto.getX() - npcs[i].getX();
                    float dz = auto.getZ() - npcs[i].getZ();
                    if (dx*dx + dz*dz < 3.2f * 3.2f && Math.abs(auto.getVelocidad()) > 5.0f) {
                        float fX = -(float) Math.sin(auto.getAngulo());
                        float fZ = -(float) Math.cos(auto.getAngulo());
                        npcs[i].explotar(particulas, Math.abs(auto.getVelocidad()), fX, fZ);
                    }
                }
            }
            
            // Actualizar partículas
            java.util.Iterator<com.graphics.conduccion.Particula> it = particulas.iterator();
            while(it.hasNext()) {
                com.graphics.conduccion.Particula p = it.next();
                p.actualizar(deltaTime);
                if (!p.estaViva()) it.remove();
            }
            
            ciudad.actualizar(deltaTime);
            
            // Redimensionamiento de ventana
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

                // Actualizar estado día/noche según fase
                esDeDia = (uDia > 0.35f);

                // Título de la ventana con información actualizada y legible
                int kilometrosPorHora = Math.round(Math.abs(auto.getVelocidad()) * 3.6f);
                String txtLuces = farosEncendidos ? "Faros: ON" : "Faros: OFF";
                String txtDia = esDeDia ? "Día" : "Noche";
                String txtEntregas;
                if (sistemaEntregas.isCompletado()) {
                    txtEntregas = "¡GANASTE! (Entregas 3/3 completadas)";
                } else {
                    txtEntregas = "Entregas: " + sistemaEntregas.getEntregasCompletadas() + "/" + sistemaEntregas.getTotalEntregas() 
                                + " [" + sistemaEntregas.getNombreDestinoActual() + "]";
                }
                glfwSetWindowTitle(ventana, String.format("Ciudad y Conducción | %d km/h | %s | %s | %s | [WASD: Manejar, C: Cámara, N: Día/Noche, F: Faros, M: Mapa, P: Ayuda, R: Reiniciar]",
                    kilometrosPorHora, txtLuces, txtDia, txtEntregas));

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

                // 1. Preparar la pantalla para el nuevo fotograma con el color del cielo
                renderizador.prepararEscena(ancho, alto, colorCielo);
                renderizador.configurarTiempo((float) glfwGetTime());

                // 2. Posicionar la cámara
                float ojoX, ojoY, ojoZ, objetivoX, objetivoY, objetivoZ;
                if (camaraAerea) {
                    ojoX = -60; ojoY = 70; ojoZ = 60;
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
                
                boolean farosNPC = (uDia < 0.35f);
                for (int i = 0; i < 3; i++) {
                    float nX = -(float) Math.sin(npcs[i].getAngulo());
                    float nZ = -(float) Math.cos(npcs[i].getAngulo());
                    renderizador.configurarLuzVehiculo(i + 1, npcs[i].getX(), npcs[i].getY(), npcs[i].getZ(), nX, nZ, farosNPC);
                }
                renderizador.configurarFarolas(ciudad.getPosicionesFarolasCercanas(auto.getX(), auto.getZ()));

                // 5. Dibujar la escena principal
                ciudad.dibujar(renderizador);
                sistemaEntregas.dibujar(renderizador); // Baliza 3D de entrega
                if (!auto.isDestruido()) autoModelo.dibujar(renderizador);
                for (int i = 0; i < 3; i++) {
                    if (!npcs[i].isDestruido()) npcModelos[i].dibujar(renderizador);
                }
                for (com.graphics.conduccion.Particula p : particulas) {
                    p.dibujar(renderizador);
                }

                // 6. Minimapa cenital
                if (mostrarMinimapa) {
                    renderizador.dibujarMinimapa(ancho, alto, () -> {
                        ciudad.dibujar(renderizador);
                        sistemaEntregas.dibujarMarcadorMinimapa(renderizador);
                        if (!auto.isDestruido()) autoModelo.dibujar(renderizador);
                        for (int i = 0; i < 3; i++) {
                            if (!npcs[i].isDestruido()) npcModelos[i].dibujar(renderizador);
                        }
                        for (com.graphics.conduccion.Particula p : particulas) {
                            p.dibujar(renderizador);
                        }
                    });
                }

                // 7. Panel de control HUD
                String zona = "CENTRO URBANO";
                float ax = auto.getX();
                float az = auto.getZ();
                if (ax < -15 && az < -40) zona = "DOWNTOWN NORTE";
                else if (ax < -15 && az >= -40 && az <= 15) zona = "PLAZA DE LA CATEDRAL";
                else if (ax < -15 && az > 15) zona = "PARQUE CENTRAL";
                else if (Math.abs(ax) <= 15) zona = "PUENTES SOBRE EL CANAL";
                else if (ax > 15 && az < 0) zona = "SECTOR RESIDENCIAL NORTE";
                else if (ax > 15 && az >= 0) zona = "SECTOR RESIDENCIAL SUR";
                
                String estadoEntregasUI = sistemaEntregas.isCompletado() ? 
                    "¡ENTREGAS COMPLETADAS!" : 
                    ("ENTREGAS: " + sistemaEntregas.getEntregasCompletadas() + "/" + sistemaEntregas.getTotalEntregas());

                panelUI.actualizar(kilometrosPorHora, farosEncendidos, fase, zona, auto.getVida(), estadoEntregasUI, sistemaEntregas.isCompletado());
                panelUI.dibujar(renderizador, ancho, alto);

                // 8. Menú / Superposición de ayuda de controles
                if (mostrarAyuda) {
                    menuAyuda.dibujar(renderizador, ancho, alto);
                }
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
