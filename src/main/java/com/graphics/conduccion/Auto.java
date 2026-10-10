package com.graphics.conduccion;

import com.graphics.modelos.Ciudad;
import com.graphics.modelos.Entorno;
import com.graphics.modelos.Objeto;

public class Auto extends Objeto {
    protected float angulo = 0;
    private float velocidad = 0;
    private float velocidadY = 0;
    private static final float RADIO_AUTO = 1.65f;
    private Entorno entorno;
    
    // Sistema de Vida y Destruccion
    private float vida = 100.0f;
    private boolean destruido = false;
    
    // Aceleracion y Turbo
    private float tiempoAcelerando = 0;
    private boolean turbo = false;

    public Auto(Entorno entorno) {
        super(-80, 40);
        this.entorno = entorno;
    }

    public Entorno getEntorno() {
        return entorno;
    }
    
    public void setEntorno(Entorno entorno) {
        this.entorno = entorno;
        reiniciar();
    }
    
    public float getVida() { return vida; }
    public boolean isDestruido() { return destruido; }
    
    public void setTurbo(boolean t) { this.turbo = t; }
    
    // Método para que Motor lo llame para explotar
    public void explotar() {
        if (!destruido) {
            vida = 0;
            destruido = true;
        }
    }

    public void actualizar(float deltaTime, boolean adelante, boolean atras, boolean izquierda, boolean derecha, boolean freno) {
        if (destruido) return; // Si explota, ya no se controla ni se mueve normalmente
        
        float alturaSuelo = entorno.getAlturaSuelo(x, z);
        
        // Gravedad y salto
        if (y > alturaSuelo || entorno.enAgua(x, z)) {
            velocidadY -= 30f * deltaTime; // Gravedad
            y += velocidadY * deltaTime;
            
            // Movimiento inercial en el aire
            x += -(float) Math.sin(angulo) * velocidad * deltaTime;
            z += -(float) Math.cos(angulo) * velocidad * deltaTime;
            
            if (y <= alturaSuelo && !entorno.enAgua(x, z)) {
                y = alturaSuelo; // Aterrizó
                if (velocidadY < -15f) {
                    // Daño por caida fuerte
                    vida += velocidadY * 0.5f; 
                    if (vida <= 0) { vida = 0; destruido = true; }
                }
                velocidadY = 0;
            }
            
            if (y < -30) reiniciar();
            return; // No se puede acelerar ni girar en el aire
        } else if (y < alturaSuelo) {
            // Subiendo rampa
            y = alturaSuelo;
            // Para saltar, transferimos parte de la velocidad horizontal a vertical si la rampa termina
            velocidadY = Math.abs(velocidad) * entorno.getPendienteSuelo(x, z);
        }

        float acelerador = 0;
        if (adelante) acelerador += 1;
        if (atras) acelerador -= 1;

        if (adelante && velocidad >= 0) {
            tiempoAcelerando += deltaTime;
        } else {
            tiempoAcelerando = 0;
        }

        float aceleracionBase = 10f + Math.min(tiempoAcelerando * 5f, 15f); // Aceleración progresiva
        if (turbo) aceleracionBase += 25f; // Empuje extra del turbo

        velocidad += acelerador * aceleracionBase * deltaTime;
        
        float resistencia = freno ? 7f : 0.7f;
        
        float factorFrenado = (float) Math.exp(-resistencia * deltaTime);
        velocidad *= factorFrenado;
        
        float maxVel = turbo ? 85f : 55.5f; // 55.5 m/s = 200 km/h, Turbo llega más lejos
        velocidad = Math.max(-15f, Math.min(maxVel, velocidad));

        float direccion = 0;
        if (izquierda) direccion += 1;
        if (derecha) direccion -= 1;

        angulo += direccion * velocidad * 0.11f * deltaTime;
        
        float frenteX = -(float) Math.sin(angulo);
        float frenteZ = -(float) Math.cos(angulo);
        
        float siguienteX = x + frenteX * velocidad * deltaTime;
        float siguienteZ = z + frenteZ * velocidad * deltaTime;

        boolean colisionX = false;
        boolean colisionZ = false;

        Objeto objX = new Objeto(siguienteX, z) {
            @Override public float getRadio() { return RADIO_AUTO; }
        };
        if (!entorno.puedeCircular(objX, velocidad, frenteX, 0)) {
            colisionX = true;
        }

        Objeto objZ = new Objeto(x, siguienteZ) {
            @Override public float getRadio() { return RADIO_AUTO; }
        };
        if (!entorno.puedeCircular(objZ, velocidad, 0, frenteZ)) {
            colisionZ = true;
        }

        // Caso de esquina: si X y Z por separado no chocan pero en conjunto si
        if (!colisionX && !colisionZ) {
            Objeto objAmbos = new Objeto(siguienteX, siguienteZ) {
                @Override public float getRadio() { return RADIO_AUTO; }
            };
            if (!entorno.puedeCircular(objAmbos, velocidad, frenteX, frenteZ)) {
                colisionX = true;
                colisionZ = true;
            }
        }

        if (!colisionX) {
            x = siguienteX;
        }
        if (!colisionZ) {
            z = siguienteZ;
        }

        if (colisionX || colisionZ) {
            float impulsoChoque = 0;
            if (colisionX) impulsoChoque = Math.max(impulsoChoque, Math.abs(velocidad * frenteX));
            if (colisionZ) impulsoChoque = Math.max(impulsoChoque, Math.abs(velocidad * frenteZ));

            // Si el impacto frontal/lateral es fuerte, entonces dañar
            if (impulsoChoque > 3.0f && vida > 0) {
                vida -= impulsoChoque * 3.0f; // Chocar duro duele mucho
                if (vida <= 0) {
                    vida = 0;
                    destruido = true;
                }
                // Rebote elástico debido al fuerte impacto
                velocidad = -velocidad * 0.5f;
            } else {
                // Solo rozando (sliding): no quita vida pero hay fricción
                velocidad *= 0.95f; 
            }
        }
    }

    public void reiniciar() {
        x = -80;
        y = 0;
        z = 40;
        angulo = 0;
        velocidad = 0;
        velocidadY = 0;
        vida = 100.0f;
        destruido = false;
    }

    public float getAngulo() { return angulo; }
    public float getVelocidad() { return velocidad; }
}

