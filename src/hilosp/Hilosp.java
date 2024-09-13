package hilosp;

import java.util.concurrent.ThreadLocalRandom;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

public class Hilosp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean continuarPrograma = true;

        while (continuarPrograma) {
            ejecutarPrograma(scanner);

            System.out.print("\n¿Deseas ejecutar el programa de nuevo? (s/n): ");
            String respuesta = scanner.nextLine().trim().toLowerCase();
            continuarPrograma = respuesta.equals("s");
        }

        System.out.println("Programa finalizado. ¡Hasta luego!");
        scanner.close();
    }

    private static void ejecutarPrograma(Scanner scanner) {
        int intentos = 0;
        int tamañoTotal = 0;

        while (intentos < 3) {
            System.out.print("Ingresa el tamaño total para ambos arreglos (entre 1 y 20): ");
            String entrada = scanner.nextLine();

            try {
                tamañoTotal = Integer.parseInt(entrada);
                if (tamañoTotal < 1 || tamañoTotal > 20) {
                    System.out.println("Error: El tamaño total debe ser un número entre 1 y 20.");
                    intentos++;
                } else {
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Entrada inválida. Solo se permiten números.");
                intentos++;
            }

            if (intentos == 3) {
                System.out.println("Has excedido el número de intentos. El programa se cerrará.");
                return;
            }
        }

        int tamañoArreglo1 = tamañoTotal / 2;
        int tamañoArreglo2 = tamañoTotal - tamañoArreglo1; // Si es impar, el segundo arreglo tendrá un elemento más

        int[] arreglo1 = new int[tamañoArreglo1];
        int[] arreglo2 = new int[tamañoArreglo2];

        AtomicInteger contadorArreglo1 = new AtomicInteger(0);
        AtomicInteger contadorArreglo2 = new AtomicInteger(0);

        // Crear y lanzar los hilos
        HiloGenerador hilo1 = new HiloGenerador(1, 1, 120, arreglo1, contadorArreglo1, tamañoArreglo1);
        HiloGenerador hilo2 = new HiloGenerador(2, 150, 250, arreglo1, contadorArreglo1, tamañoArreglo1);
        HiloGenerador hilo3 = new HiloGenerador(3, 300, 320, arreglo2, contadorArreglo2, tamañoArreglo2);
        HiloGenerador hilo4 = new HiloGenerador(4, 450, 600, arreglo2, contadorArreglo2, tamañoArreglo2);

        hilo1.start();
        hilo2.start();
        hilo3.start();
        hilo4.start();

        try {
            hilo1.join();
            hilo2.join();
            hilo3.join();
            hilo4.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Mostrar el contenido de los arreglos
        System.out.println("\nArreglo 1 (generado por hilos 1 y 2):");
        for (int valor : arreglo1) {
            System.out.print(valor + ", ");
        }

        System.out.println("\nArreglo 2 (generado por hilos 3 y 4):");
        for (int valor : arreglo2) {
            System.out.print(valor + ", ");
        }
    }
}

class HiloGenerador extends Thread {
    private final int id;
    private final int min;
    private final int max;
    private int[] arreglo;
    private AtomicInteger contador;
    private int tamañoObjetivo;

    public HiloGenerador(int id, int min, int max, int[] arreglo, AtomicInteger contador, int tamañoObjetivo) {
        this.id = id;
        this.min = min;
        this.max = max;
        this.arreglo = arreglo;
        this.contador = contador;
        this.tamañoObjetivo = tamañoObjetivo;
    }

    @Override
    public void run() {
        while (true) {
            int index = contador.getAndIncrement();
            if (index >= tamañoObjetivo) {
                break; // Ya se generaron todos los números necesarios para este arreglo
            }

            int valor = ThreadLocalRandom.current().nextInt(min, max + 1);
            System.out.println("Hilo " + id + " genera valor: " + valor);
            
            synchronized (arreglo) {
                arreglo[index] = valor;
            }
        }
    }
}