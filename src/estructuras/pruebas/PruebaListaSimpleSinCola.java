package estructuras.pruebas;

import estructuras.listas.ListaSimpleSinCola;

public class PruebaListaSimpleSinCola {

    public static void main(String[] args) {

        probarListaVacia();
        probarUnElemento();
        probarVariosElementos();
        probarPopHastaVaciar();
        probarOperacionesInvalidas();
        probarNull();

        System.out.println(
                "Todas las pruebas iniciales de ListaSimpleSinCola fueron superadas."
        );
    }

    private static void probarListaVacia() {
        ListaSimpleSinCola<Integer> lista =
                new ListaSimpleSinCola<>();

        verificar(
                lista.isEmpty(),
                "Una lista nueva debe estar vacía."
        );

        verificar(
                lista.size() == 0,
                "Una lista nueva debe tener tamaño 0."
        );
    }

    private static void probarUnElemento() {
        ListaSimpleSinCola<Integer> lista =
                new ListaSimpleSinCola<>();

        lista.pushFront(10);

        verificar(
                !lista.isEmpty(),
                "La lista no debe estar vacía después de insertar."
        );

        verificar(
                lista.size() == 1,
                "El tamaño debe ser 1 después de una inserción."
        );

        verificar(
                lista.topFront().equals(10),
                "El primer elemento debe ser 10."
        );
    }

    private static void probarVariosElementos() {
        ListaSimpleSinCola<Integer> lista =
                new ListaSimpleSinCola<>();

        lista.pushFront(10);
        lista.pushFront(20);
        lista.pushFront(30);

        verificar(
                lista.size() == 3,
                "El tamaño debe ser 3."
        );

        verificar(
                lista.topFront().equals(30),
                "pushFront debe insertar al inicio."
        );
    }

    private static void probarPopHastaVaciar() {
        ListaSimpleSinCola<Integer> lista =
                new ListaSimpleSinCola<>();

        lista.pushFront(10);
        lista.pushFront(20);
        lista.pushFront(30);

        verificar(
                lista.popFront().equals(30),
                "El primer popFront debe retornar 30."
        );

        verificar(
                lista.size() == 2,
                "El tamaño debe bajar a 2."
        );

        verificar(
                lista.popFront().equals(20),
                "El segundo popFront debe retornar 20."
        );

        verificar(
                lista.popFront().equals(10),
                "El tercer popFront debe retornar 10."
        );

        verificar(
                lista.isEmpty(),
                "La lista debe quedar vacía."
        );

        verificar(
                lista.size() == 0,
                "El tamaño final debe ser 0."
        );
    }

    private static void probarOperacionesInvalidas() {
        ListaSimpleSinCola<Integer> lista =
                new ListaSimpleSinCola<>();

        boolean popLanzoExcepcion = false;

        try {
            lista.popFront();
        } catch (IllegalStateException e) {
            popLanzoExcepcion = true;
        }

        verificar(
                popLanzoExcepcion,
                "popFront sobre una lista vacía debe lanzar IllegalStateException."
        );

        boolean topLanzoExcepcion = false;

        try {
            lista.topFront();
        } catch (IllegalStateException e) {
            topLanzoExcepcion = true;
        }

        verificar(
                topLanzoExcepcion,
                "topFront sobre una lista vacía debe lanzar IllegalStateException."
        );
    }

    private static void probarNull() {
        ListaSimpleSinCola<Integer> lista =
                new ListaSimpleSinCola<>();

        boolean lanzoExcepcion = false;

        try {
            lista.pushFront(null);
        } catch (IllegalArgumentException e) {
            lanzoExcepcion = true;
        }

        verificar(
                lanzoExcepcion,
                "pushFront(null) debe lanzar IllegalArgumentException."
        );

        verificar(
                lista.isEmpty(),
                "Una inserción inválida no debe modificar la lista."
        );
    }

    private static void verificar(
            boolean condicion,
            String mensaje) {

        if (!condicion) {
            throw new AssertionError(mensaje);
        }
    }
}