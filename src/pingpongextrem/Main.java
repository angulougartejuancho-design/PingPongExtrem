/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pingpongextrem;

/**
 *
 * @author angul
 */
import presentacion.VentanaPrincipal;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;


public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            String nombreIzquierdo = solicitarNombreJugador(
                    "Nombre del jugador 1 (W/S):", "JUGADOR-1");

            String nombreDerecho = solicitarNombreJugador(
                    "Nombre del jugador 2 (\u2191 / \u2193):", "JUGADOR-2");

            VentanaPrincipal ventana = new VentanaPrincipal();
            ventana.establecerNombresJugadores(nombreIzquierdo, nombreDerecho);
            ventana.setVisible(true);
        });
    }

    private static String solicitarNombreJugador(String mensaje, String nombrePorDefecto) {
        String nombre = JOptionPane.showInputDialog(
                null,
                mensaje,
                "Ping Pong  Extreme V2.2",
                JOptionPane.QUESTION_MESSAGE);

        if (nombre == null || nombre.trim().isEmpty()) {
            nombre = nombrePorDefecto;
        }

        return nombre.trim();
    }
}
