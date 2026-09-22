package estructuras.queue;

/**
 * Contrato básico para una estructura Queue (cola).
 *
 * @param <T> tipo de dato almacenado
 */
public interface MyQueue<T> {

    /**
     * Inserta un elemento al final de la cola.
     */
    void enqueue(T x);

    /**
     * Elimina y retorna el primer elemento.
     */
    T dequeue();

    /**
     * Retorna el primer elemento sin eliminarlo.
     */
    T front();

    /**
     * Indica si la cola está vacía.
     */
    boolean isEmpty();

    /**
     * Retorna el número de elementos almacenados.
     */
    int size();

    /**
     * Elimina la primera coincidencia encontrada
     * recorriendo desde el frente.
     *
     * @return true si se eliminó un elemento;
     *         false si no estaba presente
     */
    boolean delete(T valor);
}