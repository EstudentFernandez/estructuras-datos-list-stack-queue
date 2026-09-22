package estructuras.queue;

/**
 * Implementación de una cola mediante un arreglo
 * circular dinámico propio.
 *
 * El arreglo duplica su capacidad cuando se llena
 * y no reduce automáticamente su capacidad.
 *
 * @param <T> tipo de dato almacenado
 */
public class ColaArreglo<T> implements MyQueue<T> {

    private static final int CAPACIDAD_INICIAL = 4;

    private Object[] elementos;

    /**
     * Índice físico donde se encuentra el
     * primer elemento lógico de la cola.
     */
    private int head;

    /**
     * Número actual de elementos almacenados.
     */
    private int size;

    /**
     * Construye una cola con capacidad inicial predeterminada.
     */
    public ColaArreglo() {
        this(CAPACIDAD_INICIAL);
    }

    /**
     * Construye una cola con capacidad inicial específica.
     */
    public ColaArreglo(int capacidadInicial) {
        if (capacidadInicial <= 0) {
            throw new IllegalArgumentException(
                    "La capacidad inicial debe ser positiva."
            );
        }

        this.elementos = new Object[capacidadInicial];
        this.head = 0;
        this.size = 0;
    }

    /**
     * Inserta un elemento al final de la cola.
     */
    @Override
    public void enqueue(T x) {
        validarDato(x);
        asegurarCapacidad();

        int indiceFinal = indiceFisico(size);

        elementos[indiceFinal] = x;
        size++;
    }

    /**
     * Elimina y retorna el primer elemento.
     */
    @Override
    public T dequeue() {
        verificarNoVacia();

        T valor = elementoEn(head);

        elementos[head] = null;

        head = (head + 1) % elementos.length;
        size--;

        /*
         * Cuando la cola queda vacía normalizamos head a cero.
         * No es estrictamente necesario para la complejidad,
         * pero simplifica el estado vacío.
         */
        if (size == 0) {
            head = 0;
        }

        return valor;
    }

    /**
     * Retorna el primer elemento sin eliminarlo.
     */
    @Override
    public T front() {
        verificarNoVacia();

        return elementoEn(head);
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public int size() {
        return size;
    }

    /**
     * Elimina la primera coincidencia encontrada
     * recorriendo desde el frente hacia el final.
     *
     * El orden relativo de los demás elementos
     * se conserva.
     */
    @Override
    public boolean delete(T valor) {
        validarDato(valor);

        for (int i = 0; i < size; i++) {

            int indiceActual = indiceFisico(i);

            if (elementoEn(indiceActual).equals(valor)) {

                /*
                 * Desplazamos hacia el frente únicamente
                 * los elementos posteriores al eliminado.
                 *
                 * El cálculo circular funciona aunque
                 * la cola atraviese el final del arreglo.
                 */
                for (int j = i; j < size - 1; j++) {
                    int destino = indiceFisico(j);
                    int origen = indiceFisico(j + 1);

                    elementos[destino] = elementos[origen];
                }

                int ultimoIndice = indiceFisico(size - 1);

                elementos[ultimoIndice] = null;
                size--;

                if (size == 0) {
                    head = 0;
                }

                return true;
            }
        }

        return false;
    }

    /**
     * Retorna la capacidad física actual.
     *
     * Método auxiliar útil para pruebas
     * y análisis experimental.
     */
    public int capacity() {
        return elementos.length;
    }

    /**
     * Convierte una posición lógica de la cola
     * en una posición física del arreglo.
     *
     * posición física =
     * (head + posición lógica) mod capacidad
     */
    private int indiceFisico(int indiceLogico) {
        return (head + indiceLogico) % elementos.length;
    }

    /**
     * Duplica la capacidad cuando el arreglo está lleno.
     *
     * Los elementos se copian en su orden lógico:
     * frente -> final.
     */
    private void asegurarCapacidad() {
        if (size < elementos.length) {
            return;
        }

        int nuevaCapacidad = elementos.length * 2;

        Object[] nuevoArreglo =
                new Object[nuevaCapacidad];

        for (int i = 0; i < size; i++) {
            nuevoArreglo[i] =
                    elementos[indiceFisico(i)];
        }

        elementos = nuevoArreglo;
        head = 0;
    }

    @SuppressWarnings("unchecked")
    private T elementoEn(int indiceFisico) {
        return (T) elementos[indiceFisico];
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
                    "La cola está vacía."
            );
        }
    }
}