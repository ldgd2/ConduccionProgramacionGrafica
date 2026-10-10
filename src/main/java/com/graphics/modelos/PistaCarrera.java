package com.graphics.modelos;

import com.graphics.core.Color;
import com.graphics.core.Pincel;
import com.graphics.core.Renderizador;
import java.util.ArrayList;
import java.util.List;

public class PistaCarrera implements Entorno {
    private List<Objeto> objetos = new ArrayList<>();
    private float[] posicionesFarolas = new float[27];

    public PistaCarrera() {
        // Pista base plana
        objetos.add(new Objeto(0, 0) {
            @Override public void dibujar(Pincel p) {
                // Suelo asfalto central gigante
                p.caja(0, -0.5f, 0, 400, 1f, 400, ASFALTO);
            }
            @Override public float getRadio() { return 0; }
        });

        // Algunas rampas de salto de distintos ángulos
        objetos.add(new RampaEstatica(50, -50, 20, 40, 15, 0)); // 15 grados X
        objetos.add(new RampaEstatica(-50, 50, 20, 60, 25, 1)); // 25 grados Z
        objetos.add(new RampaEstatica(50, 50, 40, 20, 35, 0)); // 35 grados X extremo

        java.util.Arrays.fill(posicionesFarolas, -1000f);
    }

    @Override
    public void actualizar(float deltaTime) {}

    @Override
    public void dibujar(Renderizador renderizador) {
        for (Objeto o : objetos) o.dibujar(renderizador);
    }

    @Override
    public boolean puedeCircular(Objeto auto, float velocidadAuto, float dirX, float dirZ) {
        return Math.abs(auto.getX()) < 200 && Math.abs(auto.getZ()) < 200;
    }

    @Override
    public boolean enAgua(float px, float pz) { return false; }

    @Override
    public float getAlturaSuelo(float px, float pz) {
        float maxH = 0;
        for (Objeto o : objetos) {
            if (o instanceof Rampa) {
                float h = ((Rampa)o).getAlturaEn(px, pz);
                if (h > maxH) maxH = h;
            }
        }
        return maxH;
    }

    @Override
    public float getPendienteSuelo(float px, float pz) {
        for (Objeto o : objetos) {
            if (o instanceof Rampa) {
                float h = ((Rampa)o).getAlturaEn(px, pz);
                if (h > 0) return ((Rampa)o).getPendiente();
            }
        }
        return 0;
    }

    @Override
    public float[] getPosicionesFarolas() {
        return posicionesFarolas;
    }

    // Clase auxiliar para la rampa estatica
    class RampaEstatica extends Objeto implements Rampa {
        float ancho, largo, angulo, dirZ; // dirZ = 1 si apunta en Z, 0 si en X
        public RampaEstatica(float x, float z, float ancho, float largo, float grados, float dirZ) {
            super(x, z);
            this.ancho = ancho;
            this.largo = largo;
            this.angulo = (float)Math.toRadians(grados);
            this.dirZ = dirZ;
        }
        @Override public float getRadio() { return 0; }
        @Override public boolean colisiona(Objeto otro) { return false; }
        @Override public float getAlturaEn(float px, float pz) {
            if (dirZ == 1) {
                float dz = pz - (this.z - largo / 2);
                if (dz >= 0 && dz <= largo && Math.abs(px - this.x) < ancho / 2) return (float)(Math.sin(angulo) * dz);
            } else {
                float dx = px - (this.x - largo / 2);
                if (dx >= 0 && dx <= largo && Math.abs(pz - this.z) < ancho / 2) return (float)(Math.sin(angulo) * dx);
            }
            return 0;
        }
        @Override public float getPendiente() { return (float)Math.tan(angulo); }
        @Override public void dibujar(Renderizador r) {
            float cy = (float)(Math.sin(angulo) * largo / 2);
            if (dirZ == 1) {
                float cz = this.z - largo / 2 + (float)(Math.cos(angulo) * largo / 2);
                r.cajaRampa(this.x, cy, cz, ancho, 1.0f, largo, ROJO, 0, -angulo);
            } else {
                float cx = this.x - largo / 2 + (float)(Math.cos(angulo) * largo / 2);
                r.cajaRampa(cx, cy, this.z, largo, 1.0f, ancho, ROJO, (float)Math.PI/2, -angulo);
            }
        }
    }
}
