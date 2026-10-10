package com.graphics.modelos;

import com.graphics.core.Renderizador;

public interface Entorno {
    void actualizar(float deltaTime);
    void dibujar(Renderizador renderizador);
    boolean puedeCircular(Objeto auto, float velocidadAuto, float dirX, float dirZ);
    boolean enAgua(float px, float pz);
    float getAlturaSuelo(float px, float pz);
    float getPendienteSuelo(float px, float pz);
    float[] getPosicionesFarolas();
}
