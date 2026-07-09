/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logica;

import java.awt.Component;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * @author Anyel
 */
public class Paleta  implements Runnable {

  
    public enum Posicion {
        IZQUIERDA, DERECHA
    }

    private static final int MARGEN_LATERAL = 20;
    private static final int RETARDO_MOVIMIENTO_MS = 12;

    private final Posicion posicion;
    private final int ancho;
    private final int alto;
    private final int velocidad;
    private final Component areaJuego;

    private volatile int y;

    private final AtomicBoolean subiendo = new AtomicBoolean(false);
    private final AtomicBoolean bajando = new AtomicBoolean(false);
    private final AtomicBoolean pausado = new AtomicBoolean(false);
    private final AtomicBoolean corriendo = new AtomicBoolean(false);

    private Thread hilo;

    public Paleta(Posicion posicion, int ancho, int alto, int velocidad, Component areaJuego) {
        this.posicion = posicion;
        this.ancho = ancho;
        this.alto = alto;
        this.velocidad = velocidad;
        this.areaJuego = areaJuego;
        this.y = calcularPosicionCentral();
    }

    public void iniciar() {
        if (corriendo.compareAndSet(false, true)) {
            hilo = new Thread(this, "Hilo-Paleta-" + posicion);
            hilo.setDaemon(true);
            hilo.start();
        }
    }

    public void detener() {
        corriendo.set(false);
        if (hilo != null) {
            hilo.interrupt();
        }
    }

    public void setPausado(boolean valor) {
        pausado.set(valor);
    }

    public boolean isPausado() {
        return pausado.get();
    }

    @Override
    public void run() {
        while (corriendo.get()) {
            try {
                if (!pausado.get()) {
                    if (subiendo.get()) {
                        moverArriba();
                    }
                    if (bajando.get()) {
                        moverAbajo();
                    }
                }
                Thread.sleep(RETARDO_MOVIMIENTO_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public void setMoviendoArriba(boolean valor) {
        subiendo.set(valor);
    }

    public void setMoviendoAbajo(boolean valor) {
        bajando.set(valor);
    }

    public synchronized void moverArriba() {
        y = validarLimiteSuperior(y - velocidad);
    }

    public synchronized void moverAbajo() {
        y = validarLimiteInferior(y + velocidad);
    }

    private int validarLimiteSuperior(int nuevaY) {
        return Math.max(0, nuevaY);
    }

    private int validarLimiteInferior(int nuevaY) {
        int limiteInferior = Math.max(0, obtenerAltoTablero() - alto);
        return Math.min(limiteInferior, nuevaY);
    }

    public synchronized void reiniciarPosicion() {
        y = calcularPosicionCentral();
    }

    private int calcularPosicionCentral() {
        return Math.max(0, (obtenerAltoTablero() - alto) / 2);
    }

    private int obtenerAltoTablero() {
        int altoActual = areaJuego.getHeight();
        return altoActual > 0 ? altoActual : areaJuego.getPreferredSize().height;
    }

    private int obtenerAnchoTablero() {
        int anchoActual = areaJuego.getWidth();
        return anchoActual > 0 ? anchoActual : areaJuego.getPreferredSize().width;
    }

    public int getX() {
        if (posicion == Posicion.IZQUIERDA) {
            return MARGEN_LATERAL;
        }
        return Math.max(MARGEN_LATERAL, obtenerAnchoTablero() - MARGEN_LATERAL - ancho);
    }

    public int getY() {
        return y;
    }

    public int getAncho() {
        return ancho;
    }

    public int getAlto() {
        return alto;
    }

    public Posicion getPosicion() {
        return posicion;
    }
}

