/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logica;


import java.awt.Graphics;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.JPanel;
/**
 *
 * @author PC
 */
public class GestorBolas {

    private CopyOnWriteArrayList<Bola> bolas = new CopyOnWriteArrayList<>();

    private JPanel panel;
    private Jugador jugadorIzquierdo;
    private Jugador jugadorDerecho;

    public GestorBolas(JPanel panel, Jugador jugadorIzquierdo, Jugador jugadorDerecho) {
        this.panel = panel;
        this.jugadorIzquierdo = jugadorIzquierdo;
        this.jugadorDerecho = jugadorDerecho;
    }

    public void crearBola() {
        Bola bola = new Bola(panel, jugadorIzquierdo, jugadorDerecho);
        bolas.add(bola);

        Thread hilo = new Thread(bola, "Hilo-Bola");
        hilo.setDaemon(true);
        hilo.start();
    }

    public void iniciar() {
        crearBola();
    }

    public void setPausado(boolean pausado) {
        for (Bola bola : bolas) {
            bola.setPausada(pausado);
        }
    }

    public void detenerTodo() {
        for (Bola bola : bolas) {
            bola.detener();
        }
        bolas.clear();
        panel.repaint();
    }

    public void limpiarBolasInactivas() {
        for (Bola bola : bolas) {
            if (!bola.isActiva()) {
                bolas.remove(bola);
            }
        }
    }

    public void dibujarBolas(Graphics g) {
        limpiarBolasInactivas();

        for (Bola bola : bolas) {
            bola.dibujar(g);
        }
    }
}
