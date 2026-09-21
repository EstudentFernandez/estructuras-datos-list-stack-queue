package estructuras.listas;

/**
 * Lista simplemente enlazada sin referencia directa al último nodo.
 *
 * @param <T> tipo de dato almacenado en la lista
 */
public class ListaSimpleSinCola<T> {

    private NodoSimple<T> head;
    private int size;

    public ListaSimpleSinCola() {
        this.head = null;
        this.size = 0;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public void pushFront(T valor) {
        validarDato(valor);

        NodoSimple<T> nuevo = new NodoSimple<>(valor);

        nuevo.next = head;
        nuevo.owner = this;
        nuevo.active = true;

        head = nuevo;
        size++;
    }

    public T topFront() {
        verificarNoVacia();

        return head.getDato();
    }

    public T popFront() {
        verificarNoVacia();

        NodoSimple<T> eliminado = head;
        T valor = eliminado.getDato();

        head = eliminado.next;

        eliminado.next = null;
        eliminado.owner = null;
        eliminado.active = false;

        size--;

        return valor;
    }

    private void validarDato(T valor) {
        if (valor == null) {
            throw new IllegalArgumentException(
                    "No se permiten valores null."
            );
        }
    }

    private void verificarNoVacia() {
        if (isEmpty()) {
            throw new IllegalStateException(
                    "La lista está vacía."
            );
        }
    }
}