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

        dibujarLineaCentral(g);
        dibujarPaletas(g);
        dibujarBolas(g);
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

    private void dibujarBolas(Graphics g) {
        if (gestorBolas != null) {
            gestorBolas.dibujarBolas(g);
        }
    }
}
    
