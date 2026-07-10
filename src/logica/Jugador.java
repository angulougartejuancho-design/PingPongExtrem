/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logica;

import java.util.concurrent.atomic.AtomicInteger;
/**
 *
 * @author Anyel
 */
public class Jugador {
    
    private volatile String nombre;

    private final Paleta paleta;

    private final AtomicInteger puntosRonda = new AtomicInteger(0);

    private final AtomicInteger puntosTotales = new AtomicInteger(0);

    private final AtomicInteger rondasGanadas = new AtomicInteger(0);

    public Jugador(String nombre, Paleta paleta) {
        this.nombre = nombre;
        this.paleta = paleta;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre != null && !nombre.trim().isEmpty()) {
            this.nombre = nombre.trim();
        }
    }

    public Paleta getPaleta() {
        return paleta;
    }

    public void sumarPuntos(int valor) {
        puntosRonda.addAndGet(valor);
        puntosTotales.addAndGet(valor);
    }

    public int getPuntos() {
        return puntosRonda.get();
    }

    public int getPuntosTotales() {
        return puntosTotales.get();
    }

    public int getRondasGanadas() {
        return rondasGanadas.get();
    }

    public void sumarRondaGanada() {
        rondasGanadas.incrementAndGet();
    }


    public void reiniciarPuntosRonda() {
        puntosRonda.set(0);
    }

 
    public void reiniciarJugador() {
        puntosRonda.set(0);
        puntosTotales.set(0);
        rondasGanadas.set(0);
        paleta.reiniciarPosicion();
    }

    @Override
    public String toString() {
        return nombre + " [ronda=" + getPuntos()
                + ", total=" + getPuntosTotales()
                + ", rondasGanadas=" + getRondasGanadas() + "]";
    }
}
