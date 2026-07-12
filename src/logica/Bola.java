/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logica;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Random;

public class Bola implements Runnable {

     private volatile int x;
    private volatile int y;

    private volatile int dx;
    private volatile int dy;

    private static final int TAMANIO = 18;
    private static final int DURACION_CONGELAMIENTO_MS = 3000;

    /*
     * Un valor menor representa una mayor velocidad.
     */
    private volatile int pausaMovimientoMs;

    /*
     * Control de ejecución y pausa del hilo.
     */
    private volatile boolean activa = true;
    private volatile boolean pausada = false;

    /*
     * Control de efectos especiales.
     */
    private boolean fantasmaUsada = false;
    private boolean efectoRapidoAplicado = false;

    private final TipoBola tipo;
    private final Dificultad dificultad;

    private final Jugador jugadorIzquierdo;
    private final Jugador jugadorDerecho;

    private final javax.swing.JPanel panel;

    private final Random random = new Random();

    public Bola(
            javax.swing.JPanel panel,
            Jugador jugadorIzquierdo,
            Jugador jugadorDerecho,
            Dificultad dificultad) {

        this.panel = panel;
        this.jugadorIzquierdo = jugadorIzquierdo;
        this.jugadorDerecho = jugadorDerecho;

        this.dificultad = dificultad != null
                ? dificultad
                : Dificultad.NORMAL;

        int anchoPanel = obtenerAnchoPanel();
        int altoPanel = obtenerAltoPanel();

        /*
         * La bola inicia en el centro del tablero.
         */
        this.x = (anchoPanel - TAMANIO) / 2;
        this.y = (altoPanel - TAMANIO) / 2;

        /*
         * Dirección inicial aleatoria.
         */
        this.dx = random.nextBoolean() ? 4 : -4;
        this.dy = random.nextBoolean() ? 4 : -4;

        /*
         * La dificultad define la probabilidad
         * de que la bola sea especial.
         */
        this.tipo = generarTipoAleatorio(
                this.dificultad.getProbabilidadEspecial()
        );

        /*
         * La dificultad define la velocidad base.
         */
        this.pausaMovimientoMs
                = this.dificultad.getRetardoMovimiento();

        /*
         * Una bola rápida inicia con mayor velocidad.
         */
        if (tipo == TipoBola.RAPIDA) {

            dx = dx > 0 ? 6 : -6;
            dy = dy > 0 ? 6 : -6;

            pausaMovimientoMs = Math.max(
                    4,
                    pausaMovimientoMs - 3
            );
        }
    }

    /**
     * Cada bola trabaja mediante su propio hilo.
     */
    @Override
    public void run() {

        while (activa) {

            if (!pausada) {

                mover();
                panel.repaint();
            }

            try {

                Thread.sleep(pausaMovimientoMs);

            } catch (InterruptedException e) {

                activa = false;
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Actualiza la posición de la bola y revisa colisiones.
     */
    private void mover() {

        x += dx;
        y += dy;

        rebotarArribaAbajo();
        verificarChoquePaletas();
        verificarSalidaLateral();
    }

    /**
     * Rebote en los bordes superior e inferior.
     */
    private void rebotarArribaAbajo() {

        if (y <= 0) {

            y = 0;
            dy = Math.abs(dy);
        }

        int limiteInferior
                = obtenerAltoPanel() - TAMANIO;

        if (y >= limiteInferior) {

            y = Math.max(0, limiteInferior);
            dy = -Math.abs(dy);
        }
    }

    /**
     * Revisa las colisiones con ambas paletas.
     */
    private void verificarChoquePaletas() {

        Rectangle rectanguloBola = getRectangulo();

        Rectangle paletaIzquierda = new Rectangle(
                jugadorIzquierdo.getPaleta().getX(),
                jugadorIzquierdo.getPaleta().getY(),
                jugadorIzquierdo.getPaleta().getAncho(),
                jugadorIzquierdo.getPaleta().getAlto()
        );

        Rectangle paletaDerecha = new Rectangle(
                jugadorDerecho.getPaleta().getX(),
                jugadorDerecho.getPaleta().getY(),
                jugadorDerecho.getPaleta().getAncho(),
                jugadorDerecho.getPaleta().getAlto()
        );

        /*
         * Colisión con la paleta izquierda.
         */
        if (rectanguloBola.intersects(paletaIzquierda)
                && dx < 0) {

            /*
             * La bola fantasma atraviesa una paleta una sola vez.
             */
            if (tipo == TipoBola.FANTASMA
                    && !fantasmaUsada) {

                fantasmaUsada = true;
                return;
            }

            /*
             * Se reposiciona fuera de la paleta para evitar
             * colisiones repetidas.
             */
            x = jugadorIzquierdo.getPaleta().getX()
                    + jugadorIzquierdo.getPaleta().getAncho();

            dx = Math.abs(dx);

            /*
             * La paleta izquierda golpeó la bola.
             * El rival es el jugador derecho.
             */
            aplicarEfectoEspecial(jugadorDerecho);
        }

        /*
         * Colisión con la paleta derecha.
         */
        if (rectanguloBola.intersects(paletaDerecha)
                && dx > 0) {

            if (tipo == TipoBola.FANTASMA
                    && !fantasmaUsada) {

                fantasmaUsada = true;
                return;
            }

            x = jugadorDerecho.getPaleta().getX()
                    - TAMANIO;

            dx = -Math.abs(dx);

            /*
             * La paleta derecha golpeó la bola.
             * El rival es el jugador izquierdo.
             */
            aplicarEfectoEspecial(jugadorIzquierdo);
        }
    }

    /**
     * Detecta cuando la bola sale por alguno de los laterales.
     */
    private void verificarSalidaLateral() {

        /*
         * La bola salió por el lado izquierdo.
         * El punto corresponde al jugador derecho.
         */
        if (x + TAMANIO < 0) {

            aplicarPuntos(jugadorDerecho);
            activa = false;
        }

        /*
         * La bola salió por el lado derecho.
         * El punto corresponde al jugador izquierdo.
         */
        if (x > obtenerAnchoPanel()) {

            aplicarPuntos(jugadorIzquierdo);
            activa = false;
        }
    }

    /**
     * Aplica el puntaje según el tipo de bola.
     */
    private void aplicarPuntos(Jugador jugador) {

        switch (tipo) {

            case NORMAL:
                jugador.sumarPuntos(1);
                break;

            case NEGATIVA:
                jugador.sumarPuntos(-2);
                break;

            case BONUS:
                jugador.sumarPuntos(2);
                break;

            case RAPIDA:
            case FANTASMA:
            case CONGELANTE:
                jugador.sumarPuntos(1);
                break;

            default:
                jugador.sumarPuntos(1);
                break;
        }
    }

    /**
     * Aplica los efectos especiales al golpear una paleta.
     */
    private void aplicarEfectoEspecial(Jugador rival) {

        /*
         * La bola rápida aumenta su velocidad una sola vez.
         */
        if (tipo == TipoBola.RAPIDA
                && !efectoRapidoAplicado) {

            dx *= 2;
            dy *= 2;

            pausaMovimientoMs = Math.max(
                    3,
                    pausaMovimientoMs - 2
            );

            efectoRapidoAplicado = true;
        }

        /*
         * La bola congelante reduce la velocidad
         * de la paleta rival durante 3 segundos.
         */
        if (tipo == TipoBola.CONGELANTE) {

            rival.getPaleta().congelarDurante(
                    DURACION_CONGELAMIENTO_MS
            );
        }
    }

    /**
     * Genera una bola normal o especial según la dificultad.
     */
    private TipoBola generarTipoAleatorio(
            int probabilidadEspecial) {

        int decision = random.nextInt(100);

        /*
         * Si no se cumple la probabilidad,
         * se genera una bola normal.
         */
        if (decision >= probabilidadEspecial) {
            return TipoBola.NORMAL;
        }

        TipoBola[] bolasEspeciales = {
            TipoBola.NEGATIVA,
            TipoBola.BONUS,
            TipoBola.RAPIDA,
            TipoBola.FANTASMA,
            TipoBola.CONGELANTE
        };

        return bolasEspeciales[
                random.nextInt(bolasEspeciales.length)
        ];
    }

    /**
     * Dibuja la bola según su color.
     */
    public void dibujar(Graphics g) {

        g.setColor(obtenerColor());

        g.fillOval(
                x,
                y,
                TAMANIO,
                TAMANIO
        );
    }

    /**
     * Devuelve el color correspondiente al tipo de bola.
     */
    private Color obtenerColor() {

        switch (tipo) {

            case NORMAL:
                return Color.WHITE;

            case NEGATIVA:
                return Color.RED;

            case BONUS:
                return Color.BLUE;

            case RAPIDA:
                return Color.YELLOW;

            case FANTASMA:
                return new Color(128, 0, 128);

            case CONGELANTE:
                return Color.CYAN;

            default:
                return Color.WHITE;
        }
    }

    /**
     * Obtiene el ancho actual del panel.
     */
    private int obtenerAnchoPanel() {

        int ancho = panel.getWidth();

        if (ancho > 0) {
            return ancho;
        }

        if (panel.getPreferredSize() != null
                && panel.getPreferredSize().width > 0) {

            return panel.getPreferredSize().width;
        }

        return 900;
    }

    /**
     * Obtiene el alto actual del panel.
     */
    private int obtenerAltoPanel() {

        int alto = panel.getHeight();

        if (alto > 0) {
            return alto;
        }

        if (panel.getPreferredSize() != null
                && panel.getPreferredSize().height > 0) {

            return panel.getPreferredSize().height;
        }

        return 400;
    }

    /**
     * Rectángulo usado para detectar colisiones.
     */
    public Rectangle getRectangulo() {

        return new Rectangle(
                x,
                y,
                TAMANIO,
                TAMANIO
        );
    }

    public void setPausada(boolean pausada) {
        this.pausada = pausada;
    }

    public boolean isPausada() {
        return pausada;
    }

    public boolean isActiva() {
        return activa;
    }

    public void detener() {
        activa = false;
    }

    public TipoBola getTipo() {
        return tipo;
    }

    public Dificultad getDificultad() {
        return dificultad;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getTamanio() {
        return TAMANIO;
    }
}