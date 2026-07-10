/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logica;

import java.awt.Graphics;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.JPanel;

public class GestorBolas {

    /*
     * Colección concurrente segura para almacenar
     * las bolas ejecutadas por diferentes hilos.
     */
    private final CopyOnWriteArrayList<Bola> bolas
            = new CopyOnWriteArrayList<>();

    private final JPanel panel;
    private final Jugador jugadorIzquierdo;
    private final Jugador jugadorDerecho;

    private volatile Dificultad dificultad
            = Dificultad.NORMAL;

    private volatile boolean generando = false;
    private volatile boolean pausado = false;

    private Thread hiloGenerador;

    private int contadorBolas = 0;

    public GestorBolas(
            JPanel panel,
            Jugador jugadorIzquierdo,
            Jugador jugadorDerecho) {

        this.panel = panel;
        this.jugadorIzquierdo = jugadorIzquierdo;
        this.jugadorDerecho = jugadorDerecho;
    }

    public void setDificultad(Dificultad dificultad) {

        if (dificultad != null) {
            this.dificultad = dificultad;
        }
    }

    public Dificultad getDificultad() {
        return dificultad;
    }

    public synchronized void crearBola() {

        limpiarBolasInactivas();

        if (!generando) {
            return;
        }

        if (bolas.size()
                >= dificultad.getMaximoBolas()) {

            return;
        }

        Bola bola = new Bola(
                panel,
                jugadorIzquierdo,
                jugadorDerecho,
                dificultad
        );

        bolas.add(bola);

        contadorBolas++;

        Thread hiloBola = new Thread(
                bola,
                "Hilo-Bola-" + contadorBolas
        );

        hiloBola.setDaemon(true);
        hiloBola.start();

        System.out.println(
                "[GestorBolas] Bola creada: "
                + bola.getTipo()
                + " | Dificultad: "
                + dificultad
                + " | Activas: "
                + bolas.size()
        );
    }

    public synchronized void iniciar() {

        if (generando) {
            return;
        }

        generando = true;
        pausado = false;

        /*
         * Primera bola inmediatamente.
         */
        crearBola();

        hiloGenerador = new Thread(() -> {

            while (generando) {

                try {

                    Thread.sleep(
                            dificultad.getFrecuenciaAparicion()
                    );

                    if (generando && !pausado) {
                        crearBola();
                    }

                } catch (InterruptedException e) {

                    Thread.currentThread().interrupt();
                    break;
                }
            }

        }, "Hilo-Generador-Bolas");

        hiloGenerador.setDaemon(true);
        hiloGenerador.start();
    }

    public void setPausado(boolean pausado) {

        this.pausado = pausado;

        for (Bola bola : bolas) {
            bola.setPausada(pausado);
        }
    }

    public synchronized void detenerTodo() {

        generando = false;
        pausado = false;

        if (hiloGenerador != null) {

            hiloGenerador.interrupt();
            hiloGenerador = null;
        }

        for (Bola bola : bolas) {
            bola.detener();
        }

        bolas.clear();

        panel.repaint();
    }

    public void limpiarBolasInactivas() {

        bolas.removeIf(
                bola -> !bola.isActiva()
        );
    }

    public void dibujarBolas(Graphics g) {

        limpiarBolasInactivas();

        for (Bola bola : bolas) {
            bola.dibujar(g);
        }
    }

    public int getCantidadBolasActivas() {

        limpiarBolasInactivas();
        return bolas.size();
    }

    public boolean isGenerando() {
        return generando;
    }

    public boolean isPausado() {
        return pausado;
    }
}