package estructuras.listas;

/**
 * Lista simplemente enlazada con referencia directa al último nodo.
 *
 * @param <T> tipo de dato almacenado en la lista
 */
public class ListaSimpleConCola<T> {

    private NodoSimple<T> head;
    private NodoSimple<T> tail;
    private int size;

    /**
     * Construye una lista vacía.
     */
    public ListaSimpleConCola() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    /**
     * Indica si la lista está vacía.
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Retorna el número de elementos almacenados.
     */
    public int size() {
        return size;
    }

    /**
     * Inserta un elemento al inicio.
     */
    public void pushFront(T valor) {
        validarDato(valor);

        NodoSimple<T> nuevo = new NodoSimple<>(valor);

        nuevo.next = head;
        nuevo.owner = this;
        nuevo.active = true;

        head = nuevo;

        // Si antes estaba vacía, el nuevo nodo
        // es simultáneamente head y tail.
        if (size == 0) {
            tail = nuevo;
        }

        size++;
    }

    /**
     * Inserta un elemento al final.
     */
    public void pushBack(T valor) {
        validarDato(valor);

        NodoSimple<T> nuevo = new NodoSimple<>(valor);

        nuevo.owner = this;
        nuevo.active = true;

        if (isEmpty()) {
            head = nuevo;
            tail = nuevo;
        } else {
            tail.next = nuevo;
            tail = nuevo;
        }

        size++;
    }

    /**
     * Elimina y retorna el primer elemento.
     */
    public T popFront() {
        verificarNoVacia();

        NodoSimple<T> eliminado = head;
        T valor = eliminado.getDato();

        head = eliminado.next;

        size--;

        // Si acabamos de eliminar el único nodo,
        // head ya es null y tail también debe serlo.
        if (size == 0) {
            tail = null;
        }

        desactivarNodo(eliminado);

        return valor;
    }

    /**
     * Elimina y retorna el último elemento.
     *
     * Aunque existe tail, en una lista simplemente enlazada
     * el último nodo no conoce a su predecesor. Por ello,
     * en el caso general se debe recorrer desde head.
     */
    public T popBack() {
        verificarNoVacia();

        if (size == 1) {
            NodoSimple<T> eliminado = head;
            T valor = eliminado.getDato();

            head = null;
            tail = null;
            size = 0;

            desactivarNodo(eliminado);

            return valor;
        }

        NodoSimple<T> actual = head;

        while (actual.next != tail) {
            actual = actual.next;
        }

        NodoSimple<T> eliminado = tail;
        T valor = eliminado.getDato();

        actual.next = null;
        tail = actual;

        size--;

        desactivarNodo(eliminado);

        return valor;
    }

    /**
     * Busca desde head la primera coincidencia.
     *
     * @return referencia al primer nodo coincidente,
     *         o null si el valor no existe
     */
    public NodoSimple<T> find(T valor) {
        validarDato(valor);

        NodoSimple<T> actual = head;

        while (actual != null) {
            if (actual.getDato().equals(valor)) {
                return actual;
            }

            actual = actual.next;
        }

        return null;
    }

    /**
     * Elimina exactamente el nodo recibido.
     */
    public void erase(NodoSimple<T> nodo) {
        validarNodo(nodo);

        // Caso: eliminar head.
        if (nodo == head) {
            head = nodo.next;
            size--;

            if (size == 0) {
                tail = null;
            }

            desactivarNodo(nodo);

            return;
        }

        NodoSimple<T> anterior = head;

        while (anterior != null && anterior.next != nodo) {
            anterior = anterior.next;
        }

        if (anterior == null) {
            throw new IllegalArgumentException(
                    "El nodo no pertenece a esta lista."
            );
        }

        anterior.next = nodo.next;

        // Si eliminamos el último nodo,
        // el anterior pasa a ser tail.
        if (nodo == tail) {
            tail = anterior;
        }

        size--;

        desactivarNodo(nodo);
    }

    /**
     * Inserta un valor inmediatamente antes del nodo indicado.
     */
    public void addBefore(NodoSimple<T> nodo, T valor) {
        validarNodo(nodo);
        validarDato(valor);

        if (nodo == head) {
            pushFront(valor);
            return;
        }

        NodoSimple<T> anterior = head;

        while (anterior != null && anterior.next != nodo) {
            anterior = anterior.next;
        }

        if (anterior == null) {
            throw new IllegalArgumentException(
                    "El nodo no pertenece a esta lista."
            );
        }

        NodoSimple<T> nuevo = new NodoSimple<>(valor);

        nuevo.next = nodo;
        nuevo.owner = this;
        nuevo.active = true;

        anterior.next = nuevo;

        size++;
    }

    /**
     * Inserta un valor inmediatamente después del nodo indicado.
     */
    public void addAfter(NodoSimple<T> nodo, T valor) {
        validarNodo(nodo);
        validarDato(valor);

        NodoSimple<T> nuevo = new NodoSimple<>(valor);

        nuevo.next = nodo.next;
        nuevo.owner = this;
        nuevo.active = true;

        nodo.next = nuevo;

        // Si insertamos después del último,
        // el nuevo nodo pasa a ser tail.
        if (nodo == tail) {
            tail = nuevo;
        }

        size++;
    }

    /**
     * Retorna el primer elemento sin eliminarlo.
     */
    public T topFront() {
        verificarNoVacia();

        return head.getDato();
    }

    /**
     * Retorna el último elemento sin eliminarlo.
     */
    public T topBack() {
        verificarNoVacia();

        return tail.getDato();
    }

    /**
     * Rechaza valores null.
     */
    private void validarDato(T valor) {
        if (valor == null) {
            throw new IllegalArgumentException(
                    "No se permiten valores null."
            );
        }
    }

    /**
     * Verifica que haya al menos un elemento.
     */
    private void verificarNoVacia() {
        if (isEmpty()) {
            throw new IllegalStateException(
                    "La lista está vacía."
            );
        }
    }

    /**
     * Comprueba en O(1) que el nodo sea vigente
     * y pertenezca a esta lista.
     */
    private void validarNodo(NodoSimple<T> nodo) {
        if (nodo == null) {
            throw new IllegalArgumentException(
                    "La referencia al nodo no puede ser null."
            );
        }

        if (nodo.owner != this || !nodo.active) {
            throw new IllegalArgumentException(
                    "El nodo no pertenece a esta lista o ya fue eliminado."
            );
        }
    }

    /**
     * Marca un nodo como eliminado.
     */
    private void desactivarNodo(NodoSimple<T> nodo) {
        nodo.next = null;
        nodo.owner = null;
        nodo.active = false;
    }
}