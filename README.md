# Extreme Ping Pong 

Proyecto desarrollado para el curso **Programación IV** utilizando **Java Swing**, **Programación Multihilo** y **Programación Orientada a Objetos**.

---

# Descripción

Extreme Ping Pong es un videojuego para dos jugadores donde cada participante controla una paleta para golpear diferentes tipos de bolas.

El juego incorpora múltiples hilos de ejecución, distintos tipos de bolas con comportamientos especiales, un sistema de rondas, temporizador, puntuación y diferentes niveles de dificultad.

---

# Características

-  Juego para dos jugadores.
-  Tres rondas por partida.
-  Temporizador por ronda.
-  Cuatro niveles de dificultad.
-  Diferentes tipos de bolas.
-  Sistema de puntajes.
-  Pausa y reinicio.
-  Resumen final de la partida.
-  Programación multihilo.
-  Interfaz desarrollada con Java Swing.

---

# Tecnologías utilizadas

- Java
- Java Swing
- Programación Orientada a Objetos (POO)
- Programación Multihilo
- AtomicInteger
- CopyOnWriteArrayList

---

# Estructura del proyecto

```
src
│
├── logica
│   ├── Bola.java
│   ├── GestorBolas.java
│   ├── Jugador.java
│   ├── Paleta.java
│   ├── SistemaRondas.java
│   ├── Dificultad.java
│   └── ...
│
├── presentacion
│   ├── VentanaPrincipal.java
│   ├── PanelDibujoJuego.java
│   └── ...
│
├── util
│   ├── Validador.java
│   └── ...
│
└── pingpongextrem
    └── Main.java
```

---

# Cómo ejecutar

## Requisitos

- Java JDK 17 o superior.
- Apache NetBeans IDE.

## Pasos

1. Clonar el repositorio.

```bash
git clone https://github.com/USUARIO/REPOSITORIO.git
```

2. Abrir el proyecto en Apache NetBeans.

3. Ejecutar la clase:

```
ejecutar la clase principal
```

# Controles

## Jugador 1

| Acción | Tecla |
|---------|-------|
| Arriba | W |
| Abajo | S |

## Jugador 2

| Acción | Tecla |
|---------|-------|
| Arriba | ↑ |
| Abajo | ↓ |

---

# Tipos de bolas

| Bola | Función |
|------|----------|
| ⚪ Normal | +1 punto |
| 🔴 Negativa | -2 puntos |
| 🔵 Bonus | +2 puntos |
| 🟡 Rápida | Mayor velocidad |
| 🟣 Fantasma | Puede atravesar una paleta una vez |
| 🔷 Congelante | Reduce temporalmente la velocidad del rival |

---

#  Sistema de juego

- La partida consta de **3 rondas**.
- Cada ronda posee un tiempo límite.
- Al finalizar una ronda se determina un ganador.
- El jugador que gane más rondas obtiene la victoria.
- Al finalizar la partida se muestra un resumen con:
  - Ganador.
  - Rondas ganadas.
  - Puntos totales.

---

# Integrantes

| Integrante | Responsabilidad |
|------------|-----------------|
| Integrante 1 | Interfaz gráfica y controles |
| Integrante 2 | Lógica del juego y paletas |
| Integrante 3 | Multihilos y bolas |
| Integrante 4 | Concurrencia, puntajes y documentación |

---


---

# Licencia

Proyecto desarrollado únicamente con fines académicos para el curso **Programación IV**.

---

# Extreme Ping Pong

Desarrollado en Java con Java Swing y Programación Multihilo.
