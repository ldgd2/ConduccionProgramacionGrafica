package com.graphics.conduccion;

import com.graphics.core.Color;
import com.graphics.core.Pincel;
import com.graphics.modelos.Objeto;

public class Particula extends Objeto {
    private float velX, velY, velZ;
    private float angX, angY, angZ;
    private float rotX, rotY, rotZ;
    private float r, g, b;
    private float size;
    private float vida = 3.0f; // Tiempo de vida en segundos

    public Particula(float x, float y, float z, float velX, float velY, float velZ, float r, float g, float b, float size) {
        super(x, z);
        this.y = y;
        this.velX = velX;
        this.velY = velY;
        this.velZ = velZ;
        this.r = r;
        this.g = g;
        this.b = b;
        this.size = size;
        
        // Rotacion inicial aleatoria
        this.rotX = (float) (Math.random() * Math.PI * 2);
        this.rotY = (float) (Math.random() * Math.PI * 2);
        this.rotZ = (float) (Math.random() * Math.PI * 2);
        
        // Velocidad de rotacion aleatoria
        this.angX = (float) (Math.random() * 10 - 5);
        this.angY = (float) (Math.random() * 10 - 5);
        this.angZ = (float) (Math.random() * 10 - 5);
    }

    @Override
    public void actualizar(float deltaTime) {
        if (vida <= 0) return;
        vida -= deltaTime;
        
        velY -= 20.0f * deltaTime; // Gravedad fuerte
        
        x += velX * deltaTime;
        y += velY * deltaTime;
        z += velZ * deltaTime;
        
        rotX += angX * deltaTime;
        rotY += angY * deltaTime;
        rotZ += angZ * deltaTime;
        
        // Rebote en el piso
        if (y < 0) {
            y = 0;
            velY = -velY * (0.3f + (float)Math.random() * 0.4f); // Rebote random
            velX *= 0.6f;
            velZ *= 0.6f;
            angX *= 0.5f;
            angY *= 0.5f;
            angZ *= 0.5f;
        }
    }

    @Override
    public void dibujar(Pincel p) {
        if (vida <= 0) return;
        p.rotar(rotY);
        if (vida > 2.7f) {
            p.conBrillo(() -> {
                p.cubo(0, 0, 0, size, Color.NARANJA);
            });
        } else {
            p.cubo(0, 0, 0, size, Color.rgb(r, g, b));
        }
    }
    
    public boolean estaViva() {
        return vida > 0;
    }
}
