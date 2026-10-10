package com.graphics.modelos;

import com.graphics.core.Color;
import com.graphics.core.Renderizador;
import java.util.ArrayList;
import java.util.List;

public class Monte implements Entorno {
    private int vaoTerreno = -1;
    private int numVertices = 0;
    private static final float TAMANO = 300f;
    private static final int DIVISIONES = 100;
    private float[] posicionesFarolas = new float[96];

    public Monte(Renderizador renderizador) {
        generarTerreno(renderizador);
        for (int i = 0; i < posicionesFarolas.length; i++) {
            posicionesFarolas[i] = -1000f; // Sin farolas por ahora
        }
    }

    private float calcularAltura(float x, float z) {
        // Terreno super irregular (complejo)
        float h = (float)(Math.sin(x * 0.1f) * Math.cos(z * 0.1f)) * 5.0f;
        h += (float)(Math.sin(x * 0.03f + 1.5f) * Math.sin(z * 0.04f)) * 15.0f;
        h += (float)(Math.cos(x * 0.2f) * Math.sin(z * 0.15f)) * 2.0f;
        // Borde como pared para no caerse
        if (x > TAMANO/2 - 10 || x < -TAMANO/2 + 10 || z > TAMANO/2 - 10 || z < -TAMANO/2 + 10) {
            h += 20.0f; 
        }
        return h;
    }

    private void generarTerreno(Renderizador r) {
        List<Float> vertices = new ArrayList<>();
        float paso = TAMANO / DIVISIONES;
        float inicio = -TAMANO / 2;

        for (int i = 0; i < DIVISIONES; i++) {
            for (int j = 0; j < DIVISIONES; j++) {
                float x = inicio + i * paso;
                float z = inicio + j * paso;
                
                float x0z0 = calcularAltura(x, z);
                float x1z0 = calcularAltura(x + paso, z);
                float x0z1 = calcularAltura(x, z + paso);
                float x1z1 = calcularAltura(x + paso, z + paso);

                // Normal aproximada plana (hacia arriba Y=1) por simplicidad
                // Triangulo 1
                agregarTriangulo(vertices, x, x0z0, z, x, x0z1, z + paso, x + paso, x1z0, z);

                // Triangulo 2
                agregarTriangulo(vertices, x + paso, x1z0, z, x, x0z1, z + paso, x + paso, x1z1, z + paso);
            }
        }

        float[] array = new float[vertices.size()];
        for(int k=0; k<vertices.size(); k++) array[k] = vertices.get(k);
        
        vaoTerreno = r.crearMalla(array);
        numVertices = vertices.size() / 6; // 6 floats per vertex
    }

    private void agregarTriangulo(List<Float> v, float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3) {
        float ux = x2 - x1; float uy = y2 - y1; float uz = z2 - z1;
        float vx = x3 - x1; float vy = y3 - y1; float vz = z3 - z1;
        float nx = uy * vz - uz * vy;
        float ny = uz * vx - ux * vz;
        float nz = ux * vy - uy * vx;
        float len = (float)Math.sqrt(nx*nx + ny*ny + nz*nz);
        if (len > 0) { nx /= len; ny /= len; nz /= len; } else { ny = 1; }
        
        v.add(x1); v.add(y1); v.add(z1); v.add(nx); v.add(ny); v.add(nz);
        v.add(x2); v.add(y2); v.add(z2); v.add(nx); v.add(ny); v.add(nz);
        v.add(x3); v.add(y3); v.add(z3); v.add(nx); v.add(ny); v.add(nz);
    }

    @Override
    public void actualizar(float deltaTime) {
    }

    @Override
    public void dibujar(Renderizador renderizador) {
        if (vaoTerreno != -1) {
            renderizador.dibujarMalla(vaoTerreno, numVertices, Color.CESPED);
        }
    }

    @Override
    public boolean puedeCircular(Objeto auto, float velocidadAuto, float dirX, float dirZ) {
        return Math.abs(auto.getX()) < TAMANO/2 && Math.abs(auto.getZ()) < TAMANO/2;
    }

    @Override
    public boolean enAgua(float px, float pz) {
        return false;
    }

    @Override
    public float getAlturaSuelo(float px, float pz) {
        if (Math.abs(px) < TAMANO/2 && Math.abs(pz) < TAMANO/2) {
            return calcularAltura(px, pz);
        }
        return 0;
    }

    @Override
    public float getPendienteSuelo(float px, float pz) {
        float h1 = calcularAltura(px, pz);
        float h2 = calcularAltura(px + 0.1f, pz + 0.1f); // Aproximacion simple
        return Math.abs((h2 - h1) / 0.1414f);
    }

    @Override
    public float[] getPosicionesFarolas() {
        return posicionesFarolas;
    }
}
