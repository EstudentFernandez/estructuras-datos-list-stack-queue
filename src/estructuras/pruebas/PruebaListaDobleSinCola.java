package estructuras.pruebas;

import estructuras.listas.ListaDobleSinCola;
import estructuras.listas.NodoDoble;

public class PruebaListaDobleSinCola {

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

        probarAddAfterMedio();
        probarAddAfterFinal();

        probarReferenciasAjenas();
        probarReferenciasEliminadas();

        probarOperacionesSobreVacia();
        probarValoresNull();

        System.out.println(
                "ListaDobleSinCola: todas las pruebas fueron superadas."
        );

        System.out.println(
                "Comprobaciones realizadas: " + comprobaciones
        );
    }

    private static void probarEstadoInicial() {
        ListaDobleSinCola<Integer> lista =
                new ListaDobleSinCola<>();

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
        ListaDobleSinCola<Integer> lista =
                new ListaDobleSinCola<>();

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
        ListaDobleSinCola<Integer> lista =
                new ListaDobleSinCola<>();

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
        ListaDobleSinCola<Integer> lista =
                new ListaDobleSinCola<>();

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
        ListaDobleSinCola<Integer> lista =
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
        ListaDobleSinCola<Integer> lista =
                crearLista(10, 20, 30);

        verificar(
                lista.popFront().equals(10),
                "popFront debe eliminar 10."
        );

        verificarEstructura(
                lista,
                new Integer[]{20, 30},
                "popFront"
        );
    }

    private static void probarPopBack() {
        ListaDobleSinCola<Integer> lista =
                crearLista(10, 20, 30);

        verificar(
                lista.popBack().equals(30),
                "popBack debe eliminar 30."
        );

        verificarEstructura(
                lista,
                new Integer[]{10, 20},
                "popBack con varios nodos"
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
                "La lista debe quedar vacía."
        );
    }

    private static void probarTransicionesVacioUnElemento() {
        ListaDobleSinCola<Integer> lista =
                new ListaDobleSinCola<>();

        lista.pushFront(10);

        verificarEstructura(
                lista,
                new Integer[]{10},
                "transición vacío a un elemento"
        );

        lista.popFront();

        verificar(
                lista.isEmpty(),
                "La lista debe quedar vacía."
        );

        lista.pushBack(20);

        verificarEstructura(
                lista,
                new Integer[]{20},
                "reinserción después de vacío"
        );

        lista.popBack();

        verificar(
                lista.isEmpty(),
                "La lista debe quedar vacía nuevamente."
        );
    }

    private static void probarFind() {
        ListaDobleSinCola<Integer> lista =
                crearLista(10, 20, 30);

        NodoDoble<Integer> nodo =
                lista.find(20);

        verificar(
                nodo != null,
                "find debe encontrar 20."
        );

        verificar(
                nodo.getDato().equals(20),
                "find debe retornar el nodo correcto."
        );

        verificar(
                nodo.getPrev() != null
                        && nodo.getPrev().getDato().equals(10),
                "El prev de 20 debe ser 10."
        );

        verificar(
                nodo.getNext() != null
                        && nodo.getNext().getDato().equals(30),
                "El next de 20 debe ser 30."
        );

        verificar(
                lista.find(999) == null,
                "find debe retornar null si el valor está ausente."
        );
    }

    private static void probarFindConDuplicados() {
        ListaDobleSinCola<Integer> lista =
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
                "Los duplicados deben tener identidad distinta."
        );

        verificar(
                segundo.getPrev() == primero,
                "El prev del segundo 20 debe ser el primero."
        );
    }

    private static void probarEraseInicio() {
        ListaDobleSinCola<Integer> lista =
                crearLista(10, 20, 30);

        lista.erase(lista.find(10));

        verificarEstructura(
                lista,
                new Integer[]{20, 30},
                "erase inicio"
        );
    }

    private static void probarEraseMedio() {
        ListaDobleSinCola<Integer> lista =
                crearLista(10, 20, 30);

        lista.erase(lista.find(20));

        verificarEstructura(
                lista,
                new Integer[]{10, 30},
                "erase medio"
        );
    }

    private static void probarEraseFinal() {
        ListaDobleSinCola<Integer> lista =
                crearLista(10, 20, 30);

        lista.erase(lista.find(30));

        verificarEstructura(
                lista,
                new Integer[]{10, 20},
                "erase final"
        );
    }

    private static void probarEraseUnico() {
        ListaDobleSinCola<Integer> lista =
                crearLista(10);

        lista.erase(lista.find(10));

        verificar(
                lista.isEmpty(),
                "erase del único nodo debe dejar la lista vacía."
        );

        verificar(
                lista.size() == 0,
                "El tamaño debe ser 0."
        );
    }

    private static void probarErasePorIdentidad() {
        ListaDobleSinCola<Integer> lista =
                crearLista(10, 20, 20, 30);

        NodoDoble<Integer> primero =
                lista.find(20);

        NodoDoble<Integer> segundo =
                primero.getNext();

        lista.erase(segundo);

        verificar(
                lista.find(20) == primero,
                "Debe mantenerse el primer nodo con valor 20."
        );

        verificarEstructura(
                lista,
                new Integer[]{10, 20, 30},
                "erase por identidad"
        );
    }

    private static void probarAddBeforeInicio() {
        ListaDobleSinCola<Integer> lista =
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
        ListaDobleSinCola<Integer> lista =
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

    private static void probarAddAfterMedio() {
        ListaDobleSinCola<Integer> lista =
                crearLista(10, 30);

        lista.addAfter(
                lista.find(10),
                20
        );

        verificarEstructura(
                lista,
                new Integer[]{10, 20, 30},
                "addAfter medio"
        );
    }

    private static void probarAddAfterFinal() {
        ListaDobleSinCola<Integer> lista =
                crearLista(10, 20);

        lista.addAfter(
                lista.find(20),
                30
        );

        verificarEstructura(
                lista,
                new Integer[]{10, 20, 30},
                "addAfter final"
        );
    }

    private static void probarReferenciasAjenas() {
        ListaDobleSinCola<Integer> listaA =
                crearLista(10, 20);

        ListaDobleSinCola<Integer> listaB =
                crearLista(30, 40);

        NodoDoble<Integer> ajeno =
                listaA.find(10);

        esperarIllegalArgument(
                () -> listaB.erase(ajeno),
                "erase debe rechazar nodos ajenos."
        );

        esperarIllegalArgument(
                () -> listaB.addBefore(ajeno, 25),
                "addBefore debe rechazar nodos ajenos."
        );

        esperarIllegalArgument(
                () -> listaB.addAfter(ajeno, 50),
                "addAfter debe rechazar nodos ajenos."
        );

        verificarEstructura(
                listaB,
                new Integer[]{30, 40},
                "referencias ajenas"
        );
    }

    private static void probarReferenciasEliminadas() {
        ListaDobleSinCola<Integer> lista =
                crearLista(10, 20, 30);

        NodoDoble<Integer> eliminado =
                lista.find(20);

        lista.erase(eliminado);

        esperarIllegalArgument(
                () -> lista.erase(eliminado),
                "No debe poder eliminarse dos veces el mismo nodo."
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
        ListaDobleSinCola<Integer> lista =
                new ListaDobleSinCola<>();

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
        ListaDobleSinCola<Integer> lista =
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
                "erase debe rechazar una referencia null."
        );

        verificarEstructura(
                lista,
                new Integer[]{10, 20},
                "rechazo de null"
        );
    }

    @SafeVarargs
    private static <T> ListaDobleSinCola<T> crearLista(
            T... valores) {

        ListaDobleSinCola<T> lista =
                new ListaDobleSinCola<>();

        for (T valor : valores) {
            lista.pushBack(valor);
        }

        return lista;
    }

    /**
     * Comprueba contenido, tamaño y consistencia
     * de los enlaces next/prev en ambos sentidos.
     */
    private static <T> void verificarEstructura(
            ListaDobleSinCola<T> lista,
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
                contexto + ": frente incorrecto."
        );

        verificar(
                lista.topBack().equals(
                        esperados[esperados.length - 1]
                ),
                contexto + ": último incorrecto."
        );

        /*
         * find(topFront) necesariamente retorna head,
         * ya que head es la primera aparición de su propio valor.
         */
        NodoDoble<T> actual =
                lista.find(esperados[0]);

        verificar(
                actual != null,
                contexto + ": no se pudo localizar head."
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
                            + ": dato incorrecto en posición "
                            + i
                            + "."
            );

            if (i == 0) {
                verificar(
                        actual.getPrev() == null,
                        contexto + ": el primer nodo no debe tener prev."
                );
            } else {
                verificar(
                        actual.getPrev() != null,
                        contexto + ": falta enlace prev."
                );

                verificar(
                        actual.getPrev().getDato()
                                .equals(esperados[i - 1]),
                        contexto + ": prev apunta al dato incorrecto."
                );

                verificar(
                        actual.getPrev().getNext() == actual,
                        contexto
                                + ": prev.next debe regresar al nodo actual."
                );
            }

            if (actual.getNext() != null) {
                verificar(
                        actual.getNext().getPrev() == actual,
                        contexto
                                + ": next.prev debe regresar al nodo actual."
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
                ultimo != null && ultimo.getNext() == null,
                contexto + ": el último nodo debe tener next null."
        );

        /*
         * Recorrido inverso para comprobar prev.
         */
        actual = ultimo;

        for (int i = esperados.length - 1; i >= 0; i--) {

            verificar(
                    actual != null,
                    contexto + ": faltan nodos al recorrer hacia atrás."
            );

            verificar(
                    actual.getDato().equals(esperados[i]),
                    contexto
                            + ": dato incorrecto en recorrido inverso."
            );

            if (actual.getPrev() != null) {
                verificar(
                        actual.getPrev().getNext() == actual,
                        contexto
                                + ": inconsistencia en el recorrido inverso."
                );
            }

            actual = actual.getPrev();
        }

        verificar(
                actual == null,
                contexto
                        + ": el recorrido inverso debe terminar antes de head."
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