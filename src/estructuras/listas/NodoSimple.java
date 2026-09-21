package estructuras.listas;

/**
 * Representa un nodo de una lista simplemente enlazada.
 *
 * @param <T> tipo de dato almacenado en el nodo
 */
public class NodoSimple<T> {

    private final T dato;

    NodoSimple<T> next;

    Object owner;
    boolean active;

    NodoSimple(T dato) {
        if (dato == null) {
            throw new IllegalArgumentException(
                    "El dato del nodo no puede ser null."
            );
        }

        this.dato = dato;
        this.next = null;
        this.owner = null;
        this.active = false;
    }

    public T getDato() {
        return dato;
    }

    public NodoSimple<T> getNext() {
        return next;
    }
}