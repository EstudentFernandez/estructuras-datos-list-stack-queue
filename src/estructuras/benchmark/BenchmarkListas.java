package estructuras.benchmark;

import estructuras.listas.ListaDobleConCola;
import estructuras.listas.ListaDobleSinCola;
import estructuras.listas.ListaSimpleConCola;
import estructuras.listas.ListaSimpleSinCola;
import estructuras.listas.NodoDoble;
import estructuras.listas.NodoSimple;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Locale;
import java.util.Random;

/**
 * Benchmark reproducible para comparar las cuatro
 * implementaciones de listas enlazadas.
 *
 * Las operaciones de preparación y restauración se
 * realizan fuera del intervalo cronometrado.
 */
public final class BenchmarkListas {

    private static final long SEMILLA_BASE = 20260921L;

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

    /*
     * Valor que nunca aparece en los datos,
     * ya que los datos contienen una permutación
     * de 0 ... n - 1.
     */
    private static final int SENTINELA = Integer.MIN_VALUE;

    /*
     * Hace observables los resultados obtenidos
     * para reducir la posibilidad de que la JVM
     * elimine trabajo aparentemente inútil.
     */
    private static volatile long blackhole = 0L;

    private BenchmarkListas() {
    }

    public static void main(String[] args) throws IOException {

        boolean modoRapido =
                args.length > 0
                        && args[0].equalsIgnoreCase("--rapido");

        int[] tamanos =
                modoRapido
                        ? TAMANOS_RAPIDOS
                        : TAMANOS_COMPLETOS;

        Files.createDirectories(Path.of("resultados"));

        escribirEntorno(modoRapido);

        Path archivo =
                Path.of("resultados", "listas_raw.csv");

        try (PrintWriter csv =
                     new PrintWriter(
                             Files.newBufferedWriter(archivo))) {

            escribirEncabezado(csv);

            for (int indiceTamano = 0;
                 indiceTamano < tamanos.length;
                 indiceTamano++) {

                int n = tamanos[indiceTamano];

                long semilla =
                        SEMILLA_BASE + n;

                int[] datos =
                        generarDatos(n, semilla);

                System.out.println();
                System.out.println(
                        "Benchmark de listas - n = " + n
                );

                /*
                 * Rotamos el orden de las implementaciones
                 * según el tamaño para reducir un posible
                 * sesgo sistemático por orden de ejecución.
                 */
                for (int desplazamiento = 0;
                     desplazamiento < 4;
                     desplazamiento++) {

                    int tipo =
                            (indiceTamano + desplazamiento) % 4;

                    FabricaLista fabrica =
                            fabricaPorTipo(tipo);

                    ListaOps temporal = fabrica.crear();

                    System.out.println(
                            "  " + temporal.nombre()
                    );

                    ejecutarBenchmarks(
                            csv,
                            fabrica,
                            datos,
                            semilla
                    );
                }
            }
        }

        System.out.println();
        System.out.println(
                "Benchmark de listas finalizado."
        );

        System.out.println(
                "Datos guardados en resultados/listas_raw.csv"
        );

        System.out.println(
                "Blackhole = " + blackhole
        );
    }

    private static void ejecutarBenchmarks(
            PrintWriter csv,
            FabricaLista fabrica,
            int[] datos,
            long semilla) {

        /*
         * Operaciones constantes en las cuatro listas.
         */
        registrar(
                csv,
                fabrica,
                datos,
                semilla,
                "pushFront",
                "general",
                +1,
                kRapido(),
                BenchmarkListas::medirPushFront
        );

        registrar(
                csv,
                fabrica,
                datos,
                semilla,
                "popFront",
                "general",
                -1,
                kRapido(),
                BenchmarkListas::medirPopFront
        );

        registrar(
                csv,
                fabrica,
                datos,
                semilla,
                "topFront",
                "general",
                0,
                kRapido(),
                BenchmarkListas::medirTopFront
        );

        registrar(
                csv,
                fabrica,
                datos,
                semilla,
                "size",
                "no_vacia",
                0,
                kRapido(),
                BenchmarkListas::medirSize
        );

        registrar(
                csv,
                fabrica,
                datos,
                semilla,
                "isEmpty",
                "no_vacia",
                0,
                kRapido(),
                BenchmarkListas::medirIsEmpty
        );

        /*
         * Operaciones cuya complejidad depende
         * de la representación.
         */
        int k =
                kSegunTamano(datos.length);

        registrar(
                csv,
                fabrica,
                datos,
                semilla,
                "pushBack",
                "general",
                +1,
                k,
                BenchmarkListas::medirPushBack
        );

        registrar(
                csv,
                fabrica,
                datos,
                semilla,
                "popBack",
                "general",
                -1,
                k,
                BenchmarkListas::medirPopBack
        );

        registrar(
                csv,
                fabrica,
                datos,
                semilla,
                "topBack",
                "general",
                0,
                k,
                BenchmarkListas::medirTopBack
        );

        registrarFind(
                csv,
                fabrica,
                datos,
                semilla,
                "inicio",
                0,
                k
        );

        registrarFind(
                csv,
                fabrica,
                datos,
                semilla,
                "medio",
                datos.length / 2,
                k
        );

        registrarFind(
                csv,
                fabrica,
                datos,
                semilla,
                "final",
                datos.length - 1,
                k
        );

        registrarFindAusente(
                csv,
                fabrica,
                datos,
                semilla,
                k
        );

        registrarErase(
                csv,
                fabrica,
                datos,
                semilla,
                "inicio",
                0,
                k
        );

        registrarErase(
                csv,
                fabrica,
                datos,
                semilla,
                "medio",
                datos.length / 2,
                k
        );

        registrarErase(
                csv,
                fabrica,
                datos,
                semilla,
                "final",
                datos.length - 1,
                k
        );

        registrarAddBefore(
                csv,
                fabrica,
                datos,
                semilla,
                "inicio",
                0,
                k
        );

        registrarAddBefore(
                csv,
                fabrica,
                datos,
                semilla,
                "medio",
                datos.length / 2,
                k
        );

        registrarAddBefore(
                csv,
                fabrica,
                datos,
                semilla,
                "final",
                datos.length - 1,
                k
        );

        registrarAddAfter(
                csv,
                fabrica,
                datos,
                semilla,
                "inicio",
                0,
                k
        );

        registrarAddAfter(
                csv,
                fabrica,
                datos,
                semilla,
                "medio",
                datos.length / 2,
                k
        );

        registrarAddAfter(
                csv,
                fabrica,
                datos,
                semilla,
                "final",
                datos.length - 1,
                k
        );
    }

    private static void registrarFind(
            PrintWriter csv,
            FabricaLista fabrica,
            int[] datos,
            long semilla,
            String escenario,
            int posicion,
            int k) {

        int objetivo =
                datos[posicion];

        registrar(
                csv,
                fabrica,
                datos,
                semilla,
                "find",
                escenario,
                0,
                k,
                (lista, valores, operaciones) ->
                        medirFind(
                                lista,
                                objetivo,
                                operaciones
                        )
        );
    }

    private static void registrarFindAusente(
            PrintWriter csv,
            FabricaLista fabrica,
            int[] datos,
            long semilla,
            int k) {

        registrar(
                csv,
                fabrica,
                datos,
                semilla,
                "find",
                "ausente",
                0,
                k,
                (lista, valores, operaciones) ->
                        medirFind(
                                lista,
                                -1,
                                operaciones
                        )
        );
    }

    private static void registrarErase(
            PrintWriter csv,
            FabricaLista fabrica,
            int[] datos,
            long semilla,
            String escenario,
            int posicion,
            int k) {

        registrar(
                csv,
                fabrica,
                datos,
                semilla,
                "erase",
                escenario + "_nodo_prelocalizado",
                -1,
                k,
                (lista, valores, operaciones) ->
                        medirErase(
                                lista,
                                valores,
                                posicion,
                                operaciones
                        )
        );
    }

    private static void registrarAddBefore(
            PrintWriter csv,
            FabricaLista fabrica,
            int[] datos,
            long semilla,
            String escenario,
            int posicion,
            int k) {

        registrar(
                csv,
                fabrica,
                datos,
                semilla,
                "addBefore",
                escenario + "_nodo_prelocalizado",
                +1,
                k,
                (lista, valores, operaciones) ->
                        medirAddBefore(
                                lista,
                                valores[posicion],
                                operaciones
                        )
        );
    }

    private static void registrarAddAfter(
            PrintWriter csv,
            FabricaLista fabrica,
            int[] datos,
            long semilla,
            String escenario,
            int posicion,
            int k) {

        registrar(
                csv,
                fabrica,
                datos,
                semilla,
                "addAfter",
                escenario + "_nodo_prelocalizado",
                +1,
                k,
                (lista, valores, operaciones) ->
                        medirAddAfter(
                                lista,
                                valores[posicion],
                                operaciones
                        )
        );
    }

    /**
     * Ejecuta calentamientos y repeticiones medidas.
     *
     * Cada repetición empieza con una estructura
     * nueva de tamaño n.
     */
    private static void registrar(
            PrintWriter csv,
            FabricaLista fabrica,
            int[] datos,
            long semilla,
            String metodo,
            String escenario,
            int deltaObjetivo,
            int k,
            Medicion medicion) {

        for (int i = 0;
             i < CALENTAMIENTOS;
             i++) {

            ListaOps lista =
                    prepararLista(
                            fabrica,
                            datos
                    );

            Resultado resultado =
                    medicion.ejecutar(
                            lista,
                            datos,
                            k
                    );

            consumir(resultado.checksum());
        }

        for (int repeticion = 1;
             repeticion <= REPETICIONES;
             repeticion++) {

            ListaOps lista =
                    prepararLista(
                            fabrica,
                            datos
                    );

            Resultado resultado =
                    medicion.ejecutar(
                            lista,
                            datos,
                            k
                    );

            consumir(resultado.checksum());

            double nsPorOperacion =
                    (double) resultado.totalNs() / k;

            csv.printf(
                    Locale.US,
                    "%s,%s,%s,%s,%d,%d,%d,%d,%d,%d,%s,%s,%d,%.3f,%d%n",
                    "listas",
                    lista.nombre(),
                    metodo,
                    escenario,
                    semilla,
                    datos.length,
                    datos.length + deltaObjetivo,
                    k,
                    repeticion,
                    -1,
                    "NA",
                    "true",
                    resultado.totalNs(),
                    nsPorOperacion,
                    resultado.checksum()
            );
        }

        csv.flush();
    }

    /**
     * Precarga todas las implementaciones en O(n)
     * usando pushFront en orden inverso.
     *
     * El orden lógico final coincide exactamente
     * con el arreglo datos.
     */
    private static ListaOps prepararLista(
            FabricaLista fabrica,
            int[] datos) {

        ListaOps lista =
                fabrica.crear();

        for (int i = datos.length - 1;
             i >= 0;
             i--) {

            lista.pushFront(datos[i]);
        }

        return lista;
    }

    private static Resultado medirPushFront(
            ListaOps lista,
            int[] datos,
            int k) {

        long total = 0L;
        long checksum = 0L;

        for (int i = 0; i < k; i++) {

            long inicio = System.nanoTime();

            lista.pushFront(SENTINELA);

            long fin = System.nanoTime();

            total += fin - inicio;

            /*
             * Restauración fuera del reloj.
             */
            checksum += lista.popFront();
        }

        return new Resultado(total, checksum);
    }

    private static Resultado medirPushBack(
            ListaOps lista,
            int[] datos,
            int k) {

        long total = 0L;
        long checksum = 0L;

        for (int i = 0; i < k; i++) {

            long inicio = System.nanoTime();

            lista.pushBack(SENTINELA);

            long fin = System.nanoTime();

            total += fin - inicio;

            checksum += lista.popBack();
        }

        return new Resultado(total, checksum);
    }

    private static Resultado medirPopFront(
            ListaOps lista,
            int[] datos,
            int k) {

        long total = 0L;
        long checksum = 0L;

        for (int i = 0; i < k; i++) {

            long inicio = System.nanoTime();

            int valor = lista.popFront();

            long fin = System.nanoTime();

            total += fin - inicio;
            checksum += valor;

            /*
             * Restauramos exactamente el frente.
             */
            lista.pushFront(valor);
        }

        return new Resultado(total, checksum);
    }

    private static Resultado medirPopBack(
            ListaOps lista,
            int[] datos,
            int k) {

        long total = 0L;
        long checksum = 0L;

        for (int i = 0; i < k; i++) {

            long inicio = System.nanoTime();

            int valor = lista.popBack();

            long fin = System.nanoTime();

            total += fin - inicio;
            checksum += valor;

            lista.pushBack(valor);
        }

        return new Resultado(total, checksum);
    }

    private static Resultado medirTopFront(
        ListaOps lista,
        int[] datos,
        int k) {

        long checksum = 0L;

        long inicio = System.nanoTime();

        for (int i = 0; i < k; i++) {
                checksum += lista.topFront();
        }

        long fin = System.nanoTime();

        return new Resultado(
                fin - inicio,
                checksum
        );
        }

    private static Resultado medirTopBack(
        ListaOps lista,
        int[] datos,
        int k) {

        long checksum = 0L;

        long inicio = System.nanoTime();

        for (int i = 0; i < k; i++) {
                checksum += lista.topBack();
        }

        long fin = System.nanoTime();

        return new Resultado(
                fin - inicio,
                checksum
        );
        }

    private static Resultado medirFind(
            ListaOps lista,
            int objetivo,
            int k) {

        long total = 0L;
        long checksum = 0L;

        for (int i = 0; i < k; i++) {

            long inicio = System.nanoTime();

            Object nodo =
                    lista.find(objetivo);

            long fin = System.nanoTime();

            total += fin - inicio;

            if (nodo == null) {
                checksum += 1;
            } else {
                checksum +=
                        System.identityHashCode(nodo);
            }
        }

        return new Resultado(total, checksum);
    }

    private static Resultado medirErase(
            ListaOps lista,
            int[] datos,
            int posicion,
            int k) {

        long total = 0L;
        long checksum = 0L;

        int objetivo =
                datos[posicion];

        for (int i = 0; i < k; i++) {

            /*
             * La referencia se obtiene ANTES del reloj.
             */
            Object nodo =
                    lista.find(objetivo);

            long inicio = System.nanoTime();

            lista.erase(nodo);

            long fin = System.nanoTime();

            total += fin - inicio;
            checksum += objetivo;

            /*
             * Restauración fuera del cronómetro.
             */
            restaurarElemento(
                    lista,
                    datos,
                    posicion,
                    objetivo
            );
        }

        return new Resultado(total, checksum);
    }

    private static Resultado medirAddBefore(
            ListaOps lista,
            int objetivo,
            int k) {

        long total = 0L;
        long checksum = 0L;

        /*
         * Este nodo permanece vigente durante
         * toda la muestra.
         */
        Object nodoObjetivo =
                lista.find(objetivo);

        for (int i = 0; i < k; i++) {

            long inicio = System.nanoTime();

            lista.addBefore(
                    nodoObjetivo,
                    SENTINELA
            );

            long fin = System.nanoTime();

            total += fin - inicio;
            checksum += lista.size();

            /*
             * Eliminamos el nodo insertado
             * fuera del intervalo medido.
             */
            Object insertado =
                    lista.find(SENTINELA);

            lista.erase(insertado);
        }

        return new Resultado(total, checksum);
    }

    private static Resultado medirAddAfter(
            ListaOps lista,
            int objetivo,
            int k) {

        long total = 0L;
        long checksum = 0L;

        Object nodoObjetivo =
                lista.find(objetivo);

        for (int i = 0; i < k; i++) {

            long inicio = System.nanoTime();

            lista.addAfter(
                    nodoObjetivo,
                    SENTINELA
            );

            long fin = System.nanoTime();

            total += fin - inicio;
            checksum += lista.size();

            Object insertado =
                    lista.find(SENTINELA);

            lista.erase(insertado);
        }

        return new Resultado(total, checksum);
    }

    private static Resultado medirSize(
        ListaOps lista,
        int[] datos,
        int k) {

        long checksum = 0L;

        long inicio = System.nanoTime();

        for (int i = 0; i < k; i++) {
                checksum += lista.size();
        }

        long fin = System.nanoTime();

        return new Resultado(
                fin - inicio,
                checksum
        );
        }

    private static Resultado medirIsEmpty(
        ListaOps lista,
        int[] datos,
        int k) {

        long checksum = 0L;

        long inicio = System.nanoTime();

        for (int i = 0; i < k; i++) {
                checksum += lista.isEmpty() ? 1 : 0;
        }

        long fin = System.nanoTime();

        return new Resultado(
                fin - inicio,
                checksum
        );
        }

    private static void restaurarElemento(
            ListaOps lista,
            int[] datos,
            int posicion,
            int valor) {

        if (posicion == 0) {
            lista.pushFront(valor);
            return;
        }

        if (posicion == datos.length - 1) {
            lista.pushBack(valor);
            return;
        }

        Object anterior =
                lista.find(
                        datos[posicion - 1]
                );

        lista.addAfter(
                anterior,
                valor
        );
    }

    /**
     * Genera una permutación pseudoaleatoria
     * de 0 ... n - 1.
     *
     * Los valores son únicos y reproducibles.
     */
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

        for (int i = n - 1; i > 0; i--) {

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

    /**
     * Número de operaciones por muestra para
     * métodos potencialmente lineales.
     *
     * Se reduce k al aumentar n para controlar
     * el tiempo total del experimento.
     */
    private static int kSegunTamano(int n) {

        if (n <= 100) {
                return 200;
        }

        if (n <= 1_000) {
                return 100;
        }

        if (n <= 10_000) {
                return 20;
        }

        if (n <= 100_000) {
                return 20;
        }

        return 1;
        }

    /**
     * Para operaciones Θ(1) utilizamos un lote
     * mayor para reducir el peso relativo del
     * propio cronómetro.
     */
    private static int kRapido() {
        return 2_000;
    }

    private static void consumir(long valor) {
        blackhole ^= valor;
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
                        + "n_final_objetivo,"
                        + "k,"
                        + "repeticion,"
                        + "capacidad,"
                        + "crecimiento,"
                        + "restauracion_fuera_reloj,"
                        + "total_ns,"
                        + "ns_por_operacion,"
                        + "checksum"
        );
    }

    /**
     * Guarda información básica necesaria para
     * documentar el entorno experimental.
     */
    private static void escribirEntorno(
            boolean modoRapido)
            throws IOException {

        Path archivo =
                Path.of(
                        "resultados",
                        "entorno-benchmark.txt"
                );

        try (PrintWriter out =
                     new PrintWriter(
                             Files.newBufferedWriter(archivo))) {

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

    private static FabricaLista fabricaPorTipo(
            int tipo) {

        return switch (tipo) {

            case 0 ->
                    SimpleSinColaOps::new;

            case 1 ->
                    SimpleConColaOps::new;

            case 2 ->
                    DobleSinColaOps::new;

            case 3 ->
                    DobleConColaOps::new;

            default ->
                    throw new IllegalArgumentException(
                            "Tipo de lista inválido."
                    );
        };
    }

    @FunctionalInterface
    private interface FabricaLista {

        ListaOps crear();
    }

    @FunctionalInterface
    private interface Medicion {

        Resultado ejecutar(
                ListaOps lista,
                int[] datos,
                int k
        );
    }

    private interface ListaOps {

        String nombre();

        void pushFront(int valor);

        void pushBack(int valor);

        int popFront();

        int popBack();

        int topFront();

        int topBack();

        Object find(int valor);

        void erase(Object nodo);

        void addBefore(
                Object nodo,
                int valor
        );

        void addAfter(
                Object nodo,
                int valor
        );

        int size();

        boolean isEmpty();
    }

    private record Resultado(
            long totalNs,
            long checksum) {
    }

    private static final class SimpleSinColaOps
            implements ListaOps {

        private final ListaSimpleSinCola<Integer> lista =
                new ListaSimpleSinCola<>();

        @Override
        public String nombre() {
            return "ListaSimpleSinCola";
        }

        @Override
        public void pushFront(int valor) {
            lista.pushFront(valor);
        }

        @Override
        public void pushBack(int valor) {
            lista.pushBack(valor);
        }

        @Override
        public int popFront() {
            return lista.popFront();
        }

        @Override
        public int popBack() {
            return lista.popBack();
        }

        @Override
        public int topFront() {
            return lista.topFront();
        }

        @Override
        public int topBack() {
            return lista.topBack();
        }

        @Override
        public Object find(int valor) {
            return lista.find(valor);
        }

        @SuppressWarnings("unchecked")
        @Override
        public void erase(Object nodo) {
            lista.erase(
                    (NodoSimple<Integer>) nodo
            );
        }

        @SuppressWarnings("unchecked")
        @Override
        public void addBefore(
                Object nodo,
                int valor) {

            lista.addBefore(
                    (NodoSimple<Integer>) nodo,
                    valor
            );
        }

        @SuppressWarnings("unchecked")
        @Override
        public void addAfter(
                Object nodo,
                int valor) {

            lista.addAfter(
                    (NodoSimple<Integer>) nodo,
                    valor
            );
        }

        @Override
        public int size() {
            return lista.size();
        }

        @Override
        public boolean isEmpty() {
            return lista.isEmpty();
        }
    }

    private static final class SimpleConColaOps
            implements ListaOps {

        private final ListaSimpleConCola<Integer> lista =
                new ListaSimpleConCola<>();

        @Override
        public String nombre() {
            return "ListaSimpleConCola";
        }

        @Override
        public void pushFront(int valor) {
            lista.pushFront(valor);
        }

        @Override
        public void pushBack(int valor) {
            lista.pushBack(valor);
        }

        @Override
        public int popFront() {
            return lista.popFront();
        }

        @Override
        public int popBack() {
            return lista.popBack();
        }

        @Override
        public int topFront() {
            return lista.topFront();
        }

        @Override
        public int topBack() {
            return lista.topBack();
        }

        @Override
        public Object find(int valor) {
            return lista.find(valor);
        }

        @SuppressWarnings("unchecked")
        @Override
        public void erase(Object nodo) {
            lista.erase(
                    (NodoSimple<Integer>) nodo
            );
        }

        @SuppressWarnings("unchecked")
        @Override
        public void addBefore(
                Object nodo,
                int valor) {

            lista.addBefore(
                    (NodoSimple<Integer>) nodo,
                    valor
            );
        }

        @SuppressWarnings("unchecked")
        @Override
        public void addAfter(
                Object nodo,
                int valor) {

            lista.addAfter(
                    (NodoSimple<Integer>) nodo,
                    valor
            );
        }

        @Override
        public int size() {
            return lista.size();
        }

        @Override
        public boolean isEmpty() {
            return lista.isEmpty();
        }
    }

    private static final class DobleSinColaOps
            implements ListaOps {

        private final ListaDobleSinCola<Integer> lista =
                new ListaDobleSinCola<>();

        @Override
        public String nombre() {
            return "ListaDobleSinCola";
        }

        @Override
        public void pushFront(int valor) {
            lista.pushFront(valor);
        }

        @Override
        public void pushBack(int valor) {
            lista.pushBack(valor);
        }

        @Override
        public int popFront() {
            return lista.popFront();
        }

        @Override
        public int popBack() {
            return lista.popBack();
        }

        @Override
        public int topFront() {
            return lista.topFront();
        }

        @Override
        public int topBack() {
            return lista.topBack();
        }

        @Override
        public Object find(int valor) {
            return lista.find(valor);
        }

        @SuppressWarnings("unchecked")
        @Override
        public void erase(Object nodo) {
            lista.erase(
                    (NodoDoble<Integer>) nodo
            );
        }

        @SuppressWarnings("unchecked")
        @Override
        public void addBefore(
                Object nodo,
                int valor) {

            lista.addBefore(
                    (NodoDoble<Integer>) nodo,
                    valor
            );
        }

        @SuppressWarnings("unchecked")
        @Override
        public void addAfter(
                Object nodo,
                int valor) {

            lista.addAfter(
                    (NodoDoble<Integer>) nodo,
                    valor
            );
        }

        @Override
        public int size() {
            return lista.size();
        }

        @Override
        public boolean isEmpty() {
            return lista.isEmpty();
        }
    }

    private static final class DobleConColaOps
            implements ListaOps {

        private final ListaDobleConCola<Integer> lista =
                new ListaDobleConCola<>();

        @Override
        public String nombre() {
            return "ListaDobleConCola";
        }

        @Override
        public void pushFront(int valor) {
            lista.pushFront(valor);
        }

        @Override
        public void pushBack(int valor) {
            lista.pushBack(valor);
        }

        @Override
        public int popFront() {
            return lista.popFront();
        }

        @Override
        public int popBack() {
            return lista.popBack();
        }

        @Override
        public int topFront() {
            return lista.topFront();
        }

        @Override
        public int topBack() {
            return lista.topBack();
        }

        @Override
        public Object find(int valor) {
            return lista.find(valor);
        }

        @SuppressWarnings("unchecked")
        @Override
        public void erase(Object nodo) {
            lista.erase(
                    (NodoDoble<Integer>) nodo
            );
        }

        @SuppressWarnings("unchecked")
        @Override
        public void addBefore(
                Object nodo,
                int valor) {

            lista.addBefore(
                    (NodoDoble<Integer>) nodo,
                    valor
            );
        }

        @SuppressWarnings("unchecked")
        @Override
        public void addAfter(
                Object nodo,
                int valor) {

            lista.addAfter(
                    (NodoDoble<Integer>) nodo,
                    valor
            );
        }

        @Override
        public int size() {
            return lista.size();
        }

        @Override
        public boolean isEmpty() {
            return lista.isEmpty();
        }
    }
}