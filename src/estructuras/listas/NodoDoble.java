package estructuras.listas;

/**
 * Representa un nodo de una lista doblemente enlazada.
 *
 * @param <T> tipo de dato almacenado en el nodo
 */
public class NodoDoble<T> {

    private final T dato;

    NodoDoble<T> prev;
    NodoDoble<T> next;

    Object owner;
    boolean active;

    NodoDoble(T dato) {
        if (dato == null) {
            throw new IllegalArgumentException(
                    "El dato del nodo no puede ser null."
            );
        }

        this.dato = dato;
        this.prev = null;
        this.next = null;
        this.owner = null;
        this.active = false;
    }

    public T getDato() {
        return dato;
    }

    public NodoDoble<T> getPrev() {
        return prev;
    }

    public NodoDoble<T> getNext() {
        return next;
    }
}