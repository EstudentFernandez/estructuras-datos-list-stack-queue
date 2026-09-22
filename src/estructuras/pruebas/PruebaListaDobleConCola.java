package estructuras.pruebas;

import estructuras.listas.ListaDobleConCola;
import estructuras.listas.NodoDoble;

public class PruebaListaDobleConCola {

    private static int comprobaciones = 0;

    public static void main(String[] args) {

        probarEstadoInicial();

        probarPushFront();
        probarPushBack();
        probarInsercionesCombinadas();

        probarTopFrontYTopBack();

        probarPopFront();
        probarPopBack();
        probarTransicionesVacioUnElemento();

        probarFind();
        probarFindConDuplicados();

        probarEraseInicio();
        probarEraseMedio();
        probarEraseFinal();
        probarEraseUnico();
        probarErasePorIdentidad();

        probarAddBeforeInicio();
        probarAddBeforeMedio();
        probarAddBeforeFinal();

        probarAddAfterInicio();
        probarAddAfterMedio();
        probarAddAfterFinal();

        probarReferenciasAjenas();
        probarReferenciasEliminadas();

        probarOperacionesSobreVacia();
        probarValoresNull();

        System.out.println(
                "ListaDobleConCola: todas las pruebas fueron superadas."
        );

        System.out.println(
                "Comprobaciones realizadas: " + comprobaciones
        );
    }

    private static void probarEstadoInicial() {
        ListaDobleConCola<Integer> lista =
                new ListaDobleConCola<>();

        verificar(
                lista.isEmpty(),
                "Una lista nueva debe estar vacía."
        );

        verificar(
                lista.size() == 0,
                "Una lista nueva debe tener tamaño 0."
        );
    }

    private static void probarPushFront() {
        ListaDobleConCola<Integer> lista =
                new ListaDobleConCola<>();

        lista.pushFront(30);
        lista.pushFront(20);
        lista.pushFront(10);

        verificarEstructura(
                lista,
                new Integer[]{10, 20, 30},
                "pushFront"
        );
    }

    private static void probarPushBack() {
        ListaDobleConCola<Integer> lista =
                new ListaDobleConCola<>();

        lista.pushBack(10);
        lista.pushBack(20);
        lista.pushBack(30);

        verificarEstructura(
                lista,
                new Integer[]{10, 20, 30},
                "pushBack"
        );
    }

    private static void probarInsercionesCombinadas() {
        ListaDobleConCola<Integer> lista =
                new ListaDobleConCola<>();

        lista.pushBack(20);
        lista.pushFront(10);
        lista.pushBack(30);
        lista.pushFront(5);
        lista.pushBack(40);

        verificarEstructura(
                lista,
                new Integer[]{5, 10, 20, 30, 40},
                "inserciones combinadas"
        );
    }

    private static void probarTopFrontYTopBack() {
        ListaDobleConCola<Integer> lista =
                crearLista(10, 20, 30);

        verificar(
                lista.topFront().equals(10),
                "topFront debe retornar 10."
        );

        verificar(
                lista.topBack().equals(30),
                "topBack debe retornar 30."
        );

        verificar(
                lista.size() == 3,
                "Las consultas top no deben modificar size."
        );

        verificarEstructura(
                lista,
                new Integer[]{10, 20, 30},
                "topFront/topBack"
        );
    }

    private static void probarPopFront() {
        ListaDobleConCola<Integer> lista =
                crearLista(10, 20, 30);

        verificar(
                lista.popFront().equals(10),
                "popFront debe eliminar 10."
        );

        verificar(
                lista.topFront().equals(20),
                "El nuevo head debe contener 20."
        );

        verificar(
                lista.topBack().equals(30),
                "tail debe continuar apuntando a 30."
        );

        verificarEstructura(
                lista,
                new Integer[]{20, 30},
                "popFront"
        );
    }

    private static void probarPopBack() {
        ListaDobleConCola<Integer> lista =
                crearLista(10, 20, 30);

        verificar(
                lista.popBack().equals(30),
                "popBack debe eliminar 30."
        );

        verificar(
                lista.topBack().equals(20),
                "tail debe actualizarse a 20."
        );

        verificarEstructura(
                lista,
                new Integer[]{10, 20},
                "popBack"
        );

        verificar(
                lista.popBack().equals(20),
                "El siguiente popBack debe eliminar 20."
        );

        verificar(
                lista.popBack().equals(10),
                "El último popBack debe eliminar 10."
        );

        verificar(
                lista.isEmpty(),
                "La lista debe terminar vacía."
        );
    }

    private static void probarTransicionesVacioUnElemento() {
        ListaDobleConCola<Integer> lista =
                new ListaDobleConCola<>();

        lista.pushBack(10);

        verificarEstructura(
                lista,
                new Integer[]{10},
                "vacío a un elemento"
        );

        lista.popBack();

        verificar(
                lista.isEmpty(),
                "popBack del único nodo debe vaciar la lista."
        );

        lista.pushFront(20);

        verificarEstructura(
                lista,
                new Integer[]{20},
                "reinserción después de popBack"
        );

        lista.popFront();

        verificar(
                lista.isEmpty(),
                "popFront del único nodo debe vaciar la lista."
        );

        lista.pushBack(30);
        lista.pushBack(40);

        verificarEstructura(
                lista,
                new Integer[]{30, 40},
                "reutilización después de vacío"
        );
    }

    private static void probarFind() {
        ListaDobleConCola<Integer> lista =
                crearLista(10, 20, 30);

        NodoDoble<Integer> nodo =
                lista.find(20);

        verificar(
                nodo != null,
                "find debe localizar 20."
        );

        verificar(
                nodo.getDato().equals(20),
                "find debe retornar el nodo correcto."
        );

        verificar(
                nodo.getPrev() != null
                        && nodo.getPrev().getDato().equals(10),
                "prev de 20 debe ser 10."
        );

        verificar(
                nodo.getNext() != null
                        && nodo.getNext().getDato().equals(30),
                "next de 20 debe ser 30."
        );

        verificar(
                lista.find(999) == null,
                "find debe retornar null si no existe."
        );
    }

    private static void probarFindConDuplicados() {
        ListaDobleConCola<Integer> lista =
                crearLista(10, 20, 20, 30);

        NodoDoble<Integer> primero =
                lista.find(20);

        NodoDoble<Integer> segundo =
                primero.getNext();

        verificar(
                segundo != null
                        && segundo.getDato().equals(20),
                "Debe existir un segundo 20."
        );

        verificar(
                primero != segundo,
                "Los duplicados deben ser nodos distintos."
        );

        verificar(
                segundo.getPrev() == primero,
                "Los enlaces entre duplicados deben ser consistentes."
        );
    }

    private static void probarEraseInicio() {
        ListaDobleConCola<Integer> lista =
                crearLista(10, 20, 30);

        lista.erase(lista.find(10));

        verificarEstructura(
                lista,
                new Integer[]{20, 30},
                "erase inicio"
        );
    }

    private static void probarEraseMedio() {
        ListaDobleConCola<Integer> lista =
                crearLista(10, 20, 30);

        lista.erase(lista.find(20));

        verificarEstructura(
                lista,
                new Integer[]{10, 30},
                "erase medio"
        );
    }

    private static void probarEraseFinal() {
        ListaDobleConCola<Integer> lista =
                crearLista(10, 20, 30);

        lista.erase(lista.find(30));

        verificar(
                lista.topBack().equals(20),
                "erase de tail debe actualizar tail."
        );

        verificarEstructura(
                lista,
                new Integer[]{10, 20},
                "erase final"
        );
    }

    private static void probarEraseUnico() {
        ListaDobleConCola<Integer> lista =
                crearLista(10);

        NodoDoble<Integer> nodo =
                lista.find(10);

        lista.erase(nodo);

        verificar(
                lista.isEmpty(),
                "erase del único nodo debe dejar la lista vacía."
        );

        verificar(
                lista.size() == 0,
                "El tamaño debe ser 0."
        );

        lista.pushBack(20);

        verificarEstructura(
                lista,
                new Integer[]{20},
                "reinserción después de erase único"
        );
    }

    private static void probarErasePorIdentidad() {
        ListaDobleConCola<Integer> lista =
                crearLista(10, 20, 20, 30);

        NodoDoble<Integer> primero =
                lista.find(20);

        NodoDoble<Integer> segundo =
                primero.getNext();

        lista.erase(segundo);

        verificar(
                lista.find(20) == primero,
                "erase debe conservar el primer nodo duplicado."
        );

        verificarEstructura(
                lista,
                new Integer[]{10, 20, 30},
                "erase por identidad"
        );
    }

    private static void probarAddBeforeInicio() {
        ListaDobleConCola<Integer> lista =
                crearLista(20, 30);

        lista.addBefore(
                lista.find(20),
                10
        );

        verificarEstructura(
                lista,
                new Integer[]{10, 20, 30},
                "addBefore inicio"
        );
    }

    private static void probarAddBeforeMedio() {
        ListaDobleConCola<Integer> lista =
                crearLista(10, 30);

        lista.addBefore(
                lista.find(30),
                20
        );

        verificarEstructura(
                lista,
                new Integer[]{10, 20, 30},
                "addBefore medio"
        );
    }

    private static void probarAddBeforeFinal() {
        ListaDobleConCola<Integer> lista =
                crearLista(10, 30);

        NodoDoble<Integer> ultimo =
                lista.find(30);

        lista.addBefore(ultimo, 20);

        verificar(
                lista.topBack().equals(30),
                "addBefore sobre tail debe conservar tail."
        );

        verificarEstructura(
                lista,
                new Integer[]{10, 20, 30},
                "addBefore final"
        );
    }

    private static void probarAddAfterInicio() {
        ListaDobleConCola<Integer> lista =
                crearLista(10, 30);

        lista.addAfter(
                lista.find(10),
                20
        );

        verificarEstructura(
                lista,
                new Integer[]{10, 20, 30},
                "addAfter inicio"
        );
    }

    private static void probarAddAfterMedio() {
        ListaDobleConCola<Integer> lista =
                crearLista(10, 20, 40);

        lista.addAfter(
                lista.find(20),
                30
        );

        verificarEstructura(
                lista,
                new Integer[]{10, 20, 30, 40},
                "addAfter medio"
        );
    }

    private static void probarAddAfterFinal() {
        ListaDobleConCola<Integer> lista =
                crearLista(10, 20);

        lista.addAfter(
                lista.find(20),
                30
        );

        verificar(
                lista.topBack().equals(30),
                "addAfter sobre tail debe actualizar tail."
        );

        verificarEstructura(
                lista,
                new Integer[]{10, 20, 30},
                "addAfter final"
        );
    }

    private static void probarReferenciasAjenas() {
        ListaDobleConCola<Integer> listaA =
                crearLista(10, 20);

        ListaDobleConCola<Integer> listaB =
                crearLista(30, 40);

        NodoDoble<Integer> ajeno =
                listaA.find(10);

        esperarIllegalArgument(
                () -> listaB.erase(ajeno),
                "erase debe rechazar un nodo ajeno."
        );

        esperarIllegalArgument(
                () -> listaB.addBefore(ajeno, 25),
                "addBefore debe rechazar un nodo ajeno."
        );

        esperarIllegalArgument(
                () -> listaB.addAfter(ajeno, 50),
                "addAfter debe rechazar un nodo ajeno."
        );

        verificarEstructura(
                listaB,
                new Integer[]{30, 40},
                "referencias ajenas"
        );
    }

    private static void probarReferenciasEliminadas() {
        ListaDobleConCola<Integer> lista =
                crearLista(10, 20, 30);

        NodoDoble<Integer> eliminado =
                lista.find(20);

        lista.erase(eliminado);

        esperarIllegalArgument(
                () -> lista.erase(eliminado),
                "No puede eliminarse dos veces el mismo nodo."
        );

        esperarIllegalArgument(
                () -> lista.addBefore(eliminado, 15),
                "addBefore debe rechazar nodos eliminados."
        );

        esperarIllegalArgument(
                () -> lista.addAfter(eliminado, 25),
                "addAfter debe rechazar nodos eliminados."
        );

        verificarEstructura(
                lista,
                new Integer[]{10, 30},
                "referencias eliminadas"
        );
    }

    private static void probarOperacionesSobreVacia() {
        ListaDobleConCola<Integer> lista =
                new ListaDobleConCola<>();

        esperarIllegalState(
                () -> lista.popFront(),
                "popFront sobre vacío debe fallar."
        );

        esperarIllegalState(
                () -> lista.popBack(),
                "popBack sobre vacío debe fallar."
        );

        esperarIllegalState(
                () -> lista.topFront(),
                "topFront sobre vacío debe fallar."
        );

        esperarIllegalState(
                () -> lista.topBack(),
                "topBack sobre vacío debe fallar."
        );
    }

    private static void probarValoresNull() {
        ListaDobleConCola<Integer> lista =
                crearLista(10, 20);

        esperarIllegalArgument(
                () -> lista.pushFront(null),
                "pushFront debe rechazar null."
        );

        esperarIllegalArgument(
                () -> lista.pushBack(null),
                "pushBack debe rechazar null."
        );

        esperarIllegalArgument(
                () -> lista.find(null),
                "find debe rechazar null."
        );

        NodoDoble<Integer> nodo =
                lista.find(10);

        esperarIllegalArgument(
                () -> lista.addBefore(nodo, null),
                "addBefore debe rechazar null."
        );

        esperarIllegalArgument(
                () -> lista.addAfter(nodo, null),
                "addAfter debe rechazar null."
        );

        esperarIllegalArgument(
                () -> lista.erase(null),
                "erase debe rechazar referencia null."
        );

        verificarEstructura(
                lista,
                new Integer[]{10, 20},
                "rechazo de null"
        );
    }

    @SafeVarargs
    private static <T> ListaDobleConCola<T> crearLista(
            T... valores) {

        ListaDobleConCola<T> lista =
                new ListaDobleConCola<>();

        for (T valor : valores) {
            lista.pushBack(valor);
        }

        return lista;
    }

    /**
     * Comprueba:
     * - tamaño
     * - valores
     * - topFront
     * - topBack
     * - head.prev == null
     * - último.next == null
     * - consistencia next.prev
     * - consistencia prev.next
     * - recorrido hacia adelante
     * - recorrido hacia atrás
     */
    private static <T> void verificarEstructura(
            ListaDobleConCola<T> lista,
            T[] esperados,
            String contexto) {

        verificar(
                lista.size() == esperados.length,
                contexto + ": tamaño incorrecto."
        );

        if (esperados.length == 0) {
            verificar(
                    lista.isEmpty(),
                    contexto + ": debería estar vacía."
            );

            return;
        }

        verificar(
                !lista.isEmpty(),
                contexto + ": no debería estar vacía."
        );

        verificar(
                lista.topFront().equals(esperados[0]),
                contexto + ": topFront incorrecto."
        );

        verificar(
                lista.topBack().equals(
                        esperados[esperados.length - 1]
                ),
                contexto + ": topBack incorrecto."
        );

        NodoDoble<T> actual =
                lista.find(esperados[0]);

        verificar(
                actual != null,
                contexto + ": no se pudo obtener head."
        );

        verificar(
                actual.getPrev() == null,
                contexto + ": head.prev debe ser null."
        );

        NodoDoble<T> ultimo = null;

        for (int i = 0; i < esperados.length; i++) {

            verificar(
                    actual != null,
                    contexto + ": faltan nodos."
            );

            verificar(
                    actual.getDato().equals(esperados[i]),
                    contexto
                            + ": valor incorrecto en posición "
                            + i
                            + "."
            );

            if (actual.getPrev() != null) {
                verificar(
                        actual.getPrev().getNext() == actual,
                        contexto
                                + ": prev.next debe ser el nodo actual."
                );
            }

            if (actual.getNext() != null) {
                verificar(
                        actual.getNext().getPrev() == actual,
                        contexto
                                + ": next.prev debe ser el nodo actual."
                );
            }

            ultimo = actual;
            actual = actual.getNext();
        }

        verificar(
                actual == null,
                contexto + ": existen nodos adicionales."
        );

        verificar(
                ultimo != null,
                contexto + ": no se encontró el último nodo."
        );

        verificar(
                ultimo.getNext() == null,
                contexto + ": tail.next debe ser null."
        );

        verificar(
                ultimo.getDato().equals(lista.topBack()),
                contexto
                        + ": el último nodo debe coincidir con topBack."
        );

        actual = ultimo;

        for (int i = esperados.length - 1; i >= 0; i--) {

            verificar(
                    actual != null,
                    contexto
                            + ": faltan nodos en recorrido inverso."
            );

            verificar(
                    actual.getDato().equals(esperados[i]),
                    contexto
                            + ": valor incorrecto en recorrido inverso."
            );

            if (actual.getPrev() != null) {
                verificar(
                        actual.getPrev().getNext() == actual,
                        contexto
                                + ": inconsistencia prev/next."
                );
            }

            actual = actual.getPrev();
        }

        verificar(
                actual == null,
                contexto
                        + ": el recorrido inverso debe terminar en null."
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