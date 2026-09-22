package estructuras.stack;

/**
 * Contrato básico para una estructura de datos Stack (pila).
 *
 * @param <T> tipo de dato almacenado
 */
public interface MyStack<T> {

    /**
     * Inserta un elemento en la cima.
     */
    void push(T x);

    /**
     * Elimina y retorna el elemento en la cima.
     */
    T pop();

    /**
     * Retorna el elemento en la cima sin eliminarlo.
     */
    T peek();

    /**
     * Indica si la pila está vacía.
     */
    boolean isEmpty();

    /**
     * Retorna el número de elementos almacenados.
     */
    int size();

    /**
     * Elimina la primera aparición del valor
     * encontrada desde la cima.
     *
     * @return true si el elemento fue eliminado;
     *         false si no estaba presente
     */
    boolean delete(T valor);
}