package com.graphics.modelos;

import com.graphics.core.Color;
import com.graphics.core.Pincel;
import com.graphics.core.Renderizador;
import com.graphics.conduccion.Auto;

public class AutoModelo {
    private Auto auto;

    private float rR = 0.7f, rG = 0.1f, rB = 0.15f;

    public AutoModelo(Auto auto) {
        this.auto = auto;
    }

    public void setColorChasis(float r, float g, float b) {
        this.rR = r;
        this.rG = g;
        this.rB = b;
    }

    public void dibujar(Renderizador renderizador) {
        Pincel p = renderizador.en(auto.getX(), auto.getY(), auto.getZ()).rotar(auto.getAngulo());
        Color chasis = Color.rgb(rR, rG, rB);

        // Chasis base
        p.caja(0, 0.45f, 0, 2.2f, 0.5f, 4.8f, chasis);
        
        // Cabina y vidrios oscuros
        p.caja(0, 1.0f, -0.4f, 1.8f, 0.6f, 2.4f, Color.VIDRIO);
        
        // Techo del color del chasis
        p.caja(0, 1.35f, -0.4f, 1.6f, 0.1f, 1.8f, chasis);

        // Alerón trasero deportivo
        p.caja(0, 0.85f, 2.2f, 2.0f, 0.1f, 0.4f, Color.METAL_OSCURO);
        p.poste(-0.8f, 0.4f, 2.2f, 0.1f, 0.4f, Color.METAL_OSCURO);
        p.poste(0.8f, 0.4f, 2.2f, 0.1f, 0.4f, Color.METAL_OSCURO);

        // Llantas
        p.caja(-1.2f, 0.4f, -1.5f, 0.4f, 0.9f, 0.9f, Color.LLANTA);
        p.caja(1.2f, 0.4f, -1.5f, 0.4f, 0.9f, 0.9f, Color.LLANTA);
        p.caja(-1.2f, 0.4f, 1.5f, 0.4f, 0.9f, 0.9f, Color.LLANTA);
        p.caja(1.2f, 0.4f, 1.5f, 0.4f, 0.9f, 0.9f, Color.LLANTA);

        // Faros delanteros y luces traseras (emiten luz)
        p.conBrillo(() -> {
            p.caja(-0.7f, 0.55f, -2.45f, 0.5f, 0.25f, 0.1f, Color.LUZ_FARO_AUTO);
            p.caja(0.7f, 0.55f, -2.45f, 0.5f, 0.25f, 0.1f, Color.LUZ_FARO_AUTO);
            p.caja(-0.7f, 0.55f, 2.45f, 0.6f, 0.25f, 0.1f, Color.LUZ_FRENO);
            p.caja(0.7f, 0.55f, 2.45f, 0.6f, 0.25f, 0.1f, Color.LUZ_FRENO);
        });
    }
}
