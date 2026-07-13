/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logica;

import util.Validador;
/**
 *
 * @author Anyel
 */
public class SistemaRondas {
    
    public static final int TOTAL_RONDAS = 3;
    public static final int RONDAS_PARA_GANAR = 2;

    private final Jugador jugador1;
    private final Jugador jugador2;

    private int rondaActual;
    private int tiempoRestante;
    private boolean partidaFinalizada;

    public SistemaRondas(Jugador jugador1, Jugador jugador2) {
        this.jugador1 = jugador1;
        this.jugador2 = jugador2;
        reiniciarPartida();
    }

    public synchronized void reiniciarPartida() {
        rondaActual = 1;
        partidaFinalizada = false;
        jugador1.reiniciarJugador();
        jugador2.reiniciarJugador();
        tiempoRestante = Validador.TIEMPO_INICIAL_RONDA;
    }

    public synchronized void iniciarRonda() {
        tiempoRestante = Validador.TIEMPO_INICIAL_RONDA;
        jugador1.reiniciarPuntosRonda();
        jugador2.reiniciarPuntosRonda();
    }

    public synchronized int getTiempoRestante() {
        return tiempoRestante;
    }

    public synchronized int getRondaActual() {
        return rondaActual;
    }

    public synchronized boolean isPartidaFinalizada() {
        return partidaFinalizada;
    }

    public synchronized boolean disminuirTiempo() {
        if (partidaFinalizada) {
            return false;
        }

        if (tiempoRestante > 0) {
            tiempoRestante--;
        }

        if (tiempoRestante <= 0) {
            finalizarRonda();
            return true;
        }

        return false;
    }

    private synchronized void finalizarRonda() {
      

    Jugador ganadorRonda = determinarGanadorRonda();

    if (ganadorRonda != null) {
        ganadorRonda.sumarRondaGanada();
    }

    boolean alguienGanoLaPartida
            = jugador1.getRondasGanadas() >= RONDAS_PARA_GANAR
            || jugador2.getRondasGanadas() >= RONDAS_PARA_GANAR;

    if (alguienGanoLaPartida || rondaActual >= TOTAL_RONDAS) {

        partidaFinalizada = true;

    } else {

        rondaActual++;
        iniciarRonda();
    }
    }

    public synchronized Jugador determinarGanadorRonda() {
        if (jugador1.getPuntos() > jugador2.getPuntos()) {
            return jugador1;
        }
        if (jugador2.getPuntos() > jugador1.getPuntos()) {
            return jugador2;
        }
        return null; // empate en la ronda
    }

    public synchronized Jugador determinarGanadorPartida() {
        if (!partidaFinalizada) {
            return null;
        }
        if (jugador1.getRondasGanadas() > jugador2.getRondasGanadas()) {
            return jugador1;
        }
        if (jugador2.getRondasGanadas() > jugador1.getRondasGanadas()) {
            return jugador2;
        }
        return null; // empate en la partida
    }

    public synchronized String obtenerResumenFinal() {
        Jugador ganador = determinarGanadorPartida();

        StringBuilder resumen = new StringBuilder();
        resumen.append("=== Resultado de la partida ===\n\n");
        resumen.append(ganador != null
                ? "Ganador: " + ganador.getNombre() + "\n\n"
                : "Resultado: Empate\n\n");

        resumen.append(jugador1.getNombre())
                .append(" -> Rondas ganadas: ").append(jugador1.getRondasGanadas())
                .append(" | Puntos totales: ").append(jugador1.getPuntosTotales())
                .append("\n");

        resumen.append(jugador2.getNombre())
                .append(" -> Rondas ganadas: ").append(jugador2.getRondasGanadas())
                .append(" | Puntos totales: ").append(jugador2.getPuntosTotales());

        return resumen.toString();
    }
}

