/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logica;

import java.awt.Component;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Paleta implements Runnable {

    public enum Posicion {
        IZQUIERDA,
        DERECHA
    }

    private static final int MARGEN_LATERAL = 20;
    private static final int RETARDO_MOVIMIENTO_MS = 12;

    private final Posicion posicion;
    private final int ancho;
    private final int alto;

    /*
     * Velocidad normal configurada al crear la paleta.
     */
    private final int velocidadBase;

    /*
     * Velocidad que se usa actualmente.
     * Puede reducirse temporalmente con la bola congelante.
     */
    private volatile int velocidadActual;

    private final Component areaJuego;

    private volatile int y;

    private final AtomicBoolean subiendo = new AtomicBoolean(false);
    private final AtomicBoolean bajando = new AtomicBoolean(false);
    private final AtomicBoolean pausado = new AtomicBoolean(false);
    private final AtomicBoolean corriendo = new AtomicBoolean(false);

    /*
     * Permite controlar efectos congelantes consecutivos.
     */
    private final AtomicInteger numeroCongelacion = new AtomicInteger(0);

    private Thread hilo;

    public Paleta(
            Posicion posicion,
            int ancho,
            int alto,
            int velocidad,
            Component areaJuego) {

        this.posicion = posicion;
        this.ancho = ancho;
        this.alto = alto;
        this.velocidadBase = velocidad;
        this.velocidadActual = velocidad;
        this.areaJuego = areaJuego;
        this.y = calcularPosicionCentral();
    }

    public void iniciar() {

        if (corriendo.compareAndSet(false, true)) {

            hilo = new Thread(
                    this,
                    "Hilo-Paleta-" + posicion
            );

            hilo.setDaemon(true);
            hilo.start();
        }
    }

    public void detener() {

        corriendo.set(false);

        subiendo.set(false);
        bajando.set(false);

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

        corriendo.set(false);
    }

    public void setMoviendoArriba(boolean valor) {
        subiendo.set(valor);
    }

    public void setMoviendoAbajo(boolean valor) {
        bajando.set(valor);
    }

    public synchronized void moverArriba() {

        y = validarLimiteSuperior(
                y - velocidadActual
        );
    }

    public synchronized void moverAbajo() {

        y = validarLimiteInferior(
                y + velocidadActual
        );
    }

    private int validarLimiteSuperior(int nuevaY) {
        return Math.max(0, nuevaY);
    }

    private int validarLimiteInferior(int nuevaY) {

        int limiteInferior = Math.max(
                0,
                obtenerAltoTablero() - alto
        );

        return Math.min(
                limiteInferior,
                nuevaY
        );
    }

    /**
     * Reduce la velocidad de esta paleta durante el tiempo indicado.
     *
     * @param milisegundos duración del efecto
     */
    public void congelarDurante(int milisegundos) {

        /*
         * Cada congelamiento obtiene un número diferente.
         * Así un efecto anterior no restaura la velocidad antes de tiempo.
         */
        int congelacionActual
                = numeroCongelacion.incrementAndGet();

        velocidadActual = Math.max(
                1,
                velocidadBase / 2
        );

        Thread hiloCongelacion = new Thread(() -> {

            try {

                Thread.sleep(milisegundos);

            } catch (InterruptedException e) {

                Thread.currentThread().interrupt();
                return;
            }

            /*
             * Solo la congelación más reciente puede restaurar
             * la velocidad normal.
             */
            if (numeroCongelacion.get() == congelacionActual) {
                velocidadActual = velocidadBase;
            }

        }, "Efecto-Congelante-" + posicion);

        hiloCongelacion.setDaemon(true);
        hiloCongelacion.start();
    }

    public synchronized void reiniciarPosicion() {

        y = calcularPosicionCentral();

        subiendo.set(false);
        bajando.set(false);

        velocidadActual = velocidadBase;

        /*
         * Invalida cualquier hilo congelante anterior.
         */
        numeroCongelacion.incrementAndGet();
    }

    private int calcularPosicionCentral() {

        return Math.max(
                0,
                (obtenerAltoTablero() - alto) / 2
        );
    }

    private int obtenerAltoTablero() {

        int altoActual = areaJuego.getHeight();

        if (altoActual > 0) {
            return altoActual;
        }

        if (areaJuego.getPreferredSize() != null
                && areaJuego.getPreferredSize().height > 0) {

            return areaJuego.getPreferredSize().height;
        }

        return 400;
    }

    private int obtenerAnchoTablero() {

        int anchoActual = areaJuego.getWidth();

        if (anchoActual > 0) {
            return anchoActual;
        }

        if (areaJuego.getPreferredSize() != null
                && areaJuego.getPreferredSize().width > 0) {

            return areaJuego.getPreferredSize().width;
        }

        return 900;
    }

    public int getX() {

        if (posicion == Posicion.IZQUIERDA) {
            return MARGEN_LATERAL;
        }

        return Math.max(
                MARGEN_LATERAL,
                obtenerAnchoTablero()
                - MARGEN_LATERAL
                - ancho
        );
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

    public int getVelocidadBase() {
        return velocidadBase;
    }

    public int getVelocidadActual() {
        return velocidadActual;
    }

    public boolean isCongelada() {
        return velocidadActual < velocidadBase;
    }
}