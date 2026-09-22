package estructuras.pruebas;

import estructuras.listas.ListaSimpleSinCola;
import estructuras.listas.NodoSimple;

public class PruebaListaSimpleSinCola {

    private static int comprobaciones = 0;

    public static void main(String[] args) {

        probarEstadoInicial();
        probarPushFront();
        probarPushBack();
        probarInsercionesCombinadas();

        probarTopFrontYTopBack();
        probarPopFront();
        probarPopBack();

        probarFind();
        probarFindConDuplicados();

        probarEraseInicio();
        probarEraseMedio();
        probarEraseFinal();
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
                "ListaSimpleSinCola: todas las pruebas fueron superadas."
        );

        System.out.println(
                "Comprobaciones realizadas: " + comprobaciones
        );
    }

    private static void probarEstadoInicial() {
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

    private static void probarPushFront() {
        ListaSimpleSinCola<Integer> lista =
                new ListaSimpleSinCola<>();

        lista.pushFront(10);
        lista.pushFront(20);
        lista.pushFront(30);

        verificar(
                lista.size() == 3,
                "pushFront debe actualizar correctamente el tamaño."
        );

        verificarSecuencia(
                lista,
                new Integer[]{30, 20, 10},
                "pushFront"
        );
    }

    private static void probarPushBack() {
        ListaSimpleSinCola<Integer> lista =
                new ListaSimpleSinCola<>();

        lista.pushBack(10);
        lista.pushBack(20);
        lista.pushBack(30);

        verificar(
                lista.size() == 3,
                "pushBack debe actualizar correctamente el tamaño."
        );

        verificarSecuencia(
                lista,
                new Integer[]{10, 20, 30},
                "pushBack"
        );
    }

    private static void probarInsercionesCombinadas() {
        ListaSimpleSinCola<Integer> lista =
                new ListaSimpleSinCola<>();

        lista.pushBack(20);
        lista.pushFront(10);
        lista.pushBack(30);
        lista.pushFront(5);

        verificarSecuencia(
                lista,
                new Integer[]{5, 10, 20, 30},
                "inserciones combinadas"
        );
    }

    private static void probarTopFrontYTopBack() {
        ListaSimpleSinCola<Integer> lista =
                new ListaSimpleSinCola<>();

        lista.pushBack(10);
        lista.pushBack(20);
        lista.pushBack(30);

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
                "Las consultas top no deben cambiar el tamaño."
        );

        verificarSecuencia(
                lista,
                new Integer[]{10, 20, 30},
                "topFront/topBack"
        );
    }

    private static void probarPopFront() {
        ListaSimpleSinCola<Integer> lista =
                new ListaSimpleSinCola<>();

        lista.pushBack(10);
        lista.pushBack(20);
        lista.pushBack(30);

        verificar(
                lista.popFront().equals(10),
                "popFront debe eliminar 10."
        );

        verificar(
                lista.size() == 2,
                "popFront debe reducir el tamaño."
        );

        verificarSecuencia(
                lista,
                new Integer[]{20, 30},
                "popFront"
        );
    }

    private static void probarPopBack() {
        ListaSimpleSinCola<Integer> lista =
                new ListaSimpleSinCola<>();

        lista.pushBack(10);
        lista.pushBack(20);
        lista.pushBack(30);

        verificar(
                lista.popBack().equals(30),
                "popBack debe eliminar 30."
        );

        verificar(
                lista.size() == 2,
                "popBack debe reducir el tamaño."
        );

        verificar(
                lista.topBack().equals(20),
                "Después de eliminar 30, el último debe ser 20."
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

    private static void probarFind() {
        ListaSimpleSinCola<Integer> lista =
                new ListaSimpleSinCola<>();

        lista.pushBack(10);
        lista.pushBack(20);
        lista.pushBack(30);

        NodoSimple<Integer> encontrado =
                lista.find(20);

        verificar(
                encontrado != null,
                "find debe encontrar un valor existente."
        );

        verificar(
                encontrado.getDato().equals(20),
                "find debe retornar el nodo correcto."
        );

        verificar(
                lista.find(999) == null,
                "find debe retornar null para un valor ausente."
        );
    }

    private static void probarFindConDuplicados() {
        ListaSimpleSinCola<Integer> lista =
                new ListaSimpleSinCola<>();

        lista.pushBack(10);
        lista.pushBack(20);
        lista.pushBack(20);
        lista.pushBack(30);

        NodoSimple<Integer> primero =
                lista.find(20);

        verificar(
                primero != null,
                "find debe encontrar el primer 20."
        );

        verificar(
                primero.getNext() != null
                        && primero.getNext().getDato().equals(20),
                "Debe existir un segundo 20 después del primero."
        );

        NodoSimple<Integer> segundo =
                primero.getNext();

        verificar(
                primero != segundo,
                "Los dos valores duplicados deben estar en nodos distintos."
        );
    }

    private static void probarEraseInicio() {
        ListaSimpleSinCola<Integer> lista =
                crearLista(10, 20, 30);

        NodoSimple<Integer> nodo =
                lista.find(10);

        lista.erase(nodo);

        verificarSecuencia(
                lista,
                new Integer[]{20, 30},
                "erase al inicio"
        );
    }

    private static void probarEraseMedio() {
        ListaSimpleSinCola<Integer> lista =
                crearLista(10, 20, 30);

        NodoSimple<Integer> nodo =
                lista.find(20);

        lista.erase(nodo);

        verificarSecuencia(
                lista,
                new Integer[]{10, 30},
                "erase en medio"
        );
    }

    private static void probarEraseFinal() {
        ListaSimpleSinCola<Integer> lista =
                crearLista(10, 20, 30);

        NodoSimple<Integer> nodo =
                lista.find(30);

        lista.erase(nodo);

        verificarSecuencia(
                lista,
                new Integer[]{10, 20},
                "erase al final"
        );
    }

    private static void probarErasePorIdentidad() {
        ListaSimpleSinCola<Integer> lista =
                crearLista(10, 20, 20, 30);

        NodoSimple<Integer> primerVeinte =
                lista.find(20);

        NodoSimple<Integer> segundoVeinte =
                primerVeinte.getNext();

        verificar(
                segundoVeinte != null
                        && segundoVeinte.getDato().equals(20),
                "Debe localizarse el segundo nodo con valor 20."
        );

        lista.erase(segundoVeinte);

        verificarSecuencia(
                lista,
                new Integer[]{10, 20, 30},
                "erase debe respetar la identidad del nodo"
        );
    }

    private static void probarAddBeforeInicio() {
        ListaSimpleSinCola<Integer> lista =
                crearLista(20, 30);

        NodoSimple<Integer> nodo =
                lista.find(20);

        lista.addBefore(nodo, 10);

        verificarSecuencia(
                lista,
                new Integer[]{10, 20, 30},
                "addBefore al inicio"
        );
    }

    private static void probarAddBeforeMedio() {
        ListaSimpleSinCola<Integer> lista =
                crearLista(10, 30);

        NodoSimple<Integer> nodo =
                lista.find(30);

        lista.addBefore(nodo, 20);

        verificarSecuencia(
                lista,
                new Integer[]{10, 20, 30},
                "addBefore en medio"
        );
    }

    private static void probarAddAfterMedio() {
        ListaSimpleSinCola<Integer> lista =
                crearLista(10, 30);

        NodoSimple<Integer> nodo =
                lista.find(10);

        lista.addAfter(nodo, 20);

        verificarSecuencia(
                lista,
                new Integer[]{10, 20, 30},
                "addAfter en medio"
        );
    }

    private static void probarAddAfterFinal() {
        ListaSimpleSinCola<Integer> lista =
                crearLista(10, 20);

        NodoSimple<Integer> nodo =
                lista.find(20);

        lista.addAfter(nodo, 30);

        verificarSecuencia(
                lista,
                new Integer[]{10, 20, 30},
                "addAfter al final"
        );
    }

    private static void probarReferenciasAjenas() {
        ListaSimpleSinCola<Integer> listaA =
                crearLista(10, 20);

        ListaSimpleSinCola<Integer> listaB =
                crearLista(30, 40);

        NodoSimple<Integer> nodoAjeno =
                listaA.find(10);

        esperarIllegalArgument(
                () -> listaB.erase(nodoAjeno),
                "erase debe rechazar un nodo de otra lista."
        );

        esperarIllegalArgument(
                () -> listaB.addBefore(nodoAjeno, 25),
                "addBefore debe rechazar un nodo de otra lista."
        );

        esperarIllegalArgument(
                () -> listaB.addAfter(nodoAjeno, 50),
                "addAfter debe rechazar un nodo de otra lista."
        );

        verificar(
                listaB.size() == 2,
                "Las operaciones inválidas no deben modificar listaB."
        );
    }

    private static void probarReferenciasEliminadas() {
        ListaSimpleSinCola<Integer> lista =
                crearLista(10, 20, 30);

        NodoSimple<Integer> eliminado =
                lista.find(20);

        lista.erase(eliminado);

        esperarIllegalArgument(
                () -> lista.erase(eliminado),
                "No debe permitirse eliminar dos veces el mismo nodo."
        );

        esperarIllegalArgument(
                () -> lista.addBefore(eliminado, 15),
                "addBefore debe rechazar nodos eliminados."
        );

        esperarIllegalArgument(
                () -> lista.addAfter(eliminado, 25),
                "addAfter debe rechazar nodos eliminados."
        );

        verificarSecuencia(
                lista,
                new Integer[]{10, 30},
                "referencia eliminada"
        );
    }

    private static void probarOperacionesSobreVacia() {
        ListaSimpleSinCola<Integer> lista =
                new ListaSimpleSinCola<>();

        esperarIllegalState(
                lista::popFront,
                "popFront sobre vacío debe fallar."
        );

        esperarIllegalState(
                lista::popBack,
                "popBack sobre vacío debe fallar."
        );

        esperarIllegalState(
                lista::topFront,
                "topFront sobre vacío debe fallar."
        );

        esperarIllegalState(
                lista::topBack,
                "topBack sobre vacío debe fallar."
        );
    }

    private static void probarValoresNull() {
        ListaSimpleSinCola<Integer> lista =
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

        NodoSimple<Integer> nodo =
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

        verificarSecuencia(
                lista,
                new Integer[]{10, 20},
                "rechazo de null"
        );
    }

    @SafeVarargs
    private static <T> ListaSimpleSinCola<T> crearLista(
            T... valores) {

        ListaSimpleSinCola<T> lista =
                new ListaSimpleSinCola<>();

        for (T valor : valores) {
            lista.pushBack(valor);
        }

        return lista;
    }

    private static <T> void verificarSecuencia(
            ListaSimpleSinCola<T> lista,
            T[] esperados,
            String contexto) {

        verificar(
                lista.size() == esperados.length,
                contexto + ": tamaño incorrecto."
        );

        for (T esperado : esperados) {
            T obtenido = lista.popFront();

            verificar(
                    obtenido.equals(esperado),
                    contexto
                            + ": se esperaba "
                            + esperado
                            + " pero se obtuvo "
                            + obtenido
                            + "."
            );
        }

        verificar(
                lista.isEmpty(),
                contexto + ": la lista debe quedar vacía."
        );

        verificar(
                lista.size() == 0,
                contexto + ": el tamaño final debe ser 0."
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