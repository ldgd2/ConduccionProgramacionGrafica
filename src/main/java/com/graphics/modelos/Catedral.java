package com.graphics.modelos;

import com.graphics.core.Color;
import com.graphics.core.Pincel;

public class Catedral extends Objeto {

    public Catedral(float x, float z) {
        super(x, z);
    }

    @Override
    public void dibujar(Pincel p) {
        Color ladrillo = Color.LADRILLO;
        Color techo = Color.TECHO;
        Color cupula = Color.rgb(0.8f, 0.3f, 0.3f);

        // Acera
        p.acera(0, 0, 10, 10);
        
        // Nave principal
        p.caja(0, 4.0f, 0, 6.0f, 8.0f, 8.0f, ladrillo);
        p.caja(0, 8.5f, 0, 5.0f, 1.0f, 8.2f, techo);
        p.caja(0, 9.5f, 0, 3.0f, 1.0f, 8.2f, techo);
        
        // Torres frontal izquierda y derecha
        p.caja(-3.5f, 6.0f, 3.0f, 2.5f, 12.0f, 2.5f, ladrillo);
        p.caja(-3.5f, 12.5f, 3.0f, 2.0f, 1.0f, 2.0f, cupula);
        p.cubo(-3.5f, 13.5f, 3.0f, 1.0f, cupula);

        p.caja(3.5f, 6.0f, 3.0f, 2.5f, 12.0f, 2.5f, ladrillo);
        p.caja(3.5f, 12.5f, 3.0f, 2.0f, 1.0f, 2.0f, cupula);
        p.cubo(3.5f, 13.5f, 3.0f, 1.0f, cupula);
        
        // Reloj y Puerta principal
        p.caja(3.5f, 10.0f, 4.3f, 0.8f, 0.8f, 0.1f, BLANCO);
        p.caja(0, 1.5f, 4.1f, 2.0f, 3.0f, 0.2f, MADERA_OSCURA);
        
        // Ventanas y Rosetón con brillo nocturno
        p.conBrilloDeNoche(() -> {
            p.caja(0, 5.0f, 4.1f, 1.5f, 1.5f, 0.1f, color.LUZ_CALIDA);
            p.caja(-3.5f, 8.0f, 4.3f, 0.8f, 1.5f, 0.1f, color.LUZ_CALIDA);
            p.caja(3.5f, 8.0f, 4.3f, 0.8f, 1.5f, 0.1f, color.LUZ_CALIDA);
        });
    }

    @Override
    public boolean esColisionable() {
        return true;
    }

    @Override
    public float getRadioColisionX() {
        return 2.2f;
    }

    @Override
    public float getRadioColisionZ() {
        return 3.0f;
    }
}
