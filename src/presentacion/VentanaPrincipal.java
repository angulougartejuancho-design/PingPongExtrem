/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package presentacion;

/**
 *
 * @author angul
 */
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.SwingUtilities;
import util.Validador;

import logica.Jugador;
import logica.Paleta;
import logica.SistemaRondas;
import logica.GestorBolas;
import logica.Dificultad;

public class VentanaPrincipal extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger
            = java.util.logging.Logger.getLogger(VentanaPrincipal.class.getName());

    private boolean enPausaEjemplo = false;

    private Jugador jugadorIzquierdo;
    private Jugador jugadorDerecho;
    private SistemaRondas sistemaRondas;
    private GestorBolas gestorBolas;
    private Dificultad dificultadSeleccionada;
    private javax.swing.Timer temporizadorRonda;

    public VentanaPrincipal() {
        initComponents();
        cargarDificultades();
        setLocationRelativeTo(null);
        configurarEventosTeclado();
        conectarBotones();
        inicializarJugadoresYPaletas();
    }

    public void establecerNombresJugadores(String Jugador1, String Jugador2) {

        lblJugador1.setText(Jugador1);
        lblNombreDerecho.setText(Jugador2);

        if (jugadorIzquierdo != null) {
            jugadorIzquierdo.setNombre(Jugador1);
        }

        if (jugadorDerecho != null) {
            jugadorDerecho.setNombre(Jugador2);
        }
    }

    private void cargarDificultades() {

        cmbDificultad.removeAllItems();

        cmbDificultad.addItem("Fácil");
        cmbDificultad.addItem("Normal");
        cmbDificultad.addItem("Difícil");
        cmbDificultad.addItem("Extremo");

        cmbDificultad.setSelectedItem("Normal");
    }

    private Dificultad obtenerDificultadSeleccionada() {

        String seleccion = (String) cmbDificultad.getSelectedItem();

        if (seleccion == null) {
            return Dificultad.NORMAL;
        }

        switch (seleccion) {

            case "Fácil":
                return Dificultad.FACIL;

            case "Difícil":
                return Dificultad.DIFICIL;

            case "Extremo":
                return Dificultad.EXTREMO;

            case "Normal":
            default:
                return Dificultad.NORMAL;
        }
    }

    private void inicializarJugadoresYPaletas() {

        Paleta paletaIzquierda = new Paleta(
                Paleta.Posicion.IZQUIERDA, 12, 70, 6, getPanelJuego());

        Paleta paletaDerecha = new Paleta(
                Paleta.Posicion.DERECHA, 12, 70, 6, getPanelJuego());

        jugadorIzquierdo = new Jugador(lblJugador1.getText(), paletaIzquierda);
        jugadorDerecho = new Jugador(lblNombreDerecho.getText(), paletaDerecha);

        sistemaRondas = new SistemaRondas(jugadorIzquierdo, jugadorDerecho);

        gestorBolas = new GestorBolas(getPanelJuego(), jugadorIzquierdo, jugadorDerecho);
        getPanelJuego().configurar(jugadorIzquierdo, jugadorDerecho, gestorBolas);
    }

    private void conectarBotones() {
        btnComenzar.addActionListener(evt -> iniciarJuego());
        btnPausa.addActionListener(evt -> pausarJuego());
        btnReinicio.addActionListener(evt -> reiniciarJuego());
    }

    private void configurarEventosTeclado() {

        setFocusable(true);

        addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {

                switch (e.getKeyCode()) {

                    case KeyEvent.VK_W:
                        jugadorIzquierdo.getPaleta().setMoviendoArriba(true);
                        break;

                    case KeyEvent.VK_S:
                        jugadorIzquierdo.getPaleta().setMoviendoAbajo(true);
                        break;

                    case KeyEvent.VK_UP:
                        jugadorDerecho.getPaleta().setMoviendoArriba(true);
                        break;

                    case KeyEvent.VK_DOWN:
                        jugadorDerecho.getPaleta().setMoviendoAbajo(true);
                        break;

                    default:
                        break;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

                switch (e.getKeyCode()) {

                    case KeyEvent.VK_W:
                        jugadorIzquierdo.getPaleta().setMoviendoArriba(false);
                        break;

                    case KeyEvent.VK_S:
                        jugadorIzquierdo.getPaleta().setMoviendoAbajo(false);
                        break;

                    case KeyEvent.VK_UP:
                        jugadorDerecho.getPaleta().setMoviendoArriba(false);
                        break;

                    case KeyEvent.VK_DOWN:
                        jugadorDerecho.getPaleta().setMoviendoAbajo(false);
                        break;

                    default:
                        break;
                }
            }
        });
    }

    private void iniciarJuego() {

        System.out.println("[Juego] Botón comenzar presionado.");

        if (!gestorBolas.isGenerando()) {

            dificultadSeleccionada
                    = obtenerDificultadSeleccionada();

            gestorBolas.setDificultad(
                    dificultadSeleccionada
            );

            cmbDificultad.setEnabled(false);
        }

        enPausaEjemplo = false;

        requestFocusInWindow();

        jugadorIzquierdo.getPaleta().setPausado(false);
        jugadorDerecho.getPaleta().setPausado(false);

        jugadorIzquierdo.getPaleta().iniciar();
        jugadorDerecho.getPaleta().iniciar();

        gestorBolas.setPausado(false);
        gestorBolas.iniciar();

        iniciarTemporizadorRonda();

        System.out.println(
                "[Juego] Dificultad seleccionada: "
                + dificultadSeleccionada
        );
    }

    private Dificultad seleccionarDificultad() {

        Dificultad[] opciones = {
            Dificultad.FACIL,
            Dificultad.NORMAL,
            Dificultad.DIFICIL,
            Dificultad.EXTREMO
        };

        Object seleccion = javax.swing.JOptionPane.showInputDialog(
                this,
                "Seleccione la dificultad:",
                "Dificultad",
                javax.swing.JOptionPane.QUESTION_MESSAGE,
                null,
                opciones,
                Dificultad.NORMAL
        );

        if (seleccion instanceof Dificultad) {
            return (Dificultad) seleccion;
        }

        return null;
    }

    private void pausarJuego() {

        enPausaEjemplo = !enPausaEjemplo;

        String estado = enPausaEjemplo ? "PAUSADO" : "REANUDADO";

        System.out.println("[Prueba] Estado: " + estado);

        jugadorIzquierdo.getPaleta().setPausado(enPausaEjemplo);
        jugadorDerecho.getPaleta().setPausado(enPausaEjemplo);

        gestorBolas.setPausado(enPausaEjemplo);

        if (temporizadorRonda != null) {
            if (enPausaEjemplo) {
                temporizadorRonda.stop();
            } else {
                temporizadorRonda.start();
            }
        }
    }

    private void reiniciarJuego() {

        cmbDificultad.setEnabled(true);
        cmbDificultad.setSelectedItem("Normal");

        System.out.println("[Juego] Reiniciando partida.");

        if (temporizadorRonda != null) {
            temporizadorRonda.stop();
        }

        gestorBolas.detenerTodo();

        enPausaEjemplo = false;
        dificultadSeleccionada = null;

        jugadorIzquierdo.getPaleta().setPausado(false);
        jugadorDerecho.getPaleta().setPausado(false);

        jugadorIzquierdo.getPaleta().setMoviendoArriba(false);
        jugadorIzquierdo.getPaleta().setMoviendoAbajo(false);

        jugadorDerecho.getPaleta().setMoviendoArriba(false);
        jugadorDerecho.getPaleta().setMoviendoAbajo(false);

        sistemaRondas.reiniciarPartida();

        lblPuntos1.setText("0");
        lblPuntos2.setText("0");

        lblTemporizador.setText(
                String.valueOf(
                        sistemaRondas.getTiempoRestante()
                )
        );

        getPanelJuego().repaint();
        requestFocusInWindow();
    }

    private void iniciarTemporizadorRonda() {

        if (temporizadorRonda != null && temporizadorRonda.isRunning()) {
            return;
        }

        actualizarTemporizador(sistemaRondas.getTiempoRestante());

        temporizadorRonda = new javax.swing.Timer(1000, evt -> {

            boolean rondaTerminada = sistemaRondas.disminuirTiempo();

            actualizarTemporizador(sistemaRondas.getTiempoRestante());
            actualizarPuntosIzquierdo(jugadorIzquierdo.getPuntos());
            actualizarPuntosDerecho(jugadorDerecho.getPuntos());

            if (rondaTerminada) {

                // Se eliminan todas las bolas de la ronda anterior.
                gestorBolas.detenerTodo();

                if (sistemaRondas.isPartidaFinalizada()) {

                    temporizadorRonda.stop();
                    mostrarResultadoFinal();

                } else {

                    System.out.println("[Juego] Comienza la ronda "
                            + sistemaRondas.getRondaActual());

                    // Mantiene la misma dificultad seleccionada.
                    gestorBolas.setDificultad(dificultadSeleccionada);

                    // Comienza la siguiente ronda con bolas nuevas.
                    gestorBolas.iniciar();
                }
            }
        });

        temporizadorRonda.start();
    }

    private void mostrarResultadoFinal() {

        gestorBolas.detenerTodo();

        jugadorIzquierdo.getPaleta().setPausado(true);
        jugadorDerecho.getPaleta().setPausado(true);

        javax.swing.JOptionPane.showMessageDialog(
                this,
                sistemaRondas.obtenerResumenFinal(),
                "Resultado de la partida",
                javax.swing.JOptionPane.INFORMATION_MESSAGE
        );
    }

    public PanelDibujoJuego getPanelJuego() {
        return (PanelDibujoJuego) PanelJuego;
    }

    public void actualizarPuntosIzquierdo(int puntos) {
        SwingUtilities.invokeLater(()
                -> lblPuntos1.setText(String.valueOf(puntos)));
    }

    public void actualizarPuntosDerecho(int puntos) {
        SwingUtilities.invokeLater(()
                -> lblPuntos2.setText(String.valueOf(puntos)));
    }

    public void actualizarTemporizador(int segundos) {
        SwingUtilities.invokeLater(()
                -> lblTemporizador.setText(String.valueOf(segundos)));
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        PanelJuego = new presentacion.PanelDibujoJuego();
        lblJugador1 = new javax.swing.JLabel();
        lblPuntos1 = new javax.swing.JLabel();
        lblTemporizador = new javax.swing.JLabel();
        lblNombreDerecho = new javax.swing.JLabel();
        lblPuntos2 = new javax.swing.JLabel();
        btnComenzar = new javax.swing.JButton();
        btnPausa = new javax.swing.JButton();
        btnReinicio = new javax.swing.JButton();
        cmbDificultad = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        PanelJuego.setBackground(new java.awt.Color(0, 153, 153));

        javax.swing.GroupLayout PanelJuegoLayout = new javax.swing.GroupLayout(PanelJuego);
        PanelJuego.setLayout(PanelJuegoLayout);
        PanelJuegoLayout.setHorizontalGroup(
            PanelJuegoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        PanelJuegoLayout.setVerticalGroup(
            PanelJuegoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 313, Short.MAX_VALUE)
        );

        lblJugador1.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        lblJugador1.setText("Jugador1");

        lblPuntos1.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        lblPuntos1.setText("0");

        lblTemporizador.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        lblTemporizador.setText("55");

        lblNombreDerecho.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        lblNombreDerecho.setText("Jugador2");

        lblPuntos2.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        lblPuntos2.setText("0");

        btnComenzar.setText("Comenzar");

        btnPausa.setText("Pausa");

        btnReinicio.setText("Reiniciar");

        cmbDificultad.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel1.setText("Dificultad:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(101, 101, 101)
                        .addComponent(lblPuntos1, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(lblJugador1)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblTemporizador)
                .addGap(179, 179, 179)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(lblPuntos2, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(56, 56, 56))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(lblNombreDerecho)
                        .addGap(33, 33, 33))))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(PanelJuego, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(btnComenzar)
                                .addGap(161, 161, 161))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(18, 18, 18)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnPausa, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 121, Short.MAX_VALUE)
                                .addComponent(btnReinicio, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(49, 49, 49))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(cmbDificultad, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblNombreDerecho)
                            .addComponent(lblJugador1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblPuntos2)
                            .addComponent(lblPuntos1)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(lblTemporizador)))
                .addGap(58, 58, 58)
                .addComponent(PanelJuego, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnComenzar)
                    .addComponent(btnPausa)
                    .addComponent(btnReinicio))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbDificultad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addContainerGap(75, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new VentanaPrincipal().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel PanelJuego;
    private javax.swing.JButton btnComenzar;
    private javax.swing.JButton btnPausa;
    private javax.swing.JButton btnReinicio;
    private javax.swing.JComboBox<String> cmbDificultad;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel lblJugador1;
    private javax.swing.JLabel lblNombreDerecho;
    private javax.swing.JLabel lblPuntos1;
    private javax.swing.JLabel lblPuntos2;
    private javax.swing.JLabel lblTemporizador;
    // End of variables declaration//GEN-END:variables
}
