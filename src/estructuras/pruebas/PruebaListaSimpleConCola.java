package estructuras.pruebas;

import estructuras.listas.ListaSimpleConCola;
import estructuras.listas.NodoSimple;

public class PruebaListaSimpleConCola {

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
                "ListaSimpleConCola: todas las pruebas fueron superadas."
        );

        System.out.println(
                "Comprobaciones realizadas: " + comprobaciones
        );
    }

    private static void probarEstadoInicial() {
        ListaSimpleConCola<Integer> lista =
                new ListaSimpleConCola<>();

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
        ListaSimpleConCola<Integer> lista =
                new ListaSimpleConCola<>();

        lista.pushFront(30);
        lista.pushFront(20);
        lista.pushFront(10);

        verificar(
                lista.topFront().equals(10),
                "pushFront debe modificar el frente."
        );

        verificar(
                lista.topBack().equals(30),
                "pushFront no debe perder la referencia al último."
        );

        verificarSecuencia(
                lista,
                new Integer[]{10, 20, 30},
                "pushFront"
        );
    }

    private static void probarPushBack() {
        ListaSimpleConCola<Integer> lista =
                new ListaSimpleConCola<>();

        lista.pushBack(10);
        lista.pushBack(20);
        lista.pushBack(30);

        verificar(
                lista.topFront().equals(10),
                "pushBack debe conservar el frente."
        );

        verificar(
                lista.topBack().equals(30),
                "tail debe apuntar al último elemento."
        );

        verificarSecuencia(
                lista,
                new Integer[]{10, 20, 30},
                "pushBack"
        );
    }

    private static void probarInsercionesCombinadas() {
        ListaSimpleConCola<Integer> lista =
                new ListaSimpleConCola<>();

        lista.pushBack(20);
        lista.pushFront(10);
        lista.pushBack(30);
        lista.pushFront(5);
        lista.pushBack(40);

        verificar(
                lista.topFront().equals(5),
                "El frente debe ser 5."
        );

        verificar(
                lista.topBack().equals(40),
                "La cola debe ser 40."
        );

        verificarSecuencia(
                lista,
                new Integer[]{5, 10, 20, 30, 40},
                "inserciones combinadas"
        );
    }

    private static void probarTopFrontYTopBack() {
        ListaSimpleConCola<Integer> lista =
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

        verificarSecuencia(
                lista,
                new Integer[]{10, 20, 30},
                "topFront/topBack"
        );
    }

    private static void probarPopFront() {
        ListaSimpleConCola<Integer> lista =
                crearLista(10, 20, 30);

        verificar(
                lista.popFront().equals(10),
                "popFront debe eliminar 10."
        );

        verificar(
                lista.topFront().equals(20),
                "El nuevo frente debe ser 20."
        );

        verificar(
                lista.topBack().equals(30),
                "popFront no debe alterar tail mientras queden elementos."
        );

        verificar(
                lista.size() == 2,
                "size debe ser 2."
        );
    }

    private static void probarPopBack() {
        ListaSimpleConCola<Integer> lista =
                crearLista(10, 20, 30);

        verificar(
                lista.popBack().equals(30),
                "popBack debe eliminar 30."
        );

        verificar(
                lista.topBack().equals(20),
                "tail debe actualizarse a 20."
        );

        verificar(
                lista.popBack().equals(20),
                "El siguiente popBack debe eliminar 20."
        );

        verificar(
                lista.topBack().equals(10),
                "tail debe actualizarse a 10."
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
        ListaSimpleConCola<Integer> lista =
                new ListaSimpleConCola<>();

        lista.pushBack(10);

        verificar(
                lista.topFront().equals(10),
                "Con un elemento, head debe contener 10."
        );

        verificar(
                lista.topBack().equals(10),
                "Con un elemento, tail debe contener 10."
        );

        verificar(
                lista.popFront().equals(10),
                "Debe poder eliminarse el único elemento."
        );

        verificar(
                lista.isEmpty(),
                "Debe quedar vacía."
        );

        lista.pushFront(20);

        verificar(
                lista.topFront().equals(20),
                "Debe poder reutilizarse después de vaciarla."
        );

        verificar(
                lista.topBack().equals(20),
                "tail debe reconstruirse correctamente."
        );

        verificar(
                lista.popBack().equals(20),
                "popBack debe eliminar el único nodo."
        );

        verificar(
                lista.isEmpty(),
                "Debe quedar vacía nuevamente."
        );

        lista.pushBack(30);
        lista.pushBack(40);

        verificarSecuencia(
                lista,
                new Integer[]{30, 40},
                "reinserción después de vacío"
        );
    }

    private static void probarFind() {
        ListaSimpleConCola<Integer> lista =
                crearLista(10, 20, 30);

        NodoSimple<Integer> nodo =
                lista.find(20);

        verificar(
                nodo != null,
                "find debe localizar valores existentes."
        );

        verificar(
                nodo.getDato().equals(20),
                "find debe retornar el nodo correcto."
        );

        verificar(
                lista.find(999) == null,
                "find debe retornar null si no existe."
        );
    }

    private static void probarFindConDuplicados() {
        ListaSimpleConCola<Integer> lista =
                crearLista(10, 20, 20, 30);

        NodoSimple<Integer> primero =
                lista.find(20);

        verificar(
                primero != null,
                "Debe encontrarse el primer 20."
        );

        NodoSimple<Integer> segundo =
                primero.getNext();

        verificar(
                segundo != null
                        && segundo.getDato().equals(20),
                "Debe existir un segundo nodo con valor 20."
        );

        verificar(
                primero != segundo,
                "Los duplicados deben conservar identidades distintas."
        );
    }

    private static void probarEraseInicio() {
        ListaSimpleConCola<Integer> lista =
                crearLista(10, 20, 30);

        lista.erase(lista.find(10));

        verificar(
                lista.topFront().equals(20),
                "erase de head debe actualizar el frente."
        );

        verificar(
                lista.topBack().equals(30),
                "erase de head no debe alterar tail."
        );

        verificarSecuencia(
                lista,
                new Integer[]{20, 30},
                "erase inicio"
        );
    }

    private static void probarEraseMedio() {
        ListaSimpleConCola<Integer> lista =
                crearLista(10, 20, 30);

        lista.erase(lista.find(20));

        verificar(
                lista.topBack().equals(30),
                "erase en medio debe conservar tail."
        );

        verificarSecuencia(
                lista,
                new Integer[]{10, 30},
                "erase medio"
        );
    }

    private static void probarEraseFinal() {
        ListaSimpleConCola<Integer> lista =
                crearLista(10, 20, 30);

        lista.erase(lista.find(30));

        verificar(
                lista.topBack().equals(20),
                "erase del último nodo debe actualizar tail."
        );

        verificarSecuencia(
                lista,
                new Integer[]{10, 20},
                "erase final"
        );
    }

    private static void probarErasePorIdentidad() {
        ListaSimpleConCola<Integer> lista =
                crearLista(10, 20, 20, 30);

        NodoSimple<Integer> primerVeinte =
                lista.find(20);

        NodoSimple<Integer> segundoVeinte =
                primerVeinte.getNext();

        lista.erase(segundoVeinte);

        verificar(
                lista.find(20) == primerVeinte,
                "erase debe conservar el primer nodo duplicado."
        );

        verificarSecuencia(
                lista,
                new Integer[]{10, 20, 30},
                "erase por identidad"
        );
    }

    private static void probarAddBeforeInicio() {
        ListaSimpleConCola<Integer> lista =
                crearLista(20, 30);

        lista.addBefore(
                lista.find(20),
                10
        );

        verificar(
                lista.topFront().equals(10),
                "addBefore sobre head debe actualizar el frente."
        );

        verificar(
                lista.topBack().equals(30),
                "addBefore sobre head debe conservar tail."
        );

        verificarSecuencia(
                lista,
                new Integer[]{10, 20, 30},
                "addBefore inicio"
        );
    }

    private static void probarAddBeforeMedio() {
        ListaSimpleConCola<Integer> lista =
                crearLista(10, 30);

        lista.addBefore(
                lista.find(30),
                20
        );

        verificar(
                lista.topBack().equals(30),
                "addBefore antes de tail debe conservar tail."
        );

        verificarSecuencia(
                lista,
                new Integer[]{10, 20, 30},
                "addBefore medio"
        );
    }

    private static void probarAddAfterMedio() {
        ListaSimpleConCola<Integer> lista =
                crearLista(10, 30);

        lista.addAfter(
                lista.find(10),
                20
        );

        verificar(
                lista.topBack().equals(30),
                "addAfter en medio debe conservar tail."
        );

        verificarSecuencia(
                lista,
                new Integer[]{10, 20, 30},
                "addAfter medio"
        );
    }

    private static void probarAddAfterFinal() {
        ListaSimpleConCola<Integer> lista =
                crearLista(10, 20);

        lista.addAfter(
                lista.find(20),
                30
        );

        verificar(
                lista.topBack().equals(30),
                "addAfter sobre tail debe actualizar tail."
        );

        verificarSecuencia(
                lista,
                new Integer[]{10, 20, 30},
                "addAfter final"
        );
    }

    private static void probarReferenciasAjenas() {
        ListaSimpleConCola<Integer> listaA =
                crearLista(10, 20);

        ListaSimpleConCola<Integer> listaB =
                crearLista(30, 40);

        NodoSimple<Integer> ajeno =
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

        verificar(
                listaB.topFront().equals(30)
                        && listaB.topBack().equals(40)
                        && listaB.size() == 2,
                "Las operaciones inválidas no deben modificar listaB."
        );
    }

    private static void probarReferenciasEliminadas() {
        ListaSimpleConCola<Integer> lista =
                crearLista(10, 20, 30);

        NodoSimple<Integer> eliminado =
                lista.find(20);

        lista.erase(eliminado);

        esperarIllegalArgument(
                () -> lista.erase(eliminado),
                "No se puede borrar dos veces el mismo nodo."
        );

        esperarIllegalArgument(
                () -> lista.addBefore(eliminado, 15),
                "addBefore debe rechazar un nodo eliminado."
        );

        esperarIllegalArgument(
                () -> lista.addAfter(eliminado, 25),
                "addAfter debe rechazar un nodo eliminado."
        );

        verificar(
                lista.topBack().equals(30),
                "Una referencia inválida no debe corromper tail."
        );

        verificarSecuencia(
                lista,
                new Integer[]{10, 30},
                "referencias eliminadas"
        );
    }

    private static void probarOperacionesSobreVacia() {
        ListaSimpleConCola<Integer> lista =
                new ListaSimpleConCola<>();

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
        ListaSimpleConCola<Integer> lista =
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

        verificar(
                lista.topFront().equals(10)
                        && lista.topBack().equals(20)
                        && lista.size() == 2,
                "Las operaciones inválidas no deben modificar la lista."
        );
    }

    @SafeVarargs
    private static <T> ListaSimpleConCola<T> crearLista(
            T... valores) {

        ListaSimpleConCola<T> lista =
                new ListaSimpleConCola<>();

        for (T valor : valores) {
            lista.pushBack(valor);
        }

        return lista;
    }

    private static <T> void verificarSecuencia(
            ListaSimpleConCola<T> lista,
            T[] esperados,
            String contexto) {

        verificar(
                lista.size() == esperados.length,
                contexto + ": tamaño incorrecto."
        );

        if (esperados.length > 0) {
            verificar(
                    lista.topFront().equals(esperados[0]),
                    contexto + ": frente incorrecto."
            );

            verificar(
                    lista.topBack().equals(
                            esperados[esperados.length - 1]
                    ),
                    contexto + ": cola incorrecta."
            );
        }

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
                contexto + ": debe terminar vacía."
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