/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logica;


import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Random;
/**
 *
 * @author PC
 */
public class Bola implements Runnable {

    private int x, y;
    private int dx, dy;
    private int tamanio = 18;
    private int velocidad;
    private boolean activa = true;
    private boolean pausada = false;
    private boolean fantasmaUsada = false;

    private TipoBola tipo;
    private Jugador jugadorIzquierdo;
    private Jugador jugadorDerecho;
    private javax.swing.JPanel panel;

    public Bola(javax.swing.JPanel panel, Jugador jugadorIzquierdo, Jugador jugadorDerecho) {
        this.panel = panel;
        this.jugadorIzquierdo = jugadorIzquierdo;
        this.jugadorDerecho = jugadorDerecho;

        this.x = panel.getWidth() / 2;
        this.y = panel.getHeight() / 2;

        Random random = new Random();
        this.dx = random.nextBoolean() ? 4 : -4;
        this.dy = random.nextBoolean() ? 4 : -4;

        this.tipo = generarTipoAleatorio();
        this.velocidad = tipo == TipoBola.RAPIDA ? 8 : 14;
    }

    @Override
    public void run() {
        while (activa) {
            if (!pausada) {
                mover();
                panel.repaint();
            }

            try {
                Thread.sleep(velocidad);
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
        if (y <= 0 || y + tamanio >= panel.getHeight()) {
            dy *= -1;
        }
    }

    private void verificarChoquePaletas() {
        Rectangle bola = getRectangulo();
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

        if (bola.intersects(paletaIzquierda)) {
            if (tipo == TipoBola.FANTASMA && !fantasmaUsada) {
                fantasmaUsada = true;
                return;
            }
            dx = Math.abs(dx);
            aplicarEfectoEspecial(jugadorDerecho);
        }

        if (bola.intersects(paletaDerecha)) {
            if (tipo == TipoBola.FANTASMA && !fantasmaUsada) {
                fantasmaUsada = true;
                return;
            }
            dx = -Math.abs(dx);
            aplicarEfectoEspecial(jugadorIzquierdo);
        }
    }

    private void verificarSalidaLateral() {
        if (x + tamanio < 0) {
            aplicarPuntos(jugadorDerecho);
            activa = false;
        }

        if (x > panel.getWidth()) {
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
            default:
                jugador.sumarPuntos(1);
                break;
        }
    }

    private void aplicarEfectoEspecial(Jugador rival) {
        if (tipo == TipoBola.RAPIDA) {
            dx *= 2;
            dy *= 2;
        }

        // La congelante se puede mejorar luego tocando la velocidad de Paleta.
    }

    private TipoBola generarTipoAleatorio() {
        TipoBola[] tipos = TipoBola.values();
        return tipos[new Random().nextInt(tipos.length)];
    }

    public void dibujar(Graphics g) {
        g.setColor(obtenerColor());
        g.fillOval(x, y, tamanio, tamanio);
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

    public Rectangle getRectangulo() {
        return new Rectangle(x, y, tamanio, tamanio);
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
}
