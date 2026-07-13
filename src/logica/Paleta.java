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
     * Velocidad utilizada actualmente.
     * Puede reducirse temporalmente por una bola congelante.
     */
    private volatile int velocidadActual;

    private final Component areaJuego;

    /*
     * La posición vertical es modificada por el hilo de la paleta
     * y leída por el hilo gráfico de Swing.
     */
    private volatile int y;

    /*
     * AtomicBoolean permite controlar el movimiento,
     * la pausa y la ejecución desde distintos hilos.
     */
    private final AtomicBoolean subiendo
            = new AtomicBoolean(false);

    private final AtomicBoolean bajando
            = new AtomicBoolean(false);

    private final AtomicBoolean pausado
            = new AtomicBoolean(false);

    private final AtomicBoolean corriendo
            = new AtomicBoolean(false);

    /*
     * Identificador usado para coordinar varios efectos
     * congelantes consecutivos.
     */
    private final AtomicInteger numeroCongelacion
            = new AtomicInteger(0);

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

        this.velocidadBase = Math.max(1, velocidad);
        this.velocidadActual = this.velocidadBase;

        this.areaJuego = areaJuego;

        this.y = calcularPosicionCentral();
    }

    /**
     * Inicia el hilo independiente de la paleta.
     */
    public void iniciar() {

        /*
         * compareAndSet evita iniciar más de un hilo
         * para la misma paleta.
         */
        if (corriendo.compareAndSet(false, true)) {

            hilo = new Thread(
                    this,
                    "Hilo-Paleta-" + posicion
            );

            hilo.setDaemon(true);
            hilo.start();
        }
    }

    /**
     * Detiene completamente el hilo de la paleta.
     */
    public void detener() {

        corriendo.set(false);

        subiendo.set(false);
        bajando.set(false);
        pausado.set(false);

        if (hilo != null) {
            hilo.interrupt();
            hilo = null;
        }
    }

    /**
     * Pausa o reanuda el movimiento de la paleta.
     */
    public void setPausado(boolean valor) {

        pausado.set(valor);

        /*
         * Al pausar se detienen las órdenes actuales
         * para evitar que la paleta continúe moviéndose.
         */
        if (valor) {
            subiendo.set(false);
            bajando.set(false);
        }
    }

    public boolean isPausado() {
        return pausado.get();
    }

    public boolean isCorriendo() {
        return corriendo.get();
    }

    /**
     * Ciclo independiente de movimiento de la paleta.
     */
    @Override
    public void run() {

        while (corriendo.get()) {

            try {

                if (!pausado.get()) {

                    if (subiendo.get()
                            && !bajando.get()) {

                        moverArriba();
                    }

                    if (bajando.get()
                            && !subiendo.get()) {

                        moverAbajo();
                    }
                }

                Thread.sleep(
                        RETARDO_MOVIMIENTO_MS
                );

            } catch (InterruptedException e) {

                Thread.currentThread().interrupt();
                break;
            }
        }

        corriendo.set(false);
    }

    /**
     * Activa o desactiva el movimiento hacia arriba.
     */
    public void setMoviendoArriba(boolean valor) {
        subiendo.set(valor);
    }

    /**
     * Activa o desactiva el movimiento hacia abajo.
     */
    public void setMoviendoAbajo(boolean valor) {
        bajando.set(valor);
    }

    /**
     * Mueve la paleta hacia arriba respetando el límite.
     */
    public synchronized void moverArriba() {

        y = validarLimiteSuperior(
                y - velocidadActual
        );
    }

    /**
     * Mueve la paleta hacia abajo respetando el límite.
     */
    public synchronized void moverAbajo() {

        y = validarLimiteInferior(
                y + velocidadActual
        );
    }

    private int validarLimiteSuperior(
            int nuevaY) {

        return Math.max(
                0,
                nuevaY
        );
    }

    private int validarLimiteInferior(
            int nuevaY) {

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
     * Reduce la velocidad de esta paleta durante
     * una cantidad determinada de milisegundos.
     *
     * @param milisegundos duración del congelamiento
     */
    public void congelarDurante(
            int milisegundos) {

        /*
         * No se inicia un efecto con duración inválida.
         */
        if (milisegundos <= 0) {
            return;
        }

        /*
         * Cada congelamiento obtiene un número único.
         */
        int congelacionActual
                = numeroCongelacion.incrementAndGet();

        /*
         * Reduce la velocidad aproximadamente a la mitad.
         */
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
             * Solo el efecto congelante más reciente
             * puede restaurar la velocidad normal.
             */
            if (numeroCongelacion.get()
                    == congelacionActual) {

                velocidadActual = velocidadBase;
            }

        }, "Efecto-Congelante-" + posicion
                + "-" + congelacionActual);

        hiloCongelacion.setDaemon(true);
        hiloCongelacion.start();
    }

    /**
     * Cancela cualquier efecto congelante activo.
     */
    public void cancelarCongelamiento() {

        /*
         * Invalida los hilos de congelamiento anteriores.
         */
        numeroCongelacion.incrementAndGet();

        velocidadActual = velocidadBase;
    }

    /**
     * Reinicia la posición y el estado de la paleta.
     */
    public synchronized void reiniciarPosicion() {

        y = calcularPosicionCentral();

        subiendo.set(false);
        bajando.set(false);
        pausado.set(false);

        cancelarCongelamiento();
    }

    private int calcularPosicionCentral() {

        return Math.max(
                0,
                (obtenerAltoTablero() - alto) / 2
        );
    }

    /**
     * Obtiene el alto real del tablero.
     */
    private int obtenerAltoTablero() {

        int altoActual
                = areaJuego.getHeight();

        if (altoActual > 0) {
            return altoActual;
        }

        if (areaJuego.getPreferredSize() != null
                && areaJuego.getPreferredSize().height > 0) {

            return areaJuego
                    .getPreferredSize()
                    .height;
        }

        return 400;
    }

    /**
     * Obtiene el ancho real del tablero.
     */
    private int obtenerAnchoTablero() {

        int anchoActual
                = areaJuego.getWidth();

        if (anchoActual > 0) {
            return anchoActual;
        }

        if (areaJuego.getPreferredSize() != null
                && areaJuego.getPreferredSize().width > 0) {

            return areaJuego
                    .getPreferredSize()
                    .width;
        }

        return 900;
    }

    /**
     * Calcula la posición horizontal según el lado.
     */
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

        return velocidadActual
                < velocidadBase;
    }

    public boolean isMoviendoArriba() {
        return subiendo.get();
    }

    public boolean isMoviendoAbajo() {
        return bajando.get();
    }
}