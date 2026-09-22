package estructuras.pruebas;

import estructuras.stack.MyStack;
import estructuras.stack.PilaArreglo;
import estructuras.stack.PilaEnlazada;

public class PruebaPilas {

    private static int comprobaciones = 0;

    public static void main(String[] args) {

        probarContratoCompleto(
                new PilaEnlazada<>(),
                "PilaEnlazada"
        );

        probarContratoCompleto(
                new PilaArreglo<>(),
                "PilaArreglo"
        );

        probarDuplicados(
                new PilaEnlazada<>(),
                "PilaEnlazada"
        );

        probarDuplicados(
                new PilaArreglo<>(),
                "PilaArreglo"
        );

        probarErrores(
                new PilaEnlazada<>(),
                "PilaEnlazada"
        );

        probarErrores(
                new PilaArreglo<>(),
                "PilaArreglo"
        );

        probarCrecimientoArreglo();

        System.out.println(
                "Pilas: todas las pruebas fueron superadas."
        );

        System.out.println(
                "Comprobaciones realizadas: " + comprobaciones
        );
    }

    private static void probarContratoCompleto(
            MyStack<Integer> pila,
            String nombre) {

        verificar(
                pila.isEmpty(),
                nombre + ": una pila nueva debe estar vacía."
        );

        verificar(
                pila.size() == 0,
                nombre + ": el tamaño inicial debe ser 0."
        );

        pila.push(10);
        pila.push(20);
        pila.push(30);

        verificar(
                pila.size() == 3,
                nombre + ": el tamaño debe ser 3."
        );

        verificar(
                pila.peek().equals(30),
                nombre + ": la cima debe ser 30."
        );

        verificar(
                pila.size() == 3,
                nombre + ": peek no debe modificar el tamaño."
        );

        verificar(
                pila.pop().equals(30),
                nombre + ": el primer pop debe retornar 30."
        );

        verificar(
                pila.pop().equals(20),
                nombre + ": el segundo pop debe retornar 20."
        );

        verificar(
                pila.pop().equals(10),
                nombre + ": el tercer pop debe retornar 10."
        );

        verificar(
                pila.isEmpty(),
                nombre + ": debe quedar vacía."
        );

        verificar(
                pila.size() == 0,
                nombre + ": el tamaño final debe ser 0."
        );

        /*
         * Comprobamos que pueda reutilizarse
         * después de quedar vacía.
         */
        pila.push(100);

        verificar(
                pila.peek().equals(100),
                nombre + ": debe poder reutilizarse."
        );

        verificar(
                pila.pop().equals(100),
                nombre + ": debe retornar el elemento reinsertado."
        );

        verificar(
                pila.isEmpty(),
                nombre + ": debe volver a quedar vacía."
        );
    }

    private static void probarDuplicados(
            MyStack<Integer> pila,
            String nombre) {

        /*
         * De base a cima:
         *
         * 10, 20A, 20B, 30
         *
         * delete(20) debe eliminar 20B,
         * porque es el primero encontrado desde la cima.
         */
        pila.push(10);
        pila.push(20);
        pila.push(20);
        pila.push(30);

        verificar(
                pila.delete(20),
                nombre + ": delete debe encontrar 20."
        );

        verificar(
                pila.size() == 3,
                nombre + ": delete debe reducir el tamaño."
        );

        verificar(
                pila.pop().equals(30),
                nombre + ": la cima debe continuar siendo 30."
        );

        verificar(
                pila.pop().equals(20),
                nombre
                        + ": debe permanecer el 20 más cercano a la base."
        );

        verificar(
                pila.pop().equals(10),
                nombre + ": el último elemento debe ser 10."
        );

        verificar(
                pila.isEmpty(),
                nombre + ": debe quedar vacía."
        );

        pila.push(1);
        pila.push(2);
        pila.push(3);

        verificar(
                !pila.delete(999),
                nombre
                        + ": delete debe retornar false si no encuentra el valor."
        );

        verificar(
                pila.size() == 3,
                nombre
                        + ": delete fallido no debe modificar el tamaño."
        );

        verificar(
                pila.peek().equals(3),
                nombre
                        + ": delete fallido no debe modificar la cima."
        );
    }

    private static void probarErrores(
            MyStack<Integer> pila,
            String nombre) {

        esperarIllegalState(
                () -> pila.pop(),
                nombre + ": pop sobre vacío debe fallar."
        );

        esperarIllegalState(
                () -> pila.peek(),
                nombre + ": peek sobre vacío debe fallar."
        );

        esperarIllegalArgument(
                () -> pila.push(null),
                nombre + ": push debe rechazar null."
        );

        esperarIllegalArgument(
                () -> pila.delete(null),
                nombre + ": delete debe rechazar null."
        );

        verificar(
                pila.isEmpty(),
                nombre
                        + ": las operaciones inválidas no deben modificarla."
        );
    }

    private static void probarCrecimientoArreglo() {
        PilaArreglo<Integer> pila =
                new PilaArreglo<>(2);

        verificar(
                pila.capacity() == 2,
                "La capacidad inicial debe ser 2."
        );

        pila.push(10);
        pila.push(20);

        verificar(
                pila.capacity() == 2,
                "La capacidad no debe crecer antes de llenarse."
        );

        /*
         * Este push fuerza:
         *
         * 2 -> 4
         */
        pila.push(30);

        verificar(
                pila.capacity() == 4,
                "La capacidad debe duplicarse de 2 a 4."
        );

        pila.push(40);

        verificar(
                pila.capacity() == 4,
                "La capacidad debe continuar en 4."
        );

        /*
         * Este push fuerza:
         *
         * 4 -> 8
         */
        pila.push(50);

        verificar(
                pila.capacity() == 8,
                "La capacidad debe duplicarse de 4 a 8."
        );

        verificar(
                pila.pop().equals(50),
                "El crecimiento debe conservar el orden LIFO."
        );

        verificar(
                pila.pop().equals(40),
                "El siguiente valor debe ser 40."
        );

        verificar(
                pila.pop().equals(30),
                "El siguiente valor debe ser 30."
        );

        verificar(
                pila.pop().equals(20),
                "El siguiente valor debe ser 20."
        );

        verificar(
                pila.pop().equals(10),
                "El último valor debe ser 10."
        );

        /*
         * Decidimos no reducir la capacidad al hacer pop.
         */
        verificar(
                pila.capacity() == 8,
                "La pila no debe reducir automáticamente su capacidad."
        );

        verificar(
                pila.isEmpty(),
                "La pila debe quedar vacía."
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
}