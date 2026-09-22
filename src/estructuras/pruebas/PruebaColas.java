package estructuras.pruebas;

import estructuras.queue.ColaArreglo;
import estructuras.queue.ColaEnlazada;
import estructuras.queue.MyQueue;

public class PruebaColas {

    private static int comprobaciones = 0;

    public static void main(String[] args) {

        probarContratoCompleto(
                new ColaEnlazada<>(),
                "ColaEnlazada"
        );

        probarContratoCompleto(
                new ColaArreglo<>(),
                "ColaArreglo"
        );

        probarDelete(
                new ColaEnlazada<>(),
                "ColaEnlazada"
        );

        probarDelete(
                new ColaArreglo<>(),
                "ColaArreglo"
        );

        probarDeletePrimeraCoincidencia(
                new ColaEnlazada<>(),
                "ColaEnlazada"
        );

        probarDeletePrimeraCoincidencia(
                new ColaArreglo<>(),
                "ColaArreglo"
        );

        probarErrores(
                new ColaEnlazada<>(),
                "ColaEnlazada"
        );

        probarErrores(
                new ColaArreglo<>(),
                "ColaArreglo"
        );

        probarCircularidadYCrecimiento();

        probarDeleteConCircularidad();

        System.out.println(
                "Colas: todas las pruebas fueron superadas."
        );

        System.out.println(
                "Comprobaciones realizadas: " + comprobaciones
        );
    }

    private static void probarContratoCompleto(
            MyQueue<Integer> cola,
            String nombre) {

        verificar(
                cola.isEmpty(),
                nombre + ": una cola nueva debe estar vacía."
        );

        verificar(
                cola.size() == 0,
                nombre + ": el tamaño inicial debe ser 0."
        );

        cola.enqueue(10);
        cola.enqueue(20);
        cola.enqueue(30);

        verificar(
                cola.size() == 3,
                nombre + ": el tamaño debe ser 3."
        );

        verificar(
                cola.front().equals(10),
                nombre + ": el frente debe ser 10."
        );

        verificar(
                cola.size() == 3,
                nombre + ": front no debe modificar el tamaño."
        );

        verificar(
                cola.dequeue().equals(10),
                nombre
                        + ": el primer dequeue debe retornar 10."
        );

        verificar(
                cola.dequeue().equals(20),
                nombre
                        + ": el segundo dequeue debe retornar 20."
        );

        verificar(
                cola.dequeue().equals(30),
                nombre
                        + ": el tercer dequeue debe retornar 30."
        );

        verificar(
                cola.isEmpty(),
                nombre + ": debe quedar vacía."
        );

        verificar(
                cola.size() == 0,
                nombre + ": el tamaño final debe ser 0."
        );

        /*
         * Reutilización después de quedar vacía.
         */
        cola.enqueue(100);

        verificar(
                cola.front().equals(100),
                nombre + ": debe poder reutilizarse."
        );

        verificar(
                cola.dequeue().equals(100),
                nombre
                        + ": debe retornar el elemento reinsertado."
        );

        verificar(
                cola.isEmpty(),
                nombre + ": debe volver a quedar vacía."
        );
    }

    private static void probarDelete(
            MyQueue<Integer> cola,
            String nombre) {

        cola.enqueue(10);
        cola.enqueue(20);
        cola.enqueue(30);
        cola.enqueue(40);

        verificar(
                cola.delete(20),
                nombre + ": delete debe encontrar 20."
        );

        verificar(
                cola.size() == 3,
                nombre + ": delete debe reducir el tamaño."
        );

        verificar(
                cola.dequeue().equals(10),
                nombre + ": debe conservarse 10."
        );

        verificar(
                cola.dequeue().equals(30),
                nombre
                        + ": 30 debe ocupar el lugar posterior a 10."
        );

        verificar(
                cola.dequeue().equals(40),
                nombre + ": debe conservarse 40."
        );

        verificar(
                cola.isEmpty(),
                nombre + ": debe quedar vacía."
        );

        cola.enqueue(1);
        cola.enqueue(2);
        cola.enqueue(3);

        verificar(
                !cola.delete(999),
                nombre
                        + ": delete debe retornar false si no existe."
        );

        verificar(
                cola.size() == 3,
                nombre
                        + ": delete fallido no debe cambiar size."
        );

        verificar(
                cola.front().equals(1),
                nombre
                        + ": delete fallido no debe cambiar front."
        );

        /*
         * Vaciamos la cola para no dejar estado residual.
         */
        verificar(cola.dequeue().equals(1), nombre);
        verificar(cola.dequeue().equals(2), nombre);
        verificar(cola.dequeue().equals(3), nombre);
    }

    /**
     * Utilizamos objetos diferentes que son iguales según equals()
     * para demostrar que delete elimina realmente la primera
     * coincidencia desde el frente.
     */
    private static void probarDeletePrimeraCoincidencia(
            MyQueue<DatoPrueba> cola,
            String nombre) {

        DatoPrueba primero =
                new DatoPrueba(10, "primero");

        DatoPrueba duplicadoA =
                new DatoPrueba(20, "A");

        DatoPrueba duplicadoB =
                new DatoPrueba(20, "B");

        DatoPrueba ultimo =
                new DatoPrueba(30, "ultimo");

        cola.enqueue(primero);
        cola.enqueue(duplicadoA);
        cola.enqueue(duplicadoB);
        cola.enqueue(ultimo);

        /*
         * Este objeto es distinto, pero equals()
         * lo considera igual a ambos duplicados.
         */
        DatoPrueba buscado =
                new DatoPrueba(20, "busqueda");

        verificar(
                cola.delete(buscado),
                nombre
                        + ": debe encontrar el primer valor equivalente."
        );

        verificar(
                cola.dequeue() == primero,
                nombre + ": primero debe conservarse."
        );

        verificar(
                cola.dequeue() == duplicadoB,
                nombre
                        + ": delete debe eliminar duplicadoA, no duplicadoB."
        );

        verificar(
                cola.dequeue() == ultimo,
                nombre + ": último debe conservarse."
        );

        verificar(
                cola.isEmpty(),
                nombre
                        + ": debe quedar vacía después de la prueba."
        );
    }

    private static void probarErrores(
            MyQueue<Integer> cola,
            String nombre) {

        esperarIllegalState(
                () -> cola.dequeue(),
                nombre
                        + ": dequeue sobre vacío debe fallar."
        );

        esperarIllegalState(
                () -> cola.front(),
                nombre
                        + ": front sobre vacío debe fallar."
        );

        esperarIllegalArgument(
                () -> cola.enqueue(null),
                nombre + ": enqueue debe rechazar null."
        );

        esperarIllegalArgument(
                () -> cola.delete(null),
                nombre + ": delete debe rechazar null."
        );

        verificar(
                cola.isEmpty(),
                nombre
                        + ": operaciones inválidas no deben modificarla."
        );
    }

    /**
     * Caso crítico exigido para un arreglo circular:
     *
     * 1. Llenar capacidad 4.
     * 2. Extraer dos.
     * 3. Insertar dos reutilizando espacios físicos.
     * 4. Provocar crecimiento.
     * 5. Verificar orden FIFO completo.
     */
    private static void probarCircularidadYCrecimiento() {

        ColaArreglo<Integer> cola =
                new ColaArreglo<>(4);

        verificar(
                cola.capacity() == 4,
                "La capacidad inicial debe ser 4."
        );

        cola.enqueue(1);
        cola.enqueue(2);
        cola.enqueue(3);
        cola.enqueue(4);

        verificar(
                cola.capacity() == 4,
                "Llenar cuatro posiciones no debe crecer aún."
        );

        verificar(
                cola.dequeue().equals(1),
                "Debe salir primero 1."
        );

        verificar(
                cola.dequeue().equals(2),
                "Debe salir después 2."
        );

        /*
         * Estos dos elementos deben reutilizar
         * físicamente los espacios liberados al comienzo.
         */
        cola.enqueue(5);
        cola.enqueue(6);

        verificar(
                cola.capacity() == 4,
                "El arreglo circular debe reutilizar espacios libres."
        );

        verificar(
                cola.size() == 4,
                "La cola debe volver a contener cuatro elementos."
        );

        verificar(
                cola.front().equals(3),
                "El frente lógico debe continuar siendo 3."
        );

        /*
         * Está llena. Este enqueue debe provocar:
         *
         * 4 -> 8
         *
         * y la copia debe respetar el orden lógico:
         *
         * 3, 4, 5, 6
         */
        cola.enqueue(7);

        verificar(
                cola.capacity() == 8,
                "La capacidad debe duplicarse de 4 a 8."
        );

        verificar(
                cola.dequeue().equals(3),
                "Después de crecer debe salir 3."
        );

        verificar(
                cola.dequeue().equals(4),
                "Después debe salir 4."
        );

        verificar(
                cola.dequeue().equals(5),
                "Después debe salir 5."
        );

        verificar(
                cola.dequeue().equals(6),
                "Después debe salir 6."
        );

        verificar(
                cola.dequeue().equals(7),
                "Finalmente debe salir 7."
        );

        verificar(
                cola.isEmpty(),
                "La cola debe quedar vacía."
        );

        /*
         * No reducimos capacidad automáticamente.
         */
        verificar(
                cola.capacity() == 8,
                "La capacidad no debe reducirse al vaciar."
        );
    }

    /**
     * Comprueba que delete también funciona cuando
     * el contenido está físicamente dividido por el
     * final del arreglo.
     */
    private static void probarDeleteConCircularidad() {

        ColaArreglo<Integer> cola =
                new ColaArreglo<>(5);

        cola.enqueue(10);
        cola.enqueue(20);
        cola.enqueue(30);
        cola.enqueue(40);
        cola.enqueue(50);

        verificar(cola.dequeue().equals(10), "Debe salir 10.");
        verificar(cola.dequeue().equals(20), "Debe salir 20.");

        /*
         * Estado lógico:
         *
         * 30, 40, 50, 60, 70
         *
         * pero físicamente atraviesa el final
         * del arreglo.
         */
        cola.enqueue(60);
        cola.enqueue(70);

        verificar(
                cola.capacity() == 5,
                "La circularidad debe evitar crecimiento innecesario."
        );

        verificar(
                cola.delete(50),
                "delete debe funcionar en disposición circular."
        );

        verificar(
                cola.size() == 4,
                "El tamaño debe ser 4 después de delete."
        );

        verificar(cola.dequeue().equals(30), "Debe salir 30.");
        verificar(cola.dequeue().equals(40), "Debe salir 40.");
        verificar(cola.dequeue().equals(60), "Debe salir 60.");
        verificar(cola.dequeue().equals(70), "Debe salir 70.");

        verificar(
                cola.isEmpty(),
                "Debe conservarse el orden después de delete circular."
        );
    }

    private static void esperarIllegalArgument(
            Runnable operacion,
            String mensaje) {

        comprobaciones++;

        try {
            operacion.run();
        } catch (IllegalArgumentException e) {
            return;
        }

        throw new AssertionError(mensaje);
    }

    private static void esperarIllegalState(
            Runnable operacion,
            String mensaje) {

        comprobaciones++;

        try {
            operacion.run();
        } catch (IllegalStateException e) {
            return;
        }

        throw new AssertionError(mensaje);
    }

    private static void verificar(
            boolean condicion,
            String mensaje) {

        comprobaciones++;

        if (!condicion) {
            throw new AssertionError(mensaje);
        }
    }

    /**
     * Clase auxiliar para distinguir identidad
     * de igualdad lógica.
     *
     * Dos instancias son iguales si tienen
     * la misma clave, aunque su etiqueta difiera.
     */
    private static class DatoPrueba {

        private final int clave;
        private final String etiqueta;

        DatoPrueba(int clave, String etiqueta) {
            this.clave = clave;
            this.etiqueta = etiqueta;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }

            if (!(obj instanceof DatoPrueba otro)) {
                return false;
            }

            return this.clave == otro.clave;
        }

        @Override
        public String toString() {
            return clave + "-" + etiqueta;
        }
    }
}