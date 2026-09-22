package estructuras.queue;

import estructuras.listas.ListaSimpleConCola;
import estructuras.listas.NodoSimple;

/**
 * Implementación de una cola mediante una lista
 * simplemente enlazada con referencia al último nodo.
 *
 * @param <T> tipo de dato almacenado
 */
public class ColaEnlazada<T> implements MyQueue<T> {

    private final ListaSimpleConCola<T> lista;

    public ColaEnlazada() {
        this.lista = new ListaSimpleConCola<>();
    }

    @Override
    public void enqueue(T x) {
        lista.pushBack(x);
    }

    @Override
    public T dequeue() {
        return lista.popFront();
    }

    @Override
    public T front() {
        return lista.topFront();
    }

    @Override
    public boolean isEmpty() {
        return lista.isEmpty();
    }

    @Override
    public int size() {
        return lista.size();
    }

    @Override
    public boolean delete(T valor) {
        NodoSimple<T> nodo = lista.find(valor);

        if (nodo == null) {
            return false;
        }

        lista.erase(nodo);

        return true;
    }
}