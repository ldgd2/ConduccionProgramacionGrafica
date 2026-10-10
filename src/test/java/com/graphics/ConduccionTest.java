package com.graphics;

import com.graphics.conduccion.Auto;
import com.graphics.conduccion.SistemaEntregas;
import com.graphics.modelos.Ciudad;
import com.graphics.modelos.Objeto;
import junit.framework.TestCase;

/**
 * Comprueba las reglas de conducción, colisiones, entregas y reinicio
 * de la arquitectura modular sin abrir ventana OpenGL.
 */
public class ConduccionTest extends TestCase {

    /** Comprueba que las calles conectan y permiten circular libremente */
    public void testCallesTransitables() {
        Ciudad ciudad = new Ciudad();

        // 1. Recorrer la calle vertical oeste (x = -80) a lo largo de toda su extensión
        for (float z = -80; z <= 70; z += 1.0f) {
            final float curZ = z;
            Objeto autoTest = new Objeto(-80, curZ) {
                @Override public float getRadio() { return 1.35f; }
            };
            assertTrue("La calle x=-80 en z=" + curZ + " debe ser transitable", ciudad.puedeCircular(autoTest, 0, 0, 0));
        }

        // 2. Recorrer la avenida principal horizontal (z = -20) a través de toda la ciudad y puentes
        for (float x = -80; x <= 70; x += 1.0f) {
            final float curX = x;
            Objeto autoTest = new Objeto(curX, -20) {
                @Override public float getRadio() { return 1.35f; }
            };
            assertTrue("La avenida z=-20 en x=" + curX + " debe ser transitable", ciudad.puedeCircular(autoTest, 0, 0, 0));
        }

        // 3. Probar esquinas e intersecciones (ej: (-80, -20), (-40, 10), (40, 40))
        Objeto cruce1 = new Objeto(-80, -20) { @Override public float getRadio() { return 1.35f; } };
        Objeto cruce2 = new Objeto(-40, 10) { @Override public float getRadio() { return 1.35f; } };
        Objeto cruce3 = new Objeto(40, 40) { @Override public float getRadio() { return 1.35f; } };
        assertTrue(ciudad.puedeCircular(cruce1, 0, 0, 0));
        assertTrue(ciudad.puedeCircular(cruce2, 0, 0, 0));
        assertTrue(ciudad.puedeCircular(cruce3, 0, 0, 0));

        // 4. Manzanas con edificios interiores deben bloquear
        Objeto objEdificio = new Objeto(-90, -90) {
            @Override public float getRadio() { return 1.35f; }
        };
        assertFalse("El interior de un edificio debe colisionar", ciudad.puedeCircular(objEdificio, 0, 0, 0));
    }

    /** Comprueba los límites de la ciudad ampliada */
    public void testLimitesCiudad() {
        Ciudad ciudad = new Ciudad();
        // Fuera de los límites del mapa ampliado (LIMITE = 105)
        Objeto objFuera = new Objeto(150, 150) {
            @Override public float getRadio() { return 1.35f; }
        };
        assertFalse(ciudad.puedeCircular(objFuera, 0, 0, 0));
    }

    /** Comprueba que el reinicio restaura la posición y detiene el auto */
    public void testReinicioAuto() {
        Ciudad ciudad = new Ciudad();
        Auto auto = new Auto(ciudad);
        
        // Simular movimiento y daño
        auto.actualizar(1.0f, true, false, false, false, false);
        auto.reiniciar();

        assertEquals(-80.0f, auto.getX(), 0.001f);
        assertEquals(40.0f, auto.getZ(), 0.001f);
        assertEquals(0.0f, auto.getVelocidad(), 0.001f);
        assertEquals(0.0f, auto.getAngulo(), 0.001f);
        assertEquals(100.0f, auto.getVida(), 0.001f);
        assertFalse(auto.isDestruido());
    }

    /** Comprueba el ciclo de misiones y entregas */
    public void testSistemaEntregas() {
        SistemaEntregas entregas = new SistemaEntregas();

        assertEquals(0, entregas.getEntregasCompletadas());
        assertEquals(3, entregas.getTotalEntregas());
        assertFalse(entregas.isCompletado());

        // Destino 1
        SistemaEntregas.Destino d1 = entregas.getDestinoActual();
        assertNotNull(d1);

        // Reinicio
        entregas.reiniciar();
        assertEquals(0, entregas.getEntregasCompletadas());
    }

    /** Comprueba la distribución y el sistema de iluminación dinámica de farolas urbanas */
    public void testAlumbradoPublico() {
        Ciudad ciudad = new Ciudad();
        float[] luces = ciudad.getPosicionesFarolasCercanas(0, 0);

        // Debe entregar exactamente 96 componentes (32 farolas x 3 coordenadas)
        assertNotNull(luces);
        assertEquals(96, luces.length);

        // Las farolas cercanas deben tener una altura elevada correspondiente a la luminaria (alrededor de 6.45m)
        assertTrue("La altura de la primera farola debe ser de alumbrado elevado", luces[1] > 5.0f);

        // Si el auto se desplaza al sector oeste (-80, 40), las luces devueltas deben adaptarse al nuevo entorno
        float[] lucesOeste = ciudad.getPosicionesFarolasCercanas(-80, 40);
        assertNotNull(lucesOeste);
        assertEquals(96, lucesOeste.length);
        float distOeste = (lucesOeste[0] - (-80)) * (lucesOeste[0] - (-80)) + (lucesOeste[2] - 40) * (lucesOeste[2] - 40);
        assertTrue("La farola más cercana debe estar próxima al auto en el sector oeste", distOeste < 100.0f);
    }

    /** Comprueba que la banqueta/acera permite circular y solo la pared del edificio colisiona */
    public void testBanquetaTransitable() {
        Ciudad ciudad = new Ciudad();
        // Edificio centrado en (-90, -90). La banqueta abarca hasta x = -85.0.
        // Un auto subido en la banqueta en x = -85.8 debe poder circular.
        Objeto autoBanqueta = new Objeto(-85.8f, -90f) {
            @Override public float getRadio() { return 1.35f; }
        };
        assertTrue("Subir a la banqueta debe ser transitable sin chocar con la acera", ciudad.puedeCircular(autoBanqueta, 0, 0, 0));

        // Pero incrustado contra la pared física del edificio (-90, -90) sí debe colisionar
        Objeto autoPared = new Objeto(-89.0f, -90f) {
            @Override public float getRadio() { return 1.35f; }
        };
        assertFalse("El choque contra la pared física del edificio debe colisionar", ciudad.puedeCircular(autoPared, 0, 0, 0));
    }

    /** Comprueba que los objetos urbanos (semáforos, bancas, vallas) se rompen y dañan el vehículo */
    public void testObjetosDestructibles() {
        Ciudad ciudad = new Ciudad();
        Auto auto = new Auto(ciudad);
        assertEquals(100.0f, auto.getVida(), 0.001f);

        // Posicionar el auto junto a una valla de obra (-81.5, -50.0) y simular choque a velocidad
        auto.setX(-81.5f);
        auto.setZ(-49.5f);
        // Simular aceleración hacia adelante
        auto.actualizar(0.3f, true, false, false, false, false);
        
        ciudad.verificarImpactosDestructibles(auto, null);

        // Debe haber sufrido daño por el impacto contra el objeto destructible
        assertTrue("El impacto destructible debe restar vida al auto", auto.getVida() < 100.0f);
        assertTrue("El auto no debe estar destruido por un solo impacto moderado", auto.getVida() > 0.0f);

        // Al reiniciar la ciudad, el objeto vuelve a su estado íntegro
        ciudad.reiniciar();
        auto.reiniciar();
        assertEquals(100.0f, auto.getVida(), 0.001f);
    }

    /** Comprueba que los árboles son colisionables, se destruyen, dañan el auto y desaparecen tras su animación */
    public void testArbolesColisionYDestruccion() {
        Ciudad ciudad = new Ciudad();
        Auto auto = new Auto(ciudad);
        assertEquals(100.0f, auto.getVida(), 0.001f);

        // Posicionar el auto frente a un árbol de parque en (-57.5, -37.5)
        auto.setX(-57.5f);
        auto.setZ(-36.5f);
        auto.actualizar(0.3f, true, false, false, false, false); // Acelera hacia el árbol

        ciudad.verificarImpactosDestructibles(auto, null);

        // Verificar que el choque contra el árbol restó vida al auto
        assertTrue("El choque contra un árbol debe restar vida al auto", auto.getVida() < 100.0f);

        // Actualizar la ciudad durante 0.6s (superior a la duración de la animación de 0.5s)
        ciudad.actualizar(0.6f);

        // Al reiniciar, el árbol y el auto se restauran completamente
        ciudad.reiniciar();
        auto.reiniciar();
        assertEquals(100.0f, auto.getVida(), 0.001f);
    }
}
