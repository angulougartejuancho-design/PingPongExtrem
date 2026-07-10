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

    private int x;
    private int y;

    private int dx;
    private int dy;

    private static final int TAMANIO = 18;
    private static final int DURACION_CONGELAMIENTO_MS = 3000;

    private int pausaMovimientoMs;

    private volatile boolean activa = true;
    private volatile boolean pausada = false;

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

        this.x = (anchoPanel - TAMANIO) / 2;
        this.y = (altoPanel - TAMANIO) / 2;

        /*
         * Dirección aleatoria.
         */
        this.dx = random.nextBoolean() ? 4 : -4;
        this.dy = random.nextBoolean() ? 4 : -4;

        this.tipo = generarTipoAleatorio(
                this.dificultad.getProbabilidadEspecial()
        );

        this.pausaMovimientoMs
                = this.dificultad.getRetardoMovimiento();

        /*
         * Una bola rápida comienza con mayor velocidad.
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

    private void mover() {

        x += dx;
        y += dy;

        rebotarArribaAbajo();
        verificarChoquePaletas();
        verificarSalidaLateral();
    }

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
         * Colisión con paleta izquierda.
         */
        if (rectanguloBola.intersects(paletaIzquierda)
                && dx < 0) {

            if (tipo == TipoBola.FANTASMA
                    && !fantasmaUsada) {

                fantasmaUsada = true;
                return;
            }

            x = jugadorIzquierdo.getPaleta().getX()
                    + jugadorIzquierdo.getPaleta().getAncho();

            dx = Math.abs(dx);

            /*
             * La paleta izquierda golpeó la bola,
             * por lo tanto el rival es el jugador derecho.
             */
            aplicarEfectoEspecial(jugadorDerecho);
        }

        /*
         * Colisión con paleta derecha.
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
             * La paleta derecha golpeó la bola,
             * por lo tanto el rival es el jugador izquierdo.
             */
            aplicarEfectoEspecial(jugadorIzquierdo);
        }
    }

    private void verificarSalidaLateral() {

        /*
         * Sale por el lado izquierdo.
         */
        if (x + TAMANIO < 0) {

            aplicarPuntos(jugadorDerecho);
            activa = false;
        }

        /*
         * Sale por el lado derecho.
         */
        if (x > obtenerAnchoPanel()) {

            aplicarPuntos(jugadorIzquierdo);
            activa = false;
        }
    }

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

    private void aplicarEfectoEspecial(Jugador rival) {

        /*
         * La bola rápida aumenta su velocidad una sola vez
         * después de una colisión.
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

    private TipoBola generarTipoAleatorio(
            int probabilidadEspecial) {

        int decision = random.nextInt(100);

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

    public void dibujar(Graphics g) {

        g.setColor(obtenerColor());

        g.fillOval(
                x,
                y,
                TAMANIO,
                TAMANIO
        );
    }

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
}