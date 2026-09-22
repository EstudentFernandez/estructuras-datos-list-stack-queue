package estructuras.stack;

/**
 * Implementación de una pila mediante un arreglo dinámico propio.
 *
 * El arreglo duplica su capacidad cuando se llena.
 * No se reduce automáticamente al eliminar elementos.
 *
 * @param <T> tipo de dato almacenado
 */
public class PilaArreglo<T> implements MyStack<T> {

    private static final int CAPACIDAD_INICIAL = 4;

    private Object[] elementos;
    private int size;

    /**
     * Construye una pila con capacidad inicial predeterminada.
     */
    public PilaArreglo() {
        this(CAPACIDAD_INICIAL);
    }

    /**
     * Construye una pila con una capacidad inicial específica.
     */
    public PilaArreglo(int capacidadInicial) {
        if (capacidadInicial <= 0) {
            throw new IllegalArgumentException(
                    "La capacidad inicial debe ser positiva."
            );
        }

        this.elementos = new Object[capacidadInicial];
        this.size = 0;
    }

    /**
     * Inserta un elemento en la cima.
     */
    @Override
    public void push(T x) {
        validarDato(x);
        asegurarCapacidad();

        elementos[size] = x;
        size++;
    }

    /**
     * Elimina y retorna el elemento de la cima.
     */
    @Override
    public T pop() {
        verificarNoVacia();

        int indiceCima = size - 1;
        T valor = elementoEn(indiceCima);

        elementos[indiceCima] = null;
        size--;

        return valor;
    }

    /**
     * Retorna el elemento de la cima sin eliminarlo.
     */
    @Override
    public T peek() {
        verificarNoVacia();

        return elementoEn(size - 1);
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
     * recorriendo desde la cima hacia la base.
     */
    @Override
    public boolean delete(T valor) {
        validarDato(valor);

        for (int i = size - 1; i >= 0; i--) {

            if (elementoEn(i).equals(valor)) {

                /*
                 * Desplaza hacia la izquierda todos los
                 * elementos que estaban por encima.
                 */
                for (int j = i; j < size - 1; j++) {
                    elementos[j] = elementos[j + 1];
                }

                elementos[size - 1] = null;
                size--;

                return true;
            }
        }

        return false;
    }

    /**
     * Retorna la capacidad física actual.
     *
     * Es un método auxiliar útil para pruebas y benchmarks.
     */
    public int capacity() {
        return elementos.length;
    }

    /**
     * Duplica la capacidad si el arreglo está lleno.
     */
    private void asegurarCapacidad() {
        if (size < elementos.length) {
            return;
        }

        int nuevaCapacidad = elementos.length * 2;

        Object[] nuevoArreglo =
                new Object[nuevaCapacidad];

        for (int i = 0; i < size; i++) {
            nuevoArreglo[i] = elementos[i];
        }

        elementos = nuevoArreglo;
    }

    /**
     * Obtiene un elemento almacenado en el arreglo.
     */
    @SuppressWarnings("unchecked")
    private T elementoEn(int indice) {
        return (T) elementos[indice];
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
                    "La pila está vacía."
            );
        }
    }
}