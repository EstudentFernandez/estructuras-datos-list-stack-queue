package estructuras.benchmark;

import estructuras.queue.ColaArreglo;
import estructuras.queue.ColaEnlazada;
import estructuras.queue.MyQueue;
import estructuras.stack.MyStack;
import estructuras.stack.PilaArreglo;
import estructuras.stack.PilaEnlazada;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Locale;
import java.util.Random;

/**
 * Benchmark reproducible para comparar:
 *
 * - PilaEnlazada vs PilaArreglo
 * - ColaEnlazada vs ColaArreglo circular
 *
 * La preparación de las estructuras se realiza
 * fuera del intervalo cronometrado.
 */
public final class BenchmarkPilasColas {

    private static final long SEMILLA_BASE = 20260922L;

    private static final int CALENTAMIENTOS = 5;
    private static final int REPETICIONES = 10;

    private static final int[] TAMANOS_COMPLETOS = {
            10,
            100,
            1_000,
            10_000,
            100_000
    };

    private static final int[] TAMANOS_RAPIDOS = {
            10,
            100,
            1_000
    };

    private static final int SENTINELA =
            Integer.MIN_VALUE;

    private static volatile long blackhole = 0L;

    private BenchmarkPilasColas() {
    }

    public static void main(String[] args)
            throws IOException {

        boolean modoRapido =
                args.length > 0
                        && args[0].equalsIgnoreCase("--rapido");

        int[] tamanos =
                modoRapido
                        ? TAMANOS_RAPIDOS
                        : TAMANOS_COMPLETOS;

        Files.createDirectories(
                Path.of("resultados")
        );

        escribirEntorno(modoRapido);

        Path salida =
                Path.of(
                        "resultados",
                        "pilas_colas_raw.csv"
                );

        try (PrintWriter csv =
                     new PrintWriter(
                             Files.newBufferedWriter(salida))) {

            escribirEncabezado(csv);

            for (int indice = 0;
                 indice < tamanos.length;
                 indice++) {

                int n =
                        tamanos[indice];

                long semilla =
                        SEMILLA_BASE + n;

                int[] datos =
                        generarDatos(
                                n,
                                semilla
                        );

                System.out.println();
                System.out.println(
                        "n = " + n
                );

                /*
                 * Alternamos el orden entre tamaños para
                 * reducir sesgo sistemático de ejecución.
                 */
                if (indice % 2 == 0) {

                    System.out.println(
                            "  PilaEnlazada"
                    );

                    benchmarkPila(
                            csv,
                            "PilaEnlazada",
                            datos,
                            semilla
                    );

                    System.out.println(
                            "  PilaArreglo"
                    );

                    benchmarkPila(
                            csv,
                            "PilaArreglo",
                            datos,
                            semilla
                    );

                } else {

                    System.out.println(
                            "  PilaArreglo"
                    );

                    benchmarkPila(
                            csv,
                            "PilaArreglo",
                            datos,
                            semilla
                    );

                    System.out.println(
                            "  PilaEnlazada"
                    );

                    benchmarkPila(
                            csv,
                            "PilaEnlazada",
                            datos,
                            semilla
                    );
                }

                /*
                 * Para las colas invertimos el orden
                 * respecto de las pilas.
                 */
                if (indice % 2 == 0) {

                    System.out.println(
                            "  ColaArreglo"
                    );

                    benchmarkCola(
                            csv,
                            "ColaArreglo",
                            datos,
                            semilla
                    );

                    System.out.println(
                            "  ColaEnlazada"
                    );

                    benchmarkCola(
                            csv,
                            "ColaEnlazada",
                            datos,
                            semilla
                    );

                } else {

                    System.out.println(
                            "  ColaEnlazada"
                    );

                    benchmarkCola(
                            csv,
                            "ColaEnlazada",
                            datos,
                            semilla
                    );

                    System.out.println(
                            "  ColaArreglo"
                    );

                    benchmarkCola(
                            csv,
                            "ColaArreglo",
                            datos,
                            semilla
                    );
                }
            }
        }

        System.out.println();
        System.out.println(
                "Benchmark de pilas y colas finalizado."
        );

        System.out.println(
                "Datos guardados en "
                        + "resultados/pilas_colas_raw.csv"
        );

        System.out.println(
                "Blackhole = " + blackhole
        );
    }

    /* =========================================================
       PILAS
       ========================================================= */

    private static void benchmarkPila(
            PrintWriter csv,
            String implementacion,
            int[] datos,
            long semilla) {

        registrar(
                csv,
                "pilas",
                implementacion,
                "push",
                "sin_redimensionamiento",
                semilla,
                () -> medirPushPila(
                        implementacion,
                        datos
                )
        );

        /*
         * El escenario de crecimiento forzado
         * solamente tiene sentido para el arreglo.
         */
        if (implementacion.equals("PilaArreglo")) {

            registrar(
                    csv,
                    "pilas",
                    implementacion,
                    "push",
                    "crecimiento_forzado",
                    semilla,
                    () -> medirPushForzadoPila(
                            datos
                    )
            );
        }

        registrar(
                csv,
                "pilas",
                implementacion,
                "push",
                "secuencia_amortizada",
                semilla,
                () -> medirSecuenciaPushPila(
                        implementacion,
                        datos
                )
        );

        registrar(
                csv,
                "pilas",
                implementacion,
                "pop",
                "general",
                semilla,
                () -> medirPopPila(
                        implementacion,
                        datos
                )
        );

        registrar(
                csv,
                "pilas",
                implementacion,
                "peek",
                "general",
                semilla,
                () -> medirPeekPila(
                        implementacion,
                        datos
                )
        );

        registrar(
                csv,
                "pilas",
                implementacion,
                "size",
                "no_vacia",
                semilla,
                () -> medirSizePila(
                        implementacion,
                        datos
                )
        );

        registrar(
                csv,
                "pilas",
                implementacion,
                "isEmpty",
                "no_vacia",
                semilla,
                () -> medirIsEmptyPila(
                        implementacion,
                        datos
                )
        );

        registrarDeletePila(
                csv,
                implementacion,
                datos,
                semilla,
                "cima",
                datos[datos.length - 1]
        );

        registrarDeletePila(
                csv,
                implementacion,
                datos,
                semilla,
                "medio",
                datos[datos.length / 2]
        );

        registrarDeletePila(
                csv,
                implementacion,
                datos,
                semilla,
                "base",
                datos[0]
        );

        registrarDeletePila(
                csv,
                implementacion,
                datos,
                semilla,
                "ausente",
                -1
        );
    }

    private static void registrarDeletePila(
            PrintWriter csv,
            String implementacion,
            int[] datos,
            long semilla,
            String escenario,
            int objetivo) {

        registrar(
                csv,
                "pilas",
                implementacion,
                "delete",
                escenario,
                semilla,
                () -> medirDeletePila(
                        implementacion,
                        datos,
                        objetivo
                )
        );
    }

    private static Resultado medirPushPila(
            String implementacion,
            int[] datos) {

        int n = datos.length;
        int k = kMutaciones(n);

        int capacidad =
                implementacion.equals("PilaArreglo")
                        ? n + 1
                        : -1;

        MyStack<Integer>[] pilas =
                crearPilas(
                        implementacion,
                        k,
                        datos,
                        capacidad
                );

        int capacidadAntes =
                capacidadPila(pilas[0]);

        long inicio =
                System.nanoTime();

        for (MyStack<Integer> pila : pilas) {
            pila.push(SENTINELA);
        }

        long fin =
                System.nanoTime();

        long checksum = 0L;

        for (MyStack<Integer> pila : pilas) {
            checksum += pila.peek();
        }

        int capacidadDespues =
                capacidadPila(pilas[0]);

        return new Resultado(
                fin - inicio,
                k,
                n,
                n + 1,
                capacidadAntes,
                capacidadDespues,
                crecimiento(
                        capacidadAntes,
                        capacidadDespues
                ),
                checksum
        );
    }

    private static Resultado medirPushForzadoPila(
            int[] datos) {

        int n = datos.length;
        int k = kMutaciones(n);

        MyStack<Integer>[] pilas =
                crearPilas(
                        "PilaArreglo",
                        k,
                        datos,
                        n
                );

        int capacidadAntes =
                capacidadPila(pilas[0]);

        long inicio =
                System.nanoTime();

        for (MyStack<Integer> pila : pilas) {
            pila.push(SENTINELA);
        }

        long fin =
                System.nanoTime();

        long checksum = 0L;

        for (MyStack<Integer> pila : pilas) {
            checksum += pila.peek();
        }

        int capacidadDespues =
                capacidadPila(pilas[0]);

        return new Resultado(
                fin - inicio,
                k,
                n,
                n + 1,
                capacidadAntes,
                capacidadDespues,
                "true",
                checksum
        );
    }

    private static Resultado medirSecuenciaPushPila(
            String implementacion,
            int[] datos) {

        MyStack<Integer> pila =
                nuevaPilaVacia(
                        implementacion
                );

        int capacidadAntes =
                capacidadPila(pila);

        long inicio =
                System.nanoTime();

        for (int valor : datos) {
            pila.push(valor);
        }

        long fin =
                System.nanoTime();

        int capacidadDespues =
                capacidadPila(pila);

        long checksum =
                pila.size();

        if (!pila.isEmpty()) {
            checksum += pila.peek();
        }

        String crecimiento;

        if (capacidadAntes < 0) {
            crecimiento = "NA";
        } else if (
                capacidadDespues > capacidadAntes) {
            crecimiento = "multiple";
        } else {
            crecimiento = "false";
        }

        return new Resultado(
                fin - inicio,
                datos.length,
                0,
                datos.length,
                capacidadAntes,
                capacidadDespues,
                crecimiento,
                checksum
        );
    }

    private static Resultado medirPopPila(
            String implementacion,
            int[] datos) {

        int n = datos.length;
        int k = kMutaciones(n);

        int capacidad =
                implementacion.equals("PilaArreglo")
                        ? n
                        : -1;

        MyStack<Integer>[] pilas =
                crearPilas(
                        implementacion,
                        k,
                        datos,
                        capacidad
                );

        int capacidadAntes =
                capacidadPila(pilas[0]);

        long checksum = 0L;

        long inicio =
                System.nanoTime();

        for (MyStack<Integer> pila : pilas) {
            checksum += pila.pop();
        }

        long fin =
                System.nanoTime();

        return new Resultado(
                fin - inicio,
                k,
                n,
                n - 1,
                capacidadAntes,
                capacidadPila(pilas[0]),
                "false",
                checksum
        );
    }

    private static Resultado medirPeekPila(
            String implementacion,
            int[] datos) {

        MyStack<Integer> pila =
                crearPila(
                        implementacion,
                        datos,
                        capacidadNormalPila(
                                implementacion,
                                datos.length
                        )
                );

        int k = kConsultas();

        long checksum = 0L;

        long inicio =
                System.nanoTime();

        for (int i = 0; i < k; i++) {
            checksum += pila.peek();
        }

        long fin =
                System.nanoTime();

        int capacidad =
                capacidadPila(pila);

        return new Resultado(
                fin - inicio,
                k,
                datos.length,
                datos.length,
                capacidad,
                capacidad,
                "false",
                checksum
        );
    }

    private static Resultado medirSizePila(
            String implementacion,
            int[] datos) {

        MyStack<Integer> pila =
                crearPila(
                        implementacion,
                        datos,
                        capacidadNormalPila(
                                implementacion,
                                datos.length
                        )
                );

        int k = kConsultas();

        long checksum = 0L;

        long inicio =
                System.nanoTime();

        for (int i = 0; i < k; i++) {
            checksum += pila.size();
        }

        long fin =
                System.nanoTime();

        int capacidad =
                capacidadPila(pila);

        return new Resultado(
                fin - inicio,
                k,
                datos.length,
                datos.length,
                capacidad,
                capacidad,
                "false",
                checksum
        );
    }

    private static Resultado medirIsEmptyPila(
            String implementacion,
            int[] datos) {

        MyStack<Integer> pila =
                crearPila(
                        implementacion,
                        datos,
                        capacidadNormalPila(
                                implementacion,
                                datos.length
                        )
                );

        int k = kConsultas();

        long checksum = 0L;

        long inicio =
                System.nanoTime();

        for (int i = 0; i < k; i++) {

            if (pila.isEmpty()) {
                checksum++;
            }
        }

        long fin =
                System.nanoTime();

        int capacidad =
                capacidadPila(pila);

        return new Resultado(
                fin - inicio,
                k,
                datos.length,
                datos.length,
                capacidad,
                capacidad,
                "false",
                checksum
        );
    }

    private static Resultado medirDeletePila(
            String implementacion,
            int[] datos,
            int objetivo) {

        int n = datos.length;
        int k = kMutaciones(n);

        int capacidad =
                implementacion.equals("PilaArreglo")
                        ? n
                        : -1;

        MyStack<Integer>[] pilas =
                crearPilas(
                        implementacion,
                        k,
                        datos,
                        capacidad
                );

        int capacidadAntes =
                capacidadPila(pilas[0]);

        long checksum = 0L;

        long inicio =
                System.nanoTime();

        for (MyStack<Integer> pila : pilas) {

            if (pila.delete(objetivo)) {
                checksum++;
            }
        }

        long fin =
                System.nanoTime();

        boolean encontrado =
                objetivo != -1;

        return new Resultado(
                fin - inicio,
                k,
                n,
                encontrado ? n - 1 : n,
                capacidadAntes,
                capacidadPila(pilas[0]),
                "false",
                checksum
        );
    }

    /* =========================================================
       COLAS
       ========================================================= */

    private static void benchmarkCola(
            PrintWriter csv,
            String implementacion,
            int[] datos,
            long semilla) {

        registrar(
                csv,
                "colas",
                implementacion,
                "enqueue",
                "sin_redimensionamiento",
                semilla,
                () -> medirEnqueue(
                        implementacion,
                        datos
                )
        );

        if (implementacion.equals("ColaArreglo")) {

            registrar(
                    csv,
                    "colas",
                    implementacion,
                    "enqueue",
                    "crecimiento_forzado",
                    semilla,
                    () -> medirEnqueueForzado(
                            datos
                    )
            );
        }

        registrar(
                csv,
                "colas",
                implementacion,
                "enqueue",
                "secuencia_amortizada",
                semilla,
                () -> medirSecuenciaEnqueue(
                        implementacion,
                        datos
                )
        );

        registrar(
                csv,
                "colas",
                implementacion,
                "dequeue",
                "general",
                semilla,
                () -> medirDequeue(
                        implementacion,
                        datos
                )
        );

        registrar(
                csv,
                "colas",
                implementacion,
                "front",
                "general",
                semilla,
                () -> medirFront(
                        implementacion,
                        datos
                )
        );

        registrar(
                csv,
                "colas",
                implementacion,
                "size",
                "no_vacia",
                semilla,
                () -> medirSizeCola(
                        implementacion,
                        datos
                )
        );

        registrar(
                csv,
                "colas",
                implementacion,
                "isEmpty",
                "no_vacia",
                semilla,
                () -> medirIsEmptyCola(
                        implementacion,
                        datos
                )
        );

        registrarDeleteCola(
                csv,
                implementacion,
                datos,
                semilla,
                "frente",
                datos[0]
        );

        registrarDeleteCola(
                csv,
                implementacion,
                datos,
                semilla,
                "medio",
                datos[datos.length / 2]
        );

        registrarDeleteCola(
                csv,
                implementacion,
                datos,
                semilla,
                "final",
                datos[datos.length - 1]
        );

        registrarDeleteCola(
                csv,
                implementacion,
                datos,
                semilla,
                "ausente",
                -1
        );
    }

    private static void registrarDeleteCola(
            PrintWriter csv,
            String implementacion,
            int[] datos,
            long semilla,
            String escenario,
            int objetivo) {

        registrar(
                csv,
                "colas",
                implementacion,
                "delete",
                escenario,
                semilla,
                () -> medirDeleteCola(
                        implementacion,
                        datos,
                        objetivo
                )
        );
    }

    private static Resultado medirEnqueue(
            String implementacion,
            int[] datos) {

        int n = datos.length;
        int k = kMutaciones(n);

        int capacidad =
                implementacion.equals("ColaArreglo")
                        ? n + 1
                        : -1;

        MyQueue<Integer>[] colas =
                crearColas(
                        implementacion,
                        k,
                        datos,
                        capacidad
                );

        int capacidadAntes =
                capacidadCola(colas[0]);

        long inicio =
                System.nanoTime();

        for (MyQueue<Integer> cola : colas) {
            cola.enqueue(SENTINELA);
        }

        long fin =
                System.nanoTime();

        long checksum = 0L;

        for (MyQueue<Integer> cola : colas) {
            checksum += cola.size();
        }

        int capacidadDespues =
                capacidadCola(colas[0]);

        return new Resultado(
                fin - inicio,
                k,
                n,
                n + 1,
                capacidadAntes,
                capacidadDespues,
                crecimiento(
                        capacidadAntes,
                        capacidadDespues
                ),
                checksum
        );
    }

    private static Resultado medirEnqueueForzado(
            int[] datos) {

        int n = datos.length;
        int k = kMutaciones(n);

        MyQueue<Integer>[] colas =
                crearColas(
                        "ColaArreglo",
                        k,
                        datos,
                        n
                );

        int capacidadAntes =
                capacidadCola(colas[0]);

        long inicio =
                System.nanoTime();

        for (MyQueue<Integer> cola : colas) {
            cola.enqueue(SENTINELA);
        }

        long fin =
                System.nanoTime();

        long checksum = 0L;

        for (MyQueue<Integer> cola : colas) {
            checksum += cola.size();
        }

        int capacidadDespues =
                capacidadCola(colas[0]);

        return new Resultado(
                fin - inicio,
                k,
                n,
                n + 1,
                capacidadAntes,
                capacidadDespues,
                "true",
                checksum
        );
    }

    private static Resultado medirSecuenciaEnqueue(
            String implementacion,
            int[] datos) {

        MyQueue<Integer> cola =
                nuevaColaVacia(
                        implementacion
                );

        int capacidadAntes =
                capacidadCola(cola);

        long inicio =
                System.nanoTime();

        for (int valor : datos) {
            cola.enqueue(valor);
        }

        long fin =
                System.nanoTime();

        int capacidadDespues =
                capacidadCola(cola);

        long checksum =
                cola.size();

        if (!cola.isEmpty()) {
            checksum += cola.front();
        }

        String crecimiento;

        if (capacidadAntes < 0) {
            crecimiento = "NA";
        } else if (
                capacidadDespues > capacidadAntes) {
            crecimiento = "multiple";
        } else {
            crecimiento = "false";
        }

        return new Resultado(
                fin - inicio,
                datos.length,
                0,
                datos.length,
                capacidadAntes,
                capacidadDespues,
                crecimiento,
                checksum
        );
    }

    private static Resultado medirDequeue(
            String implementacion,
            int[] datos) {

        int n = datos.length;
        int k = kMutaciones(n);

        int capacidad =
                implementacion.equals("ColaArreglo")
                        ? n
                        : -1;

        MyQueue<Integer>[] colas =
                crearColas(
                        implementacion,
                        k,
                        datos,
                        capacidad
                );

        int capacidadAntes =
                capacidadCola(colas[0]);

        long checksum = 0L;

        long inicio =
                System.nanoTime();

        for (MyQueue<Integer> cola : colas) {
            checksum += cola.dequeue();
        }

        long fin =
                System.nanoTime();

        return new Resultado(
                fin - inicio,
                k,
                n,
                n - 1,
                capacidadAntes,
                capacidadCola(colas[0]),
                "false",
                checksum
        );
    }

    private static Resultado medirFront(
            String implementacion,
            int[] datos) {

        MyQueue<Integer> cola =
                crearCola(
                        implementacion,
                        datos,
                        capacidadNormalCola(
                                implementacion,
                                datos.length
                        )
                );

        int k = kConsultas();

        long checksum = 0L;

        long inicio =
                System.nanoTime();

        for (int i = 0; i < k; i++) {
            checksum += cola.front();
        }

        long fin =
                System.nanoTime();

        int capacidad =
                capacidadCola(cola);

        return new Resultado(
                fin - inicio,
                k,
                datos.length,
                datos.length,
                capacidad,
                capacidad,
                "false",
                checksum
        );
    }

    private static Resultado medirSizeCola(
            String implementacion,
            int[] datos) {

        MyQueue<Integer> cola =
                crearCola(
                        implementacion,
                        datos,
                        capacidadNormalCola(
                                implementacion,
                                datos.length
                        )
                );

        int k = kConsultas();

        long checksum = 0L;

        long inicio =
                System.nanoTime();

        for (int i = 0; i < k; i++) {
            checksum += cola.size();
        }

        long fin =
                System.nanoTime();

        int capacidad =
                capacidadCola(cola);

        return new Resultado(
                fin - inicio,
                k,
                datos.length,
                datos.length,
                capacidad,
                capacidad,
                "false",
                checksum
        );
    }

    private static Resultado medirIsEmptyCola(
            String implementacion,
            int[] datos) {

        MyQueue<Integer> cola =
                crearCola(
                        implementacion,
                        datos,
                        capacidadNormalCola(
                                implementacion,
                                datos.length
                        )
                );

        int k = kConsultas();

        long checksum = 0L;

        long inicio =
                System.nanoTime();

        for (int i = 0; i < k; i++) {

            if (cola.isEmpty()) {
                checksum++;
            }
        }

        long fin =
                System.nanoTime();

        int capacidad =
                capacidadCola(cola);

        return new Resultado(
                fin - inicio,
                k,
                datos.length,
                datos.length,
                capacidad,
                capacidad,
                "false",
                checksum
        );
    }

    private static Resultado medirDeleteCola(
            String implementacion,
            int[] datos,
            int objetivo) {

        int n = datos.length;
        int k = kMutaciones(n);

        int capacidad =
                implementacion.equals("ColaArreglo")
                        ? n
                        : -1;

        MyQueue<Integer>[] colas =
                crearColas(
                        implementacion,
                        k,
                        datos,
                        capacidad
                );

        int capacidadAntes =
                capacidadCola(colas[0]);

        long checksum = 0L;

        long inicio =
                System.nanoTime();

        for (MyQueue<Integer> cola : colas) {

            if (cola.delete(objetivo)) {
                checksum++;
            }
        }

        long fin =
                System.nanoTime();

        boolean encontrado =
                objetivo != -1;

        return new Resultado(
                fin - inicio,
                k,
                n,
                encontrado ? n - 1 : n,
                capacidadAntes,
                capacidadCola(colas[0]),
                "false",
                checksum
        );
    }

    /* =========================================================
       CREACIÓN DE ESTRUCTURAS
       ========================================================= */

    private static MyStack<Integer> nuevaPilaVacia(
            String implementacion) {

        if (implementacion.equals("PilaArreglo")) {
            return new PilaArreglo<>();
        }

        return new PilaEnlazada<>();
    }

    private static MyStack<Integer> crearPila(
            String implementacion,
            int[] datos,
            int capacidad) {

        MyStack<Integer> pila;

        if (implementacion.equals("PilaArreglo")) {

            pila =
                    new PilaArreglo<>(
                            capacidad
                    );

        } else {

            pila =
                    new PilaEnlazada<>();
        }

        for (int valor : datos) {
            pila.push(valor);
        }

        return pila;
    }

    @SuppressWarnings("unchecked")
    private static MyStack<Integer>[] crearPilas(
            String implementacion,
            int cantidad,
            int[] datos,
            int capacidad) {

        MyStack<Integer>[] pilas =
                (MyStack<Integer>[])
                        new MyStack<?>[cantidad];

        for (int i = 0;
             i < cantidad;
             i++) {

            pilas[i] =
                    crearPila(
                            implementacion,
                            datos,
                            capacidad
                    );
        }

        return pilas;
    }

    private static MyQueue<Integer> nuevaColaVacia(
            String implementacion) {

        if (implementacion.equals("ColaArreglo")) {
            return new ColaArreglo<>();
        }

        return new ColaEnlazada<>();
    }

    private static MyQueue<Integer> crearCola(
            String implementacion,
            int[] datos,
            int capacidad) {

        MyQueue<Integer> cola;

        if (implementacion.equals("ColaArreglo")) {

            cola =
                    new ColaArreglo<>(
                            capacidad
                    );

        } else {

            cola =
                    new ColaEnlazada<>();
        }

        for (int valor : datos) {
            cola.enqueue(valor);
        }

        return cola;
    }

    @SuppressWarnings("unchecked")
    private static MyQueue<Integer>[] crearColas(
            String implementacion,
            int cantidad,
            int[] datos,
            int capacidad) {

        MyQueue<Integer>[] colas =
                (MyQueue<Integer>[])
                        new MyQueue<?>[cantidad];

        for (int i = 0;
             i < cantidad;
             i++) {

            colas[i] =
                    crearCola(
                            implementacion,
                            datos,
                            capacidad
                    );
        }

        return colas;
    }

    /* =========================================================
       CAPACIDAD
       ========================================================= */

    private static int capacidadPila(
            MyStack<Integer> pila) {

        if (pila instanceof PilaArreglo<?> arreglo) {
            return arreglo.capacity();
        }

        return -1;
    }

    private static int capacidadCola(
            MyQueue<Integer> cola) {

        if (cola instanceof ColaArreglo<?> arreglo) {
            return arreglo.capacity();
        }

        return -1;
    }

    private static int capacidadNormalPila(
            String implementacion,
            int n) {

        return implementacion.equals("PilaArreglo")
                ? n + 1
                : -1;
    }

    private static int capacidadNormalCola(
            String implementacion,
            int n) {

        return implementacion.equals("ColaArreglo")
                ? n + 1
                : -1;
    }

    private static String crecimiento(
            int capacidadAntes,
            int capacidadDespues) {

        if (capacidadAntes < 0) {
            return "NA";
        }

        return capacidadDespues > capacidadAntes
                ? "true"
                : "false";
    }

    /* =========================================================
       REGISTRO
       ========================================================= */

    private static void registrar(
            PrintWriter csv,
            String bloque,
            String implementacion,
            String metodo,
            String escenario,
            long semilla,
            Experimento experimento) {

        for (int i = 0;
             i < CALENTAMIENTOS;
             i++) {

            Resultado resultado =
                    experimento.ejecutar();

            consumir(
                    resultado.checksum()
            );
        }

        for (int repeticion = 1;
             repeticion <= REPETICIONES;
             repeticion++) {

            Resultado resultado =
                    experimento.ejecutar();

            consumir(
                    resultado.checksum()
            );

            double nsPorOperacion =
                    (double) resultado.totalNs()
                            / resultado.k();

            csv.printf(
                    Locale.US,
                    "%s,%s,%s,%s,%d,%d,%d,%d,%d,%d,%d,%s,%d,%.3f,%d%n",
                    bloque,
                    implementacion,
                    metodo,
                    escenario,
                    semilla,
                    resultado.nInicial(),
                    resultado.nFinal(),
                    resultado.k(),
                    repeticion,
                    resultado.capacidadAntes(),
                    resultado.capacidadDespues(),
                    resultado.crecimiento(),
                    resultado.totalNs(),
                    nsPorOperacion,
                    resultado.checksum()
            );
        }

        csv.flush();
    }

    private static void escribirEncabezado(
            PrintWriter csv) {

        csv.println(
                "bloque,"
                        + "implementacion,"
                        + "metodo,"
                        + "escenario,"
                        + "semilla,"
                        + "n_inicial,"
                        + "n_final,"
                        + "k,"
                        + "repeticion,"
                        + "capacidad_antes,"
                        + "capacidad_despues,"
                        + "crecimiento,"
                        + "total_ns,"
                        + "ns_por_operacion,"
                        + "checksum"
        );
    }

    /* =========================================================
       UTILIDADES
       ========================================================= */

    private static int kMutaciones(int n) {

        int k =
                200_000 / n;

        if (k < 3) {
            return 3;
        }

        if (k > 2_000) {
            return 2_000;
        }

        return k;
    }

    private static int kConsultas() {
        return 50_000;
    }

    private static int[] generarDatos(
            int n,
            long semilla) {

        int[] datos =
                new int[n];

        for (int i = 0; i < n; i++) {
            datos[i] = i;
        }

        Random random =
                new Random(semilla);

        for (int i = n - 1;
             i > 0;
             i--) {

            int j =
                    random.nextInt(i + 1);

            int temporal =
                    datos[i];

            datos[i] =
                    datos[j];

            datos[j] =
                    temporal;
        }

        return datos;
    }

    private static void consumir(
            long valor) {

        blackhole ^= valor;
    }

    private static void escribirEntorno(
            boolean modoRapido)
            throws IOException {

        Path salida =
                Path.of(
                        "resultados",
                        "entorno-pilas-colas.txt"
                );

        try (PrintWriter out =
                     new PrintWriter(
                             Files.newBufferedWriter(salida))) {

            out.println(
                    "fecha=" + Instant.now()
            );

            out.println(
                    "modo="
                            + (modoRapido
                            ? "rapido"
                            : "completo")
            );

            out.println(
                    "java.version="
                            + System.getProperty(
                            "java.version")
            );

            out.println(
                    "java.vendor="
                            + System.getProperty(
                            "java.vendor")
            );

            out.println(
                    "java.vm.name="
                            + System.getProperty(
                            "java.vm.name")
            );

            out.println(
                    "os.name="
                            + System.getProperty(
                            "os.name")
            );

            out.println(
                    "os.version="
                            + System.getProperty(
                            "os.version")
            );

            out.println(
                    "os.arch="
                            + System.getProperty(
                            "os.arch")
            );

            out.println(
                    "processor.identifier="
                            + System.getenv(
                            "PROCESSOR_IDENTIFIER")
            );

            out.println(
                    "procesadores_logicos="
                            + Runtime
                            .getRuntime()
                            .availableProcessors()
            );

            out.println(
                    "max_memory_bytes="
                            + Runtime
                            .getRuntime()
                            .maxMemory()
            );

            out.println(
                    "jvm_args="
                            + ManagementFactory
                            .getRuntimeMXBean()
                            .getInputArguments()
            );

            out.println(
                    "semilla_base="
                            + SEMILLA_BASE
            );

            out.println(
                    "calentamientos="
                            + CALENTAMIENTOS
            );

            out.println(
                    "repeticiones="
                            + REPETICIONES
            );

            out.println(
                    "cronometro=System.nanoTime()"
            );
        }
    }

    @FunctionalInterface
    private interface Experimento {

        Resultado ejecutar();
    }

    private record Resultado(
            long totalNs,
            int k,
            int nInicial,
            int nFinal,
            int capacidadAntes,
            int capacidadDespues,
            String crecimiento,
            long checksum) {
    }
}