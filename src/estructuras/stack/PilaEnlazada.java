package estructuras.stack;

import estructuras.listas.ListaSimpleSinCola;
import estructuras.listas.NodoSimple;

/**
 * Implementación de una pila mediante una lista
 * simplemente enlazada.
 *
 * La cima corresponde al inicio de la lista.
 *
 * @param <T> tipo de dato almacenado
 */
public class PilaEnlazada<T> implements MyStack<T> {

    private final ListaSimpleSinCola<T> lista;

    public PilaEnlazada() {
        this.lista = new ListaSimpleSinCola<>();
    }

    @Override
    public void push(T x) {
        lista.pushFront(x);
    }

    @Override
    public T pop() {
        return lista.popFront();
    }

    @Override
    public T peek() {
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