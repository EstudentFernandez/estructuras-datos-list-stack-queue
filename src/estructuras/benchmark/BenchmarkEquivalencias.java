package estructuras.benchmark;

import estructuras.listas.ListaSimpleConCola;
import estructuras.listas.ListaSimpleSinCola;
import estructuras.listas.NodoSimple;
import estructuras.queue.ColaArreglo;
import estructuras.stack.PilaArreglo;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Random;

/**
 * Benchmark para comparar métodos equivalentes entre:
 *
 * - List y Stack
 * - List y Queue
 *
 * Corrección metodológica:
 * las operaciones O(1) se ejecutan en lotes suficientemente
 * grandes para evitar mediciones iguales a cero por la
 * resolución efectiva de System.nanoTime().
 *
 * En las mutaciones, el estado de las estructuras se restaura
 * fuera del intervalo cronometrado.
 */
public final class BenchmarkEquivalencias {

    private static final long SEMILLA_BASE = 20260922L;

    private static final int CALENTAMIENTOS = 5;
    private static final int REPETICIONES = 10;

    /*
     * Mínimo de operaciones realmente medidas por observación.
     */
    private static final int MIN_OPERACIONES_MUTACION = 2_000;
    private static final int MIN_OPERACIONES_CONSULTA = 20_000;

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

    /*
     * Hace observable el resultado del benchmark
     * para dificultar eliminaciones por parte del JIT.
     */
    private static volatile long blackhole = 0L;

    private BenchmarkEquivalencias() {
    }

    public static void main(String[] args)
            throws IOException {

        boolean rapido =
                args.length > 0
                        && args[0].equalsIgnoreCase("--rapido");

        int[] tamanos =
                rapido
                        ? TAMANOS_RAPIDOS
                        : TAMANOS_COMPLETOS;

        Files.createDirectories(
                Path.of("resultados")
        );

        Path salida =
                Path.of(
                        "resultados",
                        "equivalencias_raw.csv"
                );

        try (PrintWriter csv =
                     new PrintWriter(
                             Files.newBufferedWriter(salida))) {

            escribirEncabezado(csv);

            for (int n : tamanos) {

                long semilla =
                        SEMILLA_BASE + n;

                int[] datos =
                        generarDatos(
                                n,
                                semilla
                        );

                System.out.println();

                System.out.println(
                        "Equivalencias - n = " + n
                );

                benchmarkStack(
                        csv,
                        datos,
                        semilla
                );

                benchmarkQueue(
                        csv,
                        datos,
                        semilla
                );
            }
        }

        System.out.println();

        System.out.println(
                "Benchmark de equivalencias finalizado."
        );

        System.out.println(
                "Datos guardados en "
                        + "resultados/equivalencias_raw.csv"
        );

        System.out.println(
                "Blackhole = " + blackhole
        );
    }


    /* =========================================================
       STACK
       ========================================================= */

    private static void benchmarkStack(
            PrintWriter csv,
            int[] datos,
            long semilla) {

        registrar(
                csv,
                "stack",
                "ListaSimpleSinCola",
                "push",
                "pushFront",
                "general",
                semilla,
                () -> medirPushListaStack(datos)
        );

        registrar(
                csv,
                "stack",
                "PilaArreglo",
                "push",
                "push",
                "general",
                semilla,
                () -> medirPushArreglo(datos)
        );

        registrar(
                csv,
                "stack",
                "ListaSimpleSinCola",
                "pop",
                "popFront",
                "general",
                semilla,
                () -> medirPopListaStack(datos)
        );

        registrar(
                csv,
                "stack",
                "PilaArreglo",
                "pop",
                "pop",
                "general",
                semilla,
                () -> medirPopArreglo(datos)
        );

        registrar(
                csv,
                "stack",
                "ListaSimpleSinCola",
                "peek",
                "topFront",
                "general",
                semilla,
                () -> medirPeekListaStack(datos)
        );

        registrar(
                csv,
                "stack",
                "PilaArreglo",
                "peek",
                "peek",
                "general",
                semilla,
                () -> medirPeekArreglo(datos)
        );

        registrar(
                csv,
                "stack",
                "ListaSimpleSinCola",
                "delete",
                "find+erase",
                "base",
                semilla,
                () -> medirDeleteListaStack(datos)
        );

        registrar(
                csv,
                "stack",
                "PilaArreglo",
                "delete",
                "delete",
                "base",
                semilla,
                () -> medirDeleteArregloStack(datos)
        );

        registrar(
                csv,
                "stack",
                "ListaSimpleSinCola",
                "size",
                "size",
                "general",
                semilla,
                () -> medirSizeListaStack(datos)
        );

        registrar(
                csv,
                "stack",
                "PilaArreglo",
                "size",
                "size",
                "general",
                semilla,
                () -> medirSizeArregloStack(datos)
        );

        registrar(
                csv,
                "stack",
                "ListaSimpleSinCola",
                "isEmpty",
                "isEmpty",
                "general",
                semilla,
                () -> medirEmptyListaStack(datos)
        );

        registrar(
                csv,
                "stack",
                "PilaArreglo",
                "isEmpty",
                "isEmpty",
                "general",
                semilla,
                () -> medirEmptyArregloStack(datos)
        );
    }


    /* =========================================================
       QUEUE
       ========================================================= */

    private static void benchmarkQueue(
            PrintWriter csv,
            int[] datos,
            long semilla) {

        registrar(
                csv,
                "queue",
                "ListaSimpleConCola",
                "enqueue",
                "pushBack",
                "general",
                semilla,
                () -> medirEnqueueLista(datos)
        );

        registrar(
                csv,
                "queue",
                "ColaArreglo",
                "enqueue",
                "enqueue",
                "general",
                semilla,
                () -> medirEnqueueArreglo(datos)
        );

        registrar(
                csv,
                "queue",
                "ListaSimpleConCola",
                "dequeue",
                "popFront",
                "general",
                semilla,
                () -> medirDequeueLista(datos)
        );

        registrar(
                csv,
                "queue",
                "ColaArreglo",
                "dequeue",
                "dequeue",
                "general",
                semilla,
                () -> medirDequeueArreglo(datos)
        );

        registrar(
                csv,
                "queue",
                "ListaSimpleConCola",
                "front",
                "topFront",
                "general",
                semilla,
                () -> medirFrontLista(datos)
        );

        registrar(
                csv,
                "queue",
                "ColaArreglo",
                "front",
                "front",
                "general",
                semilla,
                () -> medirFrontArreglo(datos)
        );

        registrar(
                csv,
                "queue",
                "ListaSimpleConCola",
                "delete",
                "find+erase",
                "final",
                semilla,
                () -> medirDeleteListaQueue(datos)
        );

        registrar(
                csv,
                "queue",
                "ColaArreglo",
                "delete",
                "delete",
                "final",
                semilla,
                () -> medirDeleteArregloQueue(datos)
        );

        registrar(
                csv,
                "queue",
                "ListaSimpleConCola",
                "size",
                "size",
                "general",
                semilla,
                () -> medirSizeListaQueue(datos)
        );

        registrar(
                csv,
                "queue",
                "ColaArreglo",
                "size",
                "size",
                "general",
                semilla,
                () -> medirSizeArregloQueue(datos)
        );

        registrar(
                csv,
                "queue",
                "ListaSimpleConCola",
                "isEmpty",
                "isEmpty",
                "general",
                semilla,
                () -> medirEmptyListaQueue(datos)
        );

        registrar(
                csv,
                "queue",
                "ColaArreglo",
                "isEmpty",
                "isEmpty",
                "general",
                semilla,
                () -> medirEmptyArregloQueue(datos)
        );
    }


    /* =========================================================
       STACK - PUSH
       ========================================================= */

    private static Resultado medirPushListaStack(
            int[] datos) {

        int cantidad =
                cantidadEstructurasMutacion(
                        datos.length
                );

        @SuppressWarnings("unchecked")
        ListaSimpleSinCola<Integer>[] listas =
                new ListaSimpleSinCola[cantidad];

        for (int i = 0; i < cantidad; i++) {

            listas[i] =
                    crearListaStack(datos);
        }

        return medirMutacionConstante(
                listas,
                datos.length,

                lista -> {
                    lista.pushFront(
                            SENTINELA
                    );

                    return 0L;
                },

                (lista, resultado) ->
                        lista.popFront()
        );
    }


    private static Resultado medirPushArreglo(
            int[] datos) {

        int cantidad =
                cantidadEstructurasMutacion(
                        datos.length
                );

        @SuppressWarnings("unchecked")
        PilaArreglo<Integer>[] pilas =
                new PilaArreglo[cantidad];

        for (int i = 0; i < cantidad; i++) {

            pilas[i] =
                    crearPilaArreglo(
                            datos,
                            datos.length + 1
                    );
        }

        return medirMutacionConstante(
                pilas,
                datos.length,

                pila -> {
                    pila.push(
                            SENTINELA
                    );

                    return 0L;
                },

                (pila, resultado) ->
                        pila.pop()
        );
    }


    /* =========================================================
       STACK - POP
       ========================================================= */

    private static Resultado medirPopListaStack(
            int[] datos) {

        int cantidad =
                cantidadEstructurasMutacion(
                        datos.length
                );

        @SuppressWarnings("unchecked")
        ListaSimpleSinCola<Integer>[] listas =
                new ListaSimpleSinCola[cantidad];

        for (int i = 0; i < cantidad; i++) {

            listas[i] =
                    crearListaStack(datos);
        }

        return medirMutacionConstante(
                listas,
                datos.length,

                lista ->
                        lista.popFront(),

                (lista, valor) -> {

                    lista.pushFront(
                            (int) valor
                    );

                    return valor;
                }
        );
    }


    private static Resultado medirPopArreglo(
            int[] datos) {

        int cantidad =
                cantidadEstructurasMutacion(
                        datos.length
                );

        @SuppressWarnings("unchecked")
        PilaArreglo<Integer>[] pilas =
                new PilaArreglo[cantidad];

        for (int i = 0; i < cantidad; i++) {

            pilas[i] =
                    crearPilaArreglo(
                            datos,
                            datos.length
                    );
        }

        return medirMutacionConstante(
                pilas,
                datos.length,

                pila ->
                        pila.pop(),

                (pila, valor) -> {

                    pila.push(
                            (int) valor
                    );

                    return valor;
                }
        );
    }


    /* =========================================================
       STACK - PEEK
       ========================================================= */

    private static Resultado medirPeekListaStack(
            int[] datos) {

        int cantidad =
                cantidadEstructurasConsulta(
                        datos.length
                );

        @SuppressWarnings("unchecked")
        ListaSimpleSinCola<Integer>[] listas =
                new ListaSimpleSinCola[cantidad];

        for (int i = 0; i < cantidad; i++) {

            listas[i] =
                    crearListaStack(datos);
        }

        return medirConsultaConstante(
                listas,
                datos.length,
                lista ->
                        lista.topFront()
        );
    }


    private static Resultado medirPeekArreglo(
            int[] datos) {

        int cantidad =
                cantidadEstructurasConsulta(
                        datos.length
                );

        @SuppressWarnings("unchecked")
        PilaArreglo<Integer>[] pilas =
                new PilaArreglo[cantidad];

        for (int i = 0; i < cantidad; i++) {

            pilas[i] =
                    crearPilaArreglo(
                            datos,
                            datos.length
                    );
        }

        return medirConsultaConstante(
                pilas,
                datos.length,
                pila ->
                        pila.peek()
        );
    }


    /* =========================================================
       STACK - DELETE
       ========================================================= */

    private static Resultado medirDeleteListaStack(
            int[] datos) {

        int k =
                cantidadEstructurasMutacion(
                        datos.length
                );

        @SuppressWarnings("unchecked")
        ListaSimpleSinCola<Integer>[] listas =
                new ListaSimpleSinCola[k];

        for (int i = 0; i < k; i++) {

            listas[i] =
                    crearListaStack(datos);
        }

        /*
         * En una pila creada con pushFront,
         * datos[0] queda en la base.
         */
        int objetivo =
                datos[0];

        long checksum = 0L;

        long inicio =
                System.nanoTime();

        for (ListaSimpleSinCola<Integer> lista
                : listas) {

            NodoSimple<Integer> nodo =
                    lista.find(
                            objetivo
                    );

            lista.erase(
                    nodo
            );

            checksum +=
                    lista.size();
        }

        long fin =
                System.nanoTime();

        return new Resultado(
                fin - inicio,
                k,
                datos.length,
                checksum
        );
    }


    private static Resultado medirDeleteArregloStack(
            int[] datos) {

        int k =
                cantidadEstructurasMutacion(
                        datos.length
                );

        @SuppressWarnings("unchecked")
        PilaArreglo<Integer>[] pilas =
                new PilaArreglo[k];

        for (int i = 0; i < k; i++) {

            pilas[i] =
                    crearPilaArreglo(
                            datos,
                            datos.length
                    );
        }

        int objetivo =
                datos[0];

        long checksum = 0L;

        long inicio =
                System.nanoTime();

        for (PilaArreglo<Integer> pila
                : pilas) {

            if (pila.delete(
                    objetivo)) {

                checksum++;
            }
        }

        long fin =
                System.nanoTime();

        return new Resultado(
                fin - inicio,
                k,
                datos.length,
                checksum
        );
    }


    /* =========================================================
       STACK - SIZE
       ========================================================= */

    private static Resultado medirSizeListaStack(
            int[] datos) {

        int cantidad =
                cantidadEstructurasConsulta(
                        datos.length
                );

        @SuppressWarnings("unchecked")
        ListaSimpleSinCola<Integer>[] listas =
                new ListaSimpleSinCola[cantidad];

        for (int i = 0; i < cantidad; i++) {

            listas[i] =
                    crearListaStack(datos);
        }

        return medirConsultaConstante(
                listas,
                datos.length,
                lista ->
                        lista.size()
        );
    }


    private static Resultado medirSizeArregloStack(
            int[] datos) {

        int cantidad =
                cantidadEstructurasConsulta(
                        datos.length
                );

        @SuppressWarnings("unchecked")
        PilaArreglo<Integer>[] pilas =
                new PilaArreglo[cantidad];

        for (int i = 0; i < cantidad; i++) {

            pilas[i] =
                    crearPilaArreglo(
                            datos,
                            datos.length
                    );
        }

        return medirConsultaConstante(
                pilas,
                datos.length,
                pila ->
                        pila.size()
        );
    }


    /* =========================================================
       STACK - ISEMPTY
       ========================================================= */

    private static Resultado medirEmptyListaStack(
            int[] datos) {

        int cantidad =
                cantidadEstructurasConsulta(
                        datos.length
                );

        @SuppressWarnings("unchecked")
        ListaSimpleSinCola<Integer>[] listas =
                new ListaSimpleSinCola[cantidad];

        for (int i = 0; i < cantidad; i++) {

            listas[i] =
                    crearListaStack(datos);
        }

        return medirConsultaConstante(
                listas,
                datos.length,
                lista ->
                        lista.isEmpty()
                                ? 1L
                                : 2L
        );
    }


    private static Resultado medirEmptyArregloStack(
            int[] datos) {

        int cantidad =
                cantidadEstructurasConsulta(
                        datos.length
                );

        @SuppressWarnings("unchecked")
        PilaArreglo<Integer>[] pilas =
                new PilaArreglo[cantidad];

        for (int i = 0; i < cantidad; i++) {

            pilas[i] =
                    crearPilaArreglo(
                            datos,
                            datos.length
                    );
        }

        return medirConsultaConstante(
                pilas,
                datos.length,
                pila ->
                        pila.isEmpty()
                                ? 1L
                                : 2L
        );
    }


    /* =========================================================
       QUEUE - ENQUEUE
       ========================================================= */

    private static Resultado medirEnqueueLista(
            int[] datos) {

        int cantidad =
                cantidadEstructurasMutacion(
                        datos.length
                );

        @SuppressWarnings("unchecked")
        ListaSimpleConCola<Integer>[] listas =
                new ListaSimpleConCola[cantidad];

        for (int i = 0; i < cantidad; i++) {

            listas[i] =
                    crearListaQueue(datos);
        }

        return medirMutacionConstante(
                listas,
                datos.length,

                lista -> {

                    lista.pushBack(
                            SENTINELA
                    );

                    return 0L;
                },

                /*
                 * Se mantiene el tamaño n.
                 *
                 * El orden rota progresivamente,
                 * pero eso no afecta el costo de
                 * pushBack.
                 */
                (lista, resultado) ->
                        lista.popFront()
        );
    }


    private static Resultado medirEnqueueArreglo(
            int[] datos) {

        int cantidad =
                cantidadEstructurasMutacion(
                        datos.length
                );

        @SuppressWarnings("unchecked")
        ColaArreglo<Integer>[] colas =
                new ColaArreglo[cantidad];

        for (int i = 0; i < cantidad; i++) {

            /*
             * Se reserva una posición libre para
             * impedir crecimiento durante enqueue.
             */
            colas[i] =
                    crearColaArreglo(
                            datos,
                            datos.length + 1
                    );
        }

        return medirMutacionConstante(
                colas,
                datos.length,

                cola -> {

                    cola.enqueue(
                            SENTINELA
                    );

                    return 0L;
                },

                (cola, resultado) ->
                        cola.dequeue()
        );
    }


    /* =========================================================
       QUEUE - DEQUEUE
       ========================================================= */

    private static Resultado medirDequeueLista(
            int[] datos) {

        int cantidad =
                cantidadEstructurasMutacion(
                        datos.length
                );

        @SuppressWarnings("unchecked")
        ListaSimpleConCola<Integer>[] listas =
                new ListaSimpleConCola[cantidad];

        for (int i = 0; i < cantidad; i++) {

            listas[i] =
                    crearListaQueue(datos);
        }

        return medirMutacionConstante(
                listas,
                datos.length,

                lista ->
                        lista.popFront(),

                (lista, valor) -> {

                    lista.pushBack(
                            (int) valor
                    );

                    return valor;
                }
        );
    }


    private static Resultado medirDequeueArreglo(
            int[] datos) {

        int cantidad =
                cantidadEstructurasMutacion(
                        datos.length
                );

        @SuppressWarnings("unchecked")
        ColaArreglo<Integer>[] colas =
                new ColaArreglo[cantidad];

        for (int i = 0; i < cantidad; i++) {

            colas[i] =
                    crearColaArreglo(
                            datos,
                            datos.length
                    );
        }

        return medirMutacionConstante(
                colas,
                datos.length,

                cola ->
                        cola.dequeue(),

                /*
                 * Se vuelve a insertar el valor al final.
                 * La cola conserva tamaño n y se ejercita
                 * realmente el comportamiento circular.
                 */
                (cola, valor) -> {

                    cola.enqueue(
                            (int) valor
                    );

                    return valor;
                }
        );
    }


    /* =========================================================
       QUEUE - FRONT
       ========================================================= */

    private static Resultado medirFrontLista(
            int[] datos) {

        int cantidad =
                cantidadEstructurasConsulta(
                        datos.length
                );

        @SuppressWarnings("unchecked")
        ListaSimpleConCola<Integer>[] listas =
                new ListaSimpleConCola[cantidad];

        for (int i = 0; i < cantidad; i++) {

            listas[i] =
                    crearListaQueue(datos);
        }

        return medirConsultaConstante(
                listas,
                datos.length,
                lista ->
                        lista.topFront()
        );
    }


    private static Resultado medirFrontArreglo(
            int[] datos) {

        int cantidad =
                cantidadEstructurasConsulta(
                        datos.length
                );

        @SuppressWarnings("unchecked")
        ColaArreglo<Integer>[] colas =
                new ColaArreglo[cantidad];

        for (int i = 0; i < cantidad; i++) {

            colas[i] =
                    crearColaArreglo(
                            datos,
                            datos.length
                    );
        }

        return medirConsultaConstante(
                colas,
                datos.length,
                cola ->
                        cola.front()
        );
    }


    /* =========================================================
       QUEUE - DELETE
       ========================================================= */

    private static Resultado medirDeleteListaQueue(
            int[] datos) {

        int k =
                cantidadEstructurasMutacion(
                        datos.length
                );

        @SuppressWarnings("unchecked")
        ListaSimpleConCola<Integer>[] listas =
                new ListaSimpleConCola[k];

        for (int i = 0; i < k; i++) {

            listas[i] =
                    crearListaQueue(datos);
        }

        /*
         * Último elemento de la cola:
         * escenario presente de peor caso.
         */
        int objetivo =
                datos[datos.length - 1];

        long checksum = 0L;

        long inicio =
                System.nanoTime();

        for (ListaSimpleConCola<Integer> lista
                : listas) {

            /*
             * La búsqueda está incluida dentro
             * del tiempo porque delete(valor)
             * también debe buscar el valor.
             */
            NodoSimple<Integer> nodo =
                    lista.find(
                            objetivo
                    );

            lista.erase(
                    nodo
            );

            checksum +=
                    lista.size();
        }

        long fin =
                System.nanoTime();

        return new Resultado(
                fin - inicio,
                k,
                datos.length,
                checksum
        );
    }


    private static Resultado medirDeleteArregloQueue(
            int[] datos) {

        int k =
                cantidadEstructurasMutacion(
                        datos.length
                );

        @SuppressWarnings("unchecked")
        ColaArreglo<Integer>[] colas =
                new ColaArreglo[k];

        for (int i = 0; i < k; i++) {

            colas[i] =
                    crearColaArreglo(
                            datos,
                            datos.length
                    );
        }

        int objetivo =
                datos[datos.length - 1];

        long checksum = 0L;

        long inicio =
                System.nanoTime();

        for (ColaArreglo<Integer> cola
                : colas) {

            if (cola.delete(
                    objetivo)) {

                checksum++;
            }
        }

        long fin =
                System.nanoTime();

        return new Resultado(
                fin - inicio,
                k,
                datos.length,
                checksum
        );
    }


    /* =========================================================
       QUEUE - SIZE
       ========================================================= */

    private static Resultado medirSizeListaQueue(
            int[] datos) {

        int cantidad =
                cantidadEstructurasConsulta(
                        datos.length
                );

        @SuppressWarnings("unchecked")
        ListaSimpleConCola<Integer>[] listas =
                new ListaSimpleConCola[cantidad];

        for (int i = 0; i < cantidad; i++) {

            listas[i] =
                    crearListaQueue(datos);
        }

        return medirConsultaConstante(
                listas,
                datos.length,
                lista ->
                        lista.size()
        );
    }


    private static Resultado medirSizeArregloQueue(
            int[] datos) {

        int cantidad =
                cantidadEstructurasConsulta(
                        datos.length
                );

        @SuppressWarnings("unchecked")
        ColaArreglo<Integer>[] colas =
                new ColaArreglo[cantidad];

        for (int i = 0; i < cantidad; i++) {

            colas[i] =
                    crearColaArreglo(
                            datos,
                            datos.length
                    );
        }

        return medirConsultaConstante(
                colas,
                datos.length,
                cola ->
                        cola.size()
        );
    }


    /* =========================================================
       QUEUE - ISEMPTY
       ========================================================= */

    private static Resultado medirEmptyListaQueue(
            int[] datos) {

        int cantidad =
                cantidadEstructurasConsulta(
                        datos.length
                );

        @SuppressWarnings("unchecked")
        ListaSimpleConCola<Integer>[] listas =
                new ListaSimpleConCola[cantidad];

        for (int i = 0; i < cantidad; i++) {

            listas[i] =
                    crearListaQueue(datos);
        }

        return medirConsultaConstante(
                listas,
                datos.length,
                lista ->
                        lista.isEmpty()
                                ? 1L
                                : 2L
        );
    }


    private static Resultado medirEmptyArregloQueue(
            int[] datos) {

        int cantidad =
                cantidadEstructurasConsulta(
                        datos.length
                );

        @SuppressWarnings("unchecked")
        ColaArreglo<Integer>[] colas =
                new ColaArreglo[cantidad];

        for (int i = 0; i < cantidad; i++) {

            colas[i] =
                    crearColaArreglo(
                            datos,
                            datos.length
                    );
        }

        return medirConsultaConstante(
                colas,
                datos.length,
                cola ->
                        cola.isEmpty()
                                ? 1L
                                : 2L
        );
    }


    /* =========================================================
       MEDICIÓN GENÉRICA DE MUTACIONES O(1)
       ========================================================= */

    private static <T> Resultado medirMutacionConstante(
            T[] estructuras,
            int n,
            OperacionMedida<T> operacion,
            Restauracion<T> restauracion) {

        /*
         * Si existen pocas estructuras para un n grande,
         * se reutilizan durante varias rondas.
         */
        int rondas =
                Math.max(
                        1,
                        (
                                MIN_OPERACIONES_MUTACION
                                        + estructuras.length
                                        - 1
                        )
                                / estructuras.length
                );

        long totalNs = 0L;
        long checksum = 0L;

        long[] resultados =
                new long[estructuras.length];

        for (int ronda = 0;
             ronda < rondas;
             ronda++) {

            /*
             * Solo las operaciones objetivo están
             * dentro de este intervalo.
             */
            long inicio =
                    System.nanoTime();

            for (int i = 0;
                 i < estructuras.length;
                 i++) {

                resultados[i] =
                        operacion.ejecutar(
                                estructuras[i]
                        );
            }

            long fin =
                    System.nanoTime();

            totalNs +=
                    fin - inicio;

            /*
             * Restauración completamente fuera
             * del intervalo cronometrado.
             */
            for (int i = 0;
                 i < estructuras.length;
                 i++) {

                checksum +=
                        resultados[i];

                checksum +=
                        restauracion.ejecutar(
                                estructuras[i],
                                resultados[i]
                        );
            }
        }

        int kTotal =
                estructuras.length
                        * rondas;

        return new Resultado(
                totalNs,
                kTotal,
                n,
                checksum
        );
    }


    /* =========================================================
       MEDICIÓN GENÉRICA DE CONSULTAS O(1)
       ========================================================= */

    private static <T> Resultado medirConsultaConstante(
            T[] estructuras,
            int n,
            ConsultaMedida<T> consulta) {

        int rondas =
                Math.max(
                        1,
                        (
                                MIN_OPERACIONES_CONSULTA
                                        + estructuras.length
                                        - 1
                        )
                                / estructuras.length
                );

        long checksum = 0L;

        /*
         * En consultas no hay que restaurar estado,
         * por lo que todo el lote puede medirse con
         * una única pareja de lecturas del reloj.
         */
        long inicio =
                System.nanoTime();

        for (int ronda = 0;
             ronda < rondas;
             ronda++) {

            for (T estructura
                    : estructuras) {

                checksum +=
                        consulta.ejecutar(
                                estructura
                        );
            }
        }

        long fin =
                System.nanoTime();

        int kTotal =
                estructuras.length
                        * rondas;

        return new Resultado(
                fin - inicio,
                kTotal,
                n,
                checksum
        );
    }


    /* =========================================================
       CONSTRUCCIÓN DE ESTRUCTURAS
       ========================================================= */

    private static ListaSimpleSinCola<Integer>
    crearListaStack(int[] datos) {

        ListaSimpleSinCola<Integer> lista =
                new ListaSimpleSinCola<>();

        /*
         * Se insertan los datos con pushFront.
         *
         * El último elemento insertado queda
         * en head y representa la cima.
         */
        for (int valor : datos) {

            lista.pushFront(
                    valor
            );
        }

        return lista;
    }


    private static PilaArreglo<Integer>
    crearPilaArreglo(
            int[] datos,
            int capacidad) {

        PilaArreglo<Integer> pila =
                new PilaArreglo<>(
                        capacidad
                );

        for (int valor : datos) {

            pila.push(
                    valor
            );
        }

        return pila;
    }


    private static ListaSimpleConCola<Integer>
    crearListaQueue(int[] datos) {

        ListaSimpleConCola<Integer> lista =
                new ListaSimpleConCola<>();

        for (int valor : datos) {

            lista.pushBack(
                    valor
            );
        }

        return lista;
    }


    private static ColaArreglo<Integer>
    crearColaArreglo(
            int[] datos,
            int capacidad) {

        ColaArreglo<Integer> cola =
                new ColaArreglo<>(
                        capacidad
                );

        for (int valor : datos) {

            cola.enqueue(
                    valor
            );
        }

        return cola;
    }


    /* =========================================================
       REGISTRO EN CSV
       ========================================================= */

    private static void registrar(
            PrintWriter csv,
            String familia,
            String implementacion,
            String metodoComun,
            String operacion,
            String escenario,
            long semilla,
            Experimento experimento) {

        /*
         * Calentamiento del JIT.
         */
        for (int i = 0;
             i < CALENTAMIENTOS;
             i++) {

            Resultado resultado =
                    experimento.ejecutar();

            consumir(
                    resultado.checksum()
            );
        }

        /*
         * Repeticiones que sí se guardan.
         */
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
                    "%s,%s,%s,%s,%s,%d,%d,%d,%d,%d,%.6f,%d%n",
                    familia,
                    implementacion,
                    metodoComun,
                    operacion,
                    escenario,
                    semilla,
                    resultado.n(),
                    resultado.k(),
                    repeticion,
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
                "familia,"
                        + "implementacion,"
                        + "metodo_comun,"
                        + "operacion,"
                        + "escenario,"
                        + "semilla,"
                        + "n,"
                        + "k,"
                        + "repeticion,"
                        + "total_ns,"
                        + "ns_por_operacion,"
                        + "checksum"
        );
    }


    /* =========================================================
       CANTIDAD DE ESTRUCTURAS
       ========================================================= */

    /**
     * Controla cuántas estructuras independientes
     * se mantienen simultáneamente para mutaciones.
     *
     * El objetivo es evitar un uso excesivo de memoria
     * cuando n es grande.
     */
    private static int cantidadEstructurasMutacion(
            int n) {

        int cantidad =
                200_000 / n;

        if (cantidad < 3) {
            return 3;
        }

        if (cantidad > 2_000) {
            return 2_000;
        }

        return cantidad;
    }


    /**
     * Para consultas basta con un pequeño conjunto
     * de estructuras que se reutiliza muchas veces.
     *
     * Esto reduce memoria y evita medir solamente
     * una única instancia.
     */
    private static int cantidadEstructurasConsulta(
            int n) {

        int cantidad =
                100_000 / n;

        if (cantidad < 8) {
            return 8;
        }

        if (cantidad > 128) {
            return 128;
        }

        return cantidad;
    }


    /* =========================================================
       DATOS
       ========================================================= */

    private static int[] generarDatos(
            int n,
            long semilla) {

        int[] datos =
                new int[n];

        for (int i = 0;
             i < n;
             i++) {

            datos[i] = i;
        }

        Random random =
                new Random(
                        semilla
                );

        /*
         * Fisher-Yates.
         */
        for (int i = n - 1;
             i > 0;
             i--) {

            int j =
                    random.nextInt(
                            i + 1
                    );

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


    /* =========================================================
       CONTRATOS INTERNOS
       ========================================================= */

    @FunctionalInterface
    private interface Experimento {

        Resultado ejecutar();
    }


    @FunctionalInterface
    private interface OperacionMedida<T> {

        long ejecutar(
                T estructura
        );
    }


    @FunctionalInterface
    private interface Restauracion<T> {

        long ejecutar(
                T estructura,
                long resultadoOperacion
        );
    }


    @FunctionalInterface
    private interface ConsultaMedida<T> {

        long ejecutar(
                T estructura
        );
    }


    private record Resultado(
            long totalNs,
            int k,
            int n,
            long checksum) {
    }
}