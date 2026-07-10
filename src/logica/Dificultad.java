/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package logica;

public enum Dificultad {

    FACIL(
            18, // Retardo de movimiento
            5000, // Frecuencia de aparición
            1, // Máximo de bolas
            20 // Probabilidad especial
    ),
    NORMAL(
            14,
            4000,
            2,
            40
    ),
    DIFICIL(
            10,
            3000,
            4,
            60
    ),
    EXTREMO(
            7,
            1500,
            6,
            80
    );

    private final int retardoMovimiento;
    private final int frecuenciaAparicion;
    private final int maximoBolas;
    private final int probabilidadEspecial;

    Dificultad(
            int retardoMovimiento,
            int frecuenciaAparicion,
            int maximoBolas,
            int probabilidadEspecial) {

        this.retardoMovimiento = retardoMovimiento;
        this.frecuenciaAparicion = frecuenciaAparicion;
        this.maximoBolas = maximoBolas;
        this.probabilidadEspecial = probabilidadEspecial;
    }

    public int getRetardoMovimiento() {
        return retardoMovimiento;
    }

    public int getFrecuenciaAparicion() {
        return frecuenciaAparicion;
    }

    public int getMaximoBolas() {
        return maximoBolas;
    }

    public int getProbabilidadEspecial() {
        return probabilidadEspecial;
    }

    @Override
    public String toString() {

        switch (this) {

            case FACIL:
                return "Fácil";

            case NORMAL:
                return "Normal";

            case DIFICIL:
                return "Difícil";

            case EXTREMO:
                return "Extremo";

            default:
                return name();
        }
    }
}
