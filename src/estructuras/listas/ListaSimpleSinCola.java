package estructuras.listas;

/**
 * Lista simplemente enlazada sin referencia directa al último nodo.
 *
 * @param <T> tipo de dato almacenado en la lista
 */
public class ListaSimpleSinCola<T> {

    private NodoSimple<T> head;
    private int size;

    /**
     * Construye una lista vacía.
     */
    public ListaSimpleSinCola() {
        this.head = null;
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
     * Inserta un elemento al inicio de la lista.
     */
    public void pushFront(T valor) {
        validarDato(valor);

        NodoSimple<T> nuevo = new NodoSimple<>(valor);

        nuevo.next = head;
        nuevo.owner = this;
        nuevo.active = true;

        head = nuevo;
        size++;
    }

    /**
     * Inserta un elemento al final de la lista.
     */
    public void pushBack(T valor) {
        validarDato(valor);

        NodoSimple<T> nuevo = new NodoSimple<>(valor);
        nuevo.owner = this;
        nuevo.active = true;

        if (isEmpty()) {
            head = nuevo;
            size++;
            return;
        }

        NodoSimple<T> actual = head;

        while (actual.next != null) {
            actual = actual.next;
        }

        actual.next = nuevo;
        size++;
    }

    /**
     * Elimina y retorna el primer elemento de la lista.
     */
    public T popFront() {
        verificarNoVacia();

        NodoSimple<T> eliminado = head;
        T valor = eliminado.getDato();

        head = eliminado.next;

        desactivarNodo(eliminado);

        size--;

        return valor;
    }

    /**
     * Elimina y retorna el último elemento de la lista.
     */
    public T popBack() {
        verificarNoVacia();

        if (head.next == null) {
            NodoSimple<T> eliminado = head;
            T valor = eliminado.getDato();

            head = null;
            desactivarNodo(eliminado);

            size--;

            return valor;
        }

        NodoSimple<T> actual = head;

        // Al terminar, actual será el penúltimo nodo.
        while (actual.next.next != null) {
            actual = actual.next;
        }

        NodoSimple<T> eliminado = actual.next;
        T valor = eliminado.getDato();

        actual.next = null;

        desactivarNodo(eliminado);

        size--;

        return valor;
    }

    /**
     * Busca desde head el primer nodo cuyo dato sea igual al valor recibido.
     *
     * @return referencia al primer nodo coincidente, o null si no existe
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
     * Elimina exactamente el nodo indicado.
     */
    public void erase(NodoSimple<T> nodo) {
        validarNodo(nodo);

        if (nodo == head) {
            head = nodo.next;

            desactivarNodo(nodo);
            size--;

            return;
        }

        NodoSimple<T> anterior = head;

        while (anterior != null && anterior.next != nodo) {
            anterior = anterior.next;
        }

        /*
         * Si las invariantes se cumplen, este caso no debería ocurrir
         * después de validar owner y active. Se conserva como defensa
         * ante un estado estructural inconsistente.
         */
        if (anterior == null) {
            throw new IllegalArgumentException(
                    "El nodo no pertenece a esta lista."
            );
        }

        anterior.next = nodo.next;

        desactivarNodo(nodo);
        size--;
    }

    /**
     * Inserta un nuevo valor inmediatamente antes del nodo indicado.
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
     * Inserta un nuevo valor inmediatamente después del nodo indicado.
     */
    public void addAfter(NodoSimple<T> nodo, T valor) {
        validarNodo(nodo);
        validarDato(valor);

        NodoSimple<T> nuevo = new NodoSimple<>(valor);

        nuevo.next = nodo.next;
        nuevo.owner = this;
        nuevo.active = true;

        nodo.next = nuevo;

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

        NodoSimple<T> actual = head;

        while (actual.next != null) {
            actual = actual.next;
        }

        return actual.getDato();
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
     * Comprueba que la lista tenga al menos un elemento.
     */
    private void verificarNoVacia() {
        if (isEmpty()) {
            throw new IllegalStateException(
                    "La lista está vacía."
            );
        }
    }

    /**
     * Verifica en O(1) que el nodo sea vigente y pertenezca a esta lista.
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
     * Marca un nodo como eliminado y elimina sus enlaces estructurales.
     */
    private void desactivarNodo(NodoSimple<T> nodo) {
        nodo.next = null;
        nodo.owner = null;
        nodo.active = false;
    }
}