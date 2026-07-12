/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package presentacion;

/**
 *
 * @author PC
 */
import java.awt.Color;
import java.awt.Graphics;
import logica.GestorBolas;
import logica.Jugador;

public class PanelDibujoJuego extends javax.swing.JPanel {

    private Jugador jugadorIzquierdo;
    private Jugador jugadorDerecho;
    private GestorBolas gestorBolas;
    private volatile boolean juegoPausado = false;

    public PanelDibujoJuego() {
        setBackground(new Color(0, 153, 153));
        setFocusable(true);

        new javax.swing.Timer(15, e -> repaint()).start();
    }

    public void configurar(Jugador jugadorIzquierdo, Jugador jugadorDerecho, GestorBolas gestorBolas) {
        this.jugadorIzquierdo = jugadorIzquierdo;
        this.jugadorDerecho = jugadorDerecho;
        this.gestorBolas = gestorBolas;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        dibujarMesa(g);
        dibujarLineaCentral(g);
        dibujarPaletas(g);
        dibujarBolas(g);
        dibujarPausa(g);
    }

    private void dibujarMesa(Graphics g) {

        g.setColor(new java.awt.Color(0, 102, 76));
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(java.awt.Color.WHITE);

        // Borde exterior de la mesa
        g.drawRect(
                5,
                5,
                getWidth() - 10,
                getHeight() - 10
        );

        // Línea horizontal central, estilo mesa de ping pong
        g.drawLine(
                5,
                getHeight() / 2,
                getWidth() - 5,
                getHeight() / 2
        );
    }

    public void setJuegoPausado(boolean juegoPausado) {
        this.juegoPausado = juegoPausado;
        repaint();
    }

    private void dibujarLineaCentral(Graphics g) {
        g.setColor(Color.WHITE);

        int centroX = getWidth() / 2;

        for (int y = 0; y < getHeight(); y += 25) {
            g.fillRect(centroX - 2, y, 4, 15);
        }
    }

    private void dibujarPaletas(Graphics g) {
        if (jugadorIzquierdo == null || jugadorDerecho == null) {
            return;
        }

        g.setColor(Color.WHITE);

        g.fillRect(
                jugadorIzquierdo.getPaleta().getX(),
                jugadorIzquierdo.getPaleta().getY(),
                jugadorIzquierdo.getPaleta().getAncho(),
                jugadorIzquierdo.getPaleta().getAlto()
        );

        g.fillRect(
                jugadorDerecho.getPaleta().getX(),
                jugadorDerecho.getPaleta().getY(),
                jugadorDerecho.getPaleta().getAncho(),
                jugadorDerecho.getPaleta().getAlto()
        );
    }

    private void dibujarPausa(Graphics g) {

        if (!juegoPausado) {
            return;
        }

        g.setColor(new java.awt.Color(0, 0, 0, 160));
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(java.awt.Color.WHITE);
        g.setFont(new java.awt.Font(
                "Arial",
                java.awt.Font.BOLD,
                40
        ));

        String titulo = "PAUSA";

        int anchoTitulo
                = g.getFontMetrics().stringWidth(titulo);

        g.drawString(
                titulo,
                (getWidth() - anchoTitulo) / 2,
                getHeight() / 2
        );

        g.setFont(new java.awt.Font(
                "Arial",
                java.awt.Font.PLAIN,
                16
        ));

        String subtitulo
                = "Presione Reanudar para continuar";

        int anchoSubtitulo
                = g.getFontMetrics().stringWidth(subtitulo);

        g.drawString(
                subtitulo,
                (getWidth() - anchoSubtitulo) / 2,
                getHeight() / 2 + 35
        );
    }

    private void dibujarBolas(Graphics g) {
        if (gestorBolas != null) {
            gestorBolas.dibujarBolas(g);
        }
    }
}
