package com.graphics.modelos;

import com.graphics.core.Color;
import com.graphics.core.Pincel;
import com.graphics.core.Renderizador;
import java.util.ArrayList;
import java.util.List;

public class Ciudad implements Entorno {
    private static final float LIMITE = 105;
    private static final float CELDA = 10;
    
    // 0 = Calle, 1 = Edificio, 2 = Parque, 3 = Catedral, 4 = Puente, 5 = Rio
    private static final int[][] MAPA = {
        { 1, 1, 0, 1, 1, 1, 0, 1, 1, 5, 5, 5, 1, 1, 0, 1, 1, 0, 1, 1, 1 }, // 0
        { 1, 1, 0, 1, 1, 1, 0, 1, 1, 5, 5, 5, 1, 1, 0, 1, 1, 0, 1, 1, 1 }, // 1
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 4, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, // 2 (Avenida Norte)
        { 1, 1, 0, 1, 1, 1, 0, 1, 1, 5, 5, 5, 1, 1, 0, 2, 2, 0, 1, 1, 1 }, // 3
        { 1, 1, 0, 1, 1, 1, 0, 1, 1, 5, 5, 5, 1, 1, 0, 2, 2, 0, 1, 1, 1 }, // 4
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 4, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, // 5 (Calle Intermedia Norte)
        { 1, 1, 0, 1, 2, 2, 0, 3, 1, 5, 5, 5, 1, 1, 0, 1, 1, 0, 1, 1, 1 }, // 6 (Plaza en Downtown: 4,5; Catedral 7)
        { 1, 1, 0, 1, 2, 2, 0, 1, 1, 5, 5, 5, 1, 1, 0, 1, 1, 0, 1, 1, 1 }, // 7 
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 4, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, // 8 (Gran Avenida Central y Puente)
        { 1, 1, 0, 1, 1, 1, 0, 1, 1, 5, 5, 5, 2, 2, 0, 1, 1, 0, 1, 1, 1 }, // 9
        { 1, 1, 0, 1, 1, 1, 0, 1, 1, 5, 5, 5, 2, 2, 0, 1, 1, 0, 1, 1, 1 }, // 10
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 4, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, // 11 (Calle Intermedia Sur)
        { 1, 1, 0, 1, 1, 1, 0, 1, 1, 5, 5, 5, 1, 1, 0, 1, 1, 0, 2, 2, 1 }, // 12
        { 1, 1, 0, 1, 1, 1, 0, 1, 1, 5, 5, 5, 1, 1, 0, 1, 1, 0, 2, 2, 1 }, // 13
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 4, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, // 14 (Avenida Sur)
        { 1, 1, 0, 1, 1, 1, 0, 1, 1, 5, 5, 5, 1, 1, 0, 1, 1, 0, 1, 1, 1 }, // 15
        { 1, 1, 0, 1, 1, 1, 0, 1, 1, 5, 5, 5, 1, 1, 0, 1, 1, 0, 1, 1, 1 }, // 16
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 4, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, // 17 (Bulevar Perimetral Sur)
        { 1, 1, 0, 2, 2, 1, 0, 1, 1, 5, 5, 5, 1, 1, 0, 1, 1, 0, 1, 1, 1 }, // 18
        { 1, 1, 0, 2, 2, 1, 0, 1, 1, 5, 5, 5, 1, 1, 0, 1, 1, 0, 1, 1, 1 }, // 19
        { 1, 1, 0, 1, 1, 1, 0, 1, 1, 5, 5, 5, 1, 1, 0, 1, 1, 0, 1, 1, 1 }  // 20
    };
    
    private final List<Objeto> objetos = new ArrayList<>();
    private final List<Farola> farolas = new ArrayList<>();
    private final List<Farola> farolasCercanasBuffer = new ArrayList<>();

    public Ciudad() {
        for (int fila = 0; fila < MAPA.length; fila++) {
            for (int columna = 0; columna < MAPA[fila].length; columna++) {
                float x = centro(columna);
                float z = centro(fila);
                int tipo = MAPA[fila][columna];
                
                if (tipo == 0) {
                    objetos.add(new Calle(x, z, fila, columna));
                } else if (tipo == 1) {
                    float altura, rojo, azul;
                    if (columna < 9) {
                        // Zona Céntrica / Downtown (Rascacielos)
                        altura = 14 + (fila * 7 + columna * 13) % 28;
                        rojo = 0.15f + columna * 0.02f;
                        azul = 0.5f + fila * 0.03f;
                    } else {
                        // Zona Residencial (Casas y edificios medianos)
                        altura = 4 + (fila * 3 + columna * 7) % 8;
                        rojo = 0.45f + columna * 0.02f;
                        azul = 0.2f + fila * 0.01f;
                    }
                    objetos.add(new Edificio(x, z, altura, rojo, azul));
                } else if (tipo == 2) {
                    objetos.add(new Parque(x, z));
                    // 4 árboles con colisión y destrucción física en cada parque
                    objetos.add(new Arbol(x - 2.5f, z - 2.5f));
                    objetos.add(new Arbol(x + 2.5f, z - 2.5f));
                    objetos.add(new Arbol(x - 2.5f, z + 2.5f));
                    objetos.add(new Arbol(x + 2.5f, z + 2.5f));
                } else if (tipo == 3) {
                    objetos.add(new Catedral(x, z));
                } else if (tipo == 4) {
                    // Puentes urbanos sólidos y seguros sobre el canal
                    objetos.add(new Puente(x, z, fila, columna));
                } else if (tipo == 5) {
                    objetos.add(new Rio(x, z));
                }
            }
        }
        
        // Semáforos en las principales intersecciones
        objetos.add(new Semaforo(centro(6) + 3.5f, centro(6) + 3.5f));
        objetos.add(new Semaforo(centro(2) + 3.5f, centro(2) + 3.5f));
        objetos.add(new Semaforo(centro(8) - 3.5f, centro(2) - 3.5f));
        objetos.add(new Semaforo(centro(2) + 3.5f, centro(14) + 3.5f));
        
        // Barandas/Vallas a lo largo de las orillas del río (donde no hay puentes)
        for (int i = 0; i < 21; i++) {
            if (MAPA[i][9] == 5) {
                objetos.add(new Valla(centro(9) - 5f, centro(i)));
                objetos.add(new Valla(centro(11) + 5f, centro(i)));
            }
        }

        // Calles cerradas con barricadas/vallas destructibles en tramos de obras transitables
        // 1. Barricada en la calle transversal oeste (x = -80, z = -50)
        objetos.add(new Valla(-81.5f, -50.0f));
        objetos.add(new Valla(-78.5f, -50.0f));

        // 2. Barricada en la entrada residencial este (x = 70, z = 10)
        objetos.add(new Valla(68.5f, 10.0f));
        objetos.add(new Valla(71.5f, 10.0f));

        // 3. Barricada en la avenida sur (x = -40, z = 40)
        objetos.add(new Valla(-41.5f, 40.0f));
        objetos.add(new Valla(-38.5f, 40.0f));

        // Señalización urbana y mobiliario
        objetos.add(new SenalizacionUrbana(-75f, -75f, SenalizacionUrbana.Tipo.CARTEL_DOWNTOWN, 0));
        objetos.add(new SenalizacionUrbana(25f, -15f, SenalizacionUrbana.Tipo.CARTEL_RESIDENCIAL, -(float)Math.PI / 2));
        objetos.add(new SenalizacionUrbana(-35f, -15f, SenalizacionUrbana.Tipo.CARTEL_CATEDRAL, (float)Math.PI / 2));
        
        objetos.add(new SenalizacionUrbana(-76f, -16f, SenalizacionUrbana.Tipo.SENAL_STOP, 0));
        objetos.add(new SenalizacionUrbana(36f, -16f, SenalizacionUrbana.Tipo.SENAL_STOP, (float)Math.PI));
        objetos.add(new SenalizacionUrbana(-76f, 14f, SenalizacionUrbana.Tipo.SENAL_VELOCIDAD, 0));
        objetos.add(new SenalizacionUrbana(66f, -44f, SenalizacionUrbana.Tipo.SENAL_VELOCIDAD, (float)Math.PI / 2));

        objetos.add(new SenalizacionUrbana(-44f, -46f, SenalizacionUrbana.Tipo.PARADA_AUTOBUS, 0));
        objetos.add(new SenalizacionUrbana(44f, -46f, SenalizacionUrbana.Tipo.PARADA_AUTOBUS, (float)Math.PI));
        objetos.add(new SenalizacionUrbana(-84f, -24f, SenalizacionUrbana.Tipo.HIDRANTE, 0));
        objetos.add(new SenalizacionUrbana(74f, 16f, SenalizacionUrbana.Tipo.HIDRANTE, 0));

        // Bancas urbanas destructibles en parques, plazas y banquetas
        // 1. Parque Downtown (x: -55, z: -35)
        objetos.add(new Banca(-55f, -38f, 0));
        objetos.add(new Banca(-55f, -32f, (float)Math.PI));
        objetos.add(new Banca(-58f, -35f, (float)Math.PI/2));
        objetos.add(new Banca(-52f, -35f, -(float)Math.PI/2));

        // 2. Parque Residencial Norte (x: 55, z: -65)
        objetos.add(new Banca(55f, -68f, 0));
        objetos.add(new Banca(55f, -62f, (float)Math.PI));
        objetos.add(new Banca(52f, -65f, (float)Math.PI/2));
        objetos.add(new Banca(58f, -65f, -(float)Math.PI/2));

        // 3. Parque Central Este (x: 25, z: -5)
        objetos.add(new Banca(25f, -8f, 0));
        objetos.add(new Banca(25f, -2f, (float)Math.PI));
        objetos.add(new Banca(22f, -5f, (float)Math.PI/2));
        objetos.add(new Banca(28f, -5f, -(float)Math.PI/2));

        // 4. Parque Residencial Sur (x: 85, z: 25)
        objetos.add(new Banca(85f, 22f, 0));
        objetos.add(new Banca(85f, 28f, (float)Math.PI));
        objetos.add(new Banca(82f, 25f, (float)Math.PI/2));
        objetos.add(new Banca(88f, 25f, -(float)Math.PI/2));

        // 5. Explanada de la Catedral y banquetas peatonales
        objetos.add(new Banca(-35f, -22f, 0));
        objetos.add(new Banca(-45f, -22f, 0));
        objetos.add(new Banca(-35f, -18f, (float)Math.PI));
        objetos.add(new Banca(-45f, -18f, (float)Math.PI));

        // Alumbrado público distribuido por toda la ciudad
        generarAlumbradoPublico();
    }

    private void registrarFarola(Farola f) {
        farolas.add(f);
        objetos.add(f);
    }

    private void generarAlumbradoPublico() {
        float[] callesZ = {-80f, -50f, -20f, 10f, 40f, 70f};
        float[] callesX = {-80f, -40f, 40f, 70f};

        // 1. Patrón regular y despejado: EXACTAMENTE 1 farola maestra alta por intersección
        // Distribuidas en patrón alternado para iluminar cada cruce sin amontonamiento ni contaminación visual
        for (int iz = 0; iz < callesZ.length; iz++) {
            float cz = callesZ[iz];
            for (int ix = 0; ix < callesX.length; ix++) {
                float cx = callesX[ix];
                if ((iz + ix) % 2 == 0) {
                    // Esquina Suroeste orientada hacia el cruce
                    registrarFarola(new Farola(cx - 4.8f, cz + 4.8f, (float) Math.toRadians(135)));
                } else {
                    // Esquina Noreste orientada hacia el cruce
                    registrarFarola(new Farola(cx + 4.8f, cz - 4.8f, (float) Math.toRadians(315)));
                }
            }
        }

        // 2. Patrón en los 6 Puentes sobre el Río: 1 farola central de doble brazo de gran porte por puente
        for (float cz : callesZ) {
            registrarFarola(new Farola(0f, cz - 4.8f, 0, true));
        }

        // 3. Patrón regular a mitad de tramo en la Gran Avenida Central (z = -20f)
        registrarFarola(new Farola(-60f, -24.8f, 0));
        registrarFarola(new Farola(55f, -24.8f, 0));

        // 4. Entorno de la Catedral: 2 farolas monumentales simétricas
        registrarFarola(new Farola(-40f, -27f, 0));
        registrarFarola(new Farola(-40f, -13f, (float) Math.PI));
    }
    
    public void reiniciar() {
        for (Objeto o : objetos) {
            if (o instanceof ObjetoDestructible) {
                ((ObjetoDestructible) o).reiniciar();
            }
        }
    }
    
    private float centro(int indice) {
        return -LIMITE + CELDA * (indice + 0.5f);
    }
    
    public void actualizar(float deltaTime) {
        for (Objeto objeto : objetos) {
            objeto.actualizar(deltaTime);
        }
    }
    
    public void dibujar(Renderizador renderizador) {
        // Base de asfalto continuo de la ciudad (210 x 210)
        renderizador.caja(0, -0.25f, 0, 210, 0.5f, 210, Color.ASFALTO);

        // Muros perimetrales exteriores que cierran visual y físicamente toda la ciudad
        float paredGrosor = 1.2f;
        float paredAltura = 2.0f;
        renderizador.caja(0, paredAltura / 2, -LIMITE, 210, paredAltura, paredGrosor, Color.METAL_OSCURO);
        renderizador.caja(0, paredAltura / 2, LIMITE, 210, paredAltura, paredGrosor, Color.METAL_OSCURO);
        renderizador.caja(-LIMITE, paredAltura / 2, 0, paredGrosor, paredAltura, 210, Color.METAL_OSCURO);
        renderizador.caja(LIMITE, paredAltura / 2, 0, paredGrosor, paredAltura, 210, Color.METAL_OSCURO);
        
        for (Objeto objeto : objetos) {
            objeto.dibujar(renderizador);
        }
    }
    
    public float getAlturaSuelo(float px, float pz) {
        return 0; // Superficie urbana firme y plana sin desniveles ni caídas
    }

    public float getPendienteSuelo(float px, float pz) {
        return 0;
    }
    
    public boolean puedeCircular(Objeto auto, float velocidadAuto, float dirX, float dirZ) {
        float limitePermitido = LIMITE - auto.getRadio();
        
        // 1. Límites estrictos del perímetro de la ciudad: jamás se sale al vacío
        if (Math.abs(auto.getX()) > limitePermitido || Math.abs(auto.getZ()) > limitePermitido) {
            return false;
        }

        // 2. Bloqueo en orillas del canal: no permite caer al agua fuera de los puentes
        if (enAgua(auto.getX(), auto.getZ())) {
            return false;
        }

        // 3. Colisión física sólida solo con edificios y casas (la banqueta y objetos destructibles son transitables)
        for (Objeto obstaculo : objetos) {
            if (obstaculo.esColisionable() && obstaculo.colisiona(auto)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void verificarImpactosDestructibles(com.graphics.conduccion.Auto auto, java.util.List<com.graphics.conduccion.Particula> particulas) {
        if (auto == null || auto.isDestruido()) return;

        float autoX = auto.getX();
        float autoZ = auto.getZ();
        float radioAuto = 1.35f;
        float velocidad = auto.getVelocidad();

        // Requiere velocidad mínima para impactar y quebrar (más de 0.5 m/s)
        if (Math.abs(velocidad) < 0.5f) return;

        float fX = -(float) Math.sin(auto.getAngulo());
        float fZ = -(float) Math.cos(auto.getAngulo());
        float dirMovX = (velocidad >= 0) ? fX : -fX;
        float dirMovZ = (velocidad >= 0) ? fZ : -fZ;

        for (Objeto o : objetos) {
            if (o instanceof ObjetoDestructible) {
                ObjetoDestructible dest = (ObjetoDestructible) o;
                if (!dest.isRoto()) {
                    float dx = autoX - dest.getX();
                    float dz = autoZ - dest.getZ();
                    float radioTotal = radioAuto + dest.getRadioImpacto();
                    if (dx * dx + dz * dz < radioTotal * radioTotal) {
                        // Derribar y desintegrar con animación rápida de destrucción
                        dest.romper(velocidad, dirMovX, dirMovZ, particulas);
                        // Restar vida al vehículo del jugador
                        auto.aplicarDanio(dest.getDanioAlAuto());
                        // Frena ligeramente la inercia
                        auto.reducirVelocidadPorImpacto(0.85f);
                    }
                }
            }
        }
    }
    
    public boolean enAgua(float px, float pz) {
        int columna = (int) ((px + LIMITE) / CELDA);
        int fila = (int) ((pz + LIMITE) / CELDA);
        
        if (columna < 0 || columna >= 21 || fila < 0 || fila >= 21) return false;
        
        // Es agua solo si la celda es tipo 5 (Río sin puente)
        return MAPA[fila][columna] == 5;
    }

    public float[] getPosicionesFarolasCercanas(float autoX, float autoZ) {
        final int MAX_LUCES = 32;
        float[] posiciones = new float[MAX_LUCES * 3];

        // Ordenar dinámicamente las farolas por proximidad al vehículo del jugador
        farolasCercanasBuffer.clear();
        farolasCercanasBuffer.addAll(farolas);
        farolasCercanasBuffer.sort((f1, f2) -> {
            float dx1 = f1.getX() - autoX;
            float dz1 = f1.getZ() - autoZ;
            float dist1 = dx1 * dx1 + dz1 * dz1;

            float dx2 = f2.getX() - autoX;
            float dz2 = f2.getZ() - autoZ;
            float dist2 = dx2 * dx2 + dz2 * dz2;

            return Float.compare(dist1, dist2);
        });

        int total = Math.min(MAX_LUCES, farolasCercanasBuffer.size());
        for (int i = 0; i < total; i++) {
            Farola f = farolasCercanasBuffer.get(i);
            posiciones[i * 3]     = f.getPosicionLuzX();
            posiciones[i * 3 + 1] = f.getPosicionLuzY();
            posiciones[i * 3 + 2] = f.getPosicionLuzZ();
        }
        for (int i = total; i < MAX_LUCES; i++) {
            posiciones[i * 3]     = 0f;
            posiciones[i * 3 + 1] = -100f;
            posiciones[i * 3 + 2] = 0f;
        }
        return posiciones;
    }

    @Override
    public float[] getPosicionesFarolas() {
        return getPosicionesFarolasCercanas(0, 0);
    }
}
