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
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 4, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, // 2 (Calle Principal)
        { 1, 1, 0, 1, 1, 1, 0, 1, 1, 5, 5, 5, 1, 1, 0, 2, 2, 0, 1, 1, 1 }, // 3
        { 1, 1, 0, 1, 1, 1, 0, 1, 1, 5, 5, 5, 1, 1, 0, 2, 2, 0, 1, 1, 1 }, // 4
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 4, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, // 5
        { 1, 1, 0, 1, 2, 2, 0, 3, 1, 5, 5, 5, 1, 1, 0, 1, 1, 0, 1, 1, 1 }, // 6 (Plaza en Downtown: 4,5; Catedral 7)
        { 1, 1, 0, 1, 2, 2, 0, 1, 1, 5, 5, 5, 1, 1, 0, 1, 1, 0, 1, 1, 1 }, // 7 
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 4, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, // 8 (Calle Principal)
        { 1, 1, 0, 1, 1, 1, 0, 1, 1, 5, 5, 5, 2, 2, 0, 1, 1, 0, 1, 1, 1 }, // 9
        { 1, 1, 0, 1, 1, 1, 0, 1, 1, 5, 5, 5, 2, 2, 0, 1, 1, 0, 1, 1, 1 }, // 10
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 4, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, // 11
        { 1, 1, 0, 1, 1, 1, 0, 1, 1, 5, 5, 5, 1, 1, 0, 1, 1, 0, 2, 2, 1 }, // 12
        { 1, 1, 0, 1, 1, 1, 0, 1, 1, 5, 5, 5, 1, 1, 0, 1, 1, 0, 2, 2, 1 }, // 13
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 4, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, // 14
        { 1, 1, 0, 1, 1, 1, 0, 1, 1, 5, 5, 5, 1, 1, 0, 1, 1, 0, 1, 1, 1 }, // 15
        { 1, 1, 0, 1, 1, 1, 0, 1, 1, 5, 5, 5, 1, 1, 0, 1, 1, 0, 1, 1, 1 }, // 16
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 4, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, // 17
        { 1, 1, 0, 2, 2, 1, 0, 1, 1, 5, 5, 5, 1, 1, 0, 1, 1, 0, 1, 1, 1 }, // 18
        { 1, 1, 0, 2, 2, 1, 0, 1, 1, 5, 5, 5, 1, 1, 0, 1, 1, 0, 1, 1, 1 }, // 19
        { 1, 1, 0, 1, 1, 1, 0, 1, 1, 5, 5, 5, 1, 1, 0, 1, 1, 0, 1, 1, 1 }  // 20
    };
    
    private List<Objeto> objetos = new ArrayList<>();
    private List<Farola> farolas = new ArrayList<>();

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
                        // Zona Céntrica (Downtown)
                        altura = 12 + (fila * 7 + columna * 13) % 30;
                        rojo = 0.15f + columna * 0.02f;
                        azul = 0.5f + fila * 0.03f;
                    } else {
                        // Zona Residencial
                        altura = 3 + (fila * 3 + columna * 7) % 6;
                        rojo = 0.45f + columna * 0.02f;
                        azul = 0.2f + fila * 0.01f;
                    }
                    objetos.add(new Edificio(x, z, altura, rojo, azul));
                } else if (tipo == 2) {
                    objetos.add(new Parque(x, z));
                    // Poner farola en la esquina del parque si hay espacio (solo hasta 9)
                    if (farolas.size() < 9) {
                        Farola f = new Farola(x + 3.5f, z + 3.5f);
                        farolas.add(f);
                        objetos.add(f);
                    }
                } else if (tipo == 3) {
                    objetos.add(new Catedral(x, z));
                } else if (tipo == 4) {
                    // No ponemos puente estatico en fila 8, lo reemplazamos por el levadizo
                    if (fila != 8) {
                        objetos.add(new Puente(x, z, fila, columna));
                    }
                } else if (tipo == 5) {
                    objetos.add(new Rio(x, z));
                }
            }
        }
        
        // Agregar Puente Levadizo
        PuenteLevadizo pLevadizo = new PuenteLevadizo(centro(10), centro(8), 0, 1);
        objetos.add(pLevadizo);
        
        // Agregar Pista de Carrera (Visual simplificada)
        objetos.add(new Objeto(160, -15) {
            @Override public void dibujar(Pincel p) {
                p.caja(0, 0, 0, 60, 0.5f, 100, Color.ROJO);
                p.caja(0, 0.1f, 0, 40, 0.6f, 80, Color.ASFALTO);
            }
            @Override public float getRadio() { return 0; }
        });
        
        // Agregar Monte / Offroad (Visual simplificada)
        objetos.add(new Objeto(-160, -15) {
            @Override public void dibujar(Pincel p) {
                p.caja(0, -1, 0, 80, 2f, 80, Color.CESPED);
                p.caja(10, 0, 5, 20, 4f, 30, Color.CESPED_CLARO, 0.5f);
                p.caja(-10, 0, -5, 30, 5f, 20, Color.CESPED, -0.3f);
            }
            @Override public float getRadio() { return 0; }
        });
        
        // Colocar algunos semáforos en las intersecciones importantes
        objetos.add(new Semaforo(centro(6) + 3.5f, centro(6) + 3.5f)); // Plaza principal
        objetos.add(new Semaforo(centro(2) + 3.5f, centro(2) + 3.5f));
        objetos.add(new Semaforo(centro(8) - 3.5f, centro(2) - 3.5f));
        objetos.add(new Semaforo(centro(2) + 3.5f, centro(14) + 3.5f));
        
        // Colocar vallas al borde del rio (x de inicio y fin del rio)
        // El rio esta en las columnas 9, 10, 11
        for (int i = 0; i < 21; i++) {
            if (MAPA[i][9] == 5) {
                objetos.add(new Valla(centro(9) - 5f, centro(i))); // Borde Izquierdo del rio
                objetos.add(new Valla(centro(11) + 5f, centro(i))); // Borde Derecho del rio
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
        // Base de asfalto (ahora es 210 de ancho/largo)
        renderizador.caja(0, -0.25f, 0, 210, 0.5f, 210, Color.ASFALTO);
        
        // Puente extra a la pista
        renderizador.caja(130, 0, -15, 60, 0.5f, 10, Color.METAL);
        // Puente extra al monte
        renderizador.caja(-130, 0, -15, 60, 0.5f, 10, Color.MADERA);
        
        for (Objeto objeto : objetos) {
            objeto.dibujar(renderizador);
        }
    }
    
    public float getAlturaSuelo(float px, float pz) {
        float maxAltura = 0;
        for (Objeto obj : objetos) {
            if (obj instanceof Rampa) {
                float h = ((Rampa)obj).getAlturaEn(px, pz);
                if (h > maxAltura) maxAltura = h;
            }
        }
        return maxAltura;
    }

    public float getPendienteSuelo(float px, float pz) {
        for (Objeto obj : objetos) {
            if (obj instanceof Rampa) {
                float h = ((Rampa)obj).getAlturaEn(px, pz);
                if (h > 0.1f) {
                    return ((Rampa)obj).getPendiente();
                }
            }
        }
        return 0;
    }
    
    public boolean puedeCircular(Objeto auto, float velocidadAuto, float dirX, float dirZ) {
        float limitePermitido = LIMITE - auto.getRadio();
        
        boolean enLimitesNormales = (Math.abs(auto.getX()) <= limitePermitido && Math.abs(auto.getZ()) <= limitePermitido);
        boolean enPista = (auto.getX() > limitePermitido && auto.getX() < 200 && Math.abs(auto.getZ() + 15) < 50);
        boolean enMonte = (auto.getX() < -limitePermitido && auto.getX() > -200 && Math.abs(auto.getZ() + 15) < 50);
        
        if (!enLimitesNormales && !enPista && !enMonte) {
            return false; // Colisión con el borde del mapa ampliado
        }

        for (Objeto obstaculo : objetos) {
            if (obstaculo.colisiona(auto)) {
                if (obstaculo instanceof Valla) {
                    ((Valla) obstaculo).romper(velocidadAuto, dirX, dirZ);
                    return true; // No bloquea el paso
                }
                return false;
            }
        }
        return true;
    }
    
    public boolean enAgua(float px, float pz) {
        // Encontrar columna y fila a partir de X y Z
        int columna = (int) ((px + LIMITE) / CELDA);
        int fila = (int) ((pz + LIMITE) / CELDA);
        
        // Evitar fuera de indices
        if (columna < 0 || columna >= 21 || fila < 0 || fila >= 21) return false;
        
        return MAPA[fila][columna] == 5; // Si la celda es Río (5)
    }

    public float[] getPosicionesFarolas() {
        float[] posiciones = new float[27]; // 9 farolas * 3 coordenadas
        for (int i = 0; i < 9; i++) {
            if (i < farolas.size()) {
                posiciones[i * 3] = farolas.get(i).getX();
                posiciones[i * 3 + 1] = farolas.get(i).getY() + 5.0f; // Altura de la luz
                posiciones[i * 3 + 2] = farolas.get(i).getZ();
            } else {
                posiciones[i * 3] = 0f;
                posiciones[i * 3 + 1] = -100f; // Oculta debajo del mapa
                posiciones[i * 3 + 2] = 0f;
            }
        }
        return posiciones;
    }
}

