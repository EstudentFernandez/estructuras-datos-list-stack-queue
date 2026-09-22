package estructuras.listas;

/**
 * Lista doblemente enlazada con referencia directa
 * al primer y al último nodo.
 *
 * @param <T> tipo de dato almacenado en la lista
 */
public class ListaDobleConCola<T> {

    private NodoDoble<T> head;
    private NodoDoble<T> tail;
    private int size;

    /**
     * Construye una lista vacía.
     */
    public ListaDobleConCola() {
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

        NodoDoble<T> nuevo = new NodoDoble<>(valor);

        nuevo.prev = null;
        nuevo.next = head;
        nuevo.owner = this;
        nuevo.active = true;

        if (isEmpty()) {
            head = nuevo;
            tail = nuevo;
        } else {
            head.prev = nuevo;
            head = nuevo;
        }

        size++;
    }

    /**
     * Inserta un elemento al final.
     */
    public void pushBack(T valor) {
        validarDato(valor);

        NodoDoble<T> nuevo = new NodoDoble<>(valor);

        nuevo.prev = tail;
        nuevo.next = null;
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

        NodoDoble<T> eliminado = head;
        T valor = eliminado.getDato();

        head = eliminado.next;

        if (head == null) {
            tail = null;
        } else {
            head.prev = null;
        }

        size--;

        desactivarNodo(eliminado);

        return valor;
    }

    /**
     * Elimina y retorna el último elemento.
     */
    public T popBack() {
        verificarNoVacia();

        NodoDoble<T> eliminado = tail;
        T valor = eliminado.getDato();

        tail = eliminado.prev;

        if (tail == null) {
            head = null;
        } else {
            tail.next = null;
        }

        size--;

        desactivarNodo(eliminado);

        return valor;
    }

    /**
     * Busca desde head la primera coincidencia.
     *
     * @return referencia al primer nodo coincidente
     *         o null si el valor no existe
     */
    public NodoDoble<T> find(T valor) {
        validarDato(valor);

        NodoDoble<T> actual = head;

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
    public void erase(NodoDoble<T> nodo) {
        validarNodo(nodo);

        if (nodo.prev != null) {
            nodo.prev.next = nodo.next;
        } else {
            // El nodo era head.
            head = nodo.next;
        }

        if (nodo.next != null) {
            nodo.next.prev = nodo.prev;
        } else {
            // El nodo era tail.
            tail = nodo.prev;
        }

        size--;

        desactivarNodo(nodo);
    }

    /**
     * Inserta un valor inmediatamente antes
     * del nodo indicado.
     */
    public void addBefore(NodoDoble<T> nodo, T valor) {
        validarNodo(nodo);
        validarDato(valor);

        NodoDoble<T> nuevo = new NodoDoble<>(valor);

        nuevo.prev = nodo.prev;
        nuevo.next = nodo;
        nuevo.owner = this;
        nuevo.active = true;

        if (nodo.prev != null) {
            nodo.prev.next = nuevo;
        } else {
            // nodo era head.
            head = nuevo;
        }

        nodo.prev = nuevo;

        size++;
    }

    /**
     * Inserta un valor inmediatamente después
     * del nodo indicado.
     */
    public void addAfter(NodoDoble<T> nodo, T valor) {
        validarNodo(nodo);
        validarDato(valor);

        NodoDoble<T> nuevo = new NodoDoble<>(valor);

        nuevo.prev = nodo;
        nuevo.next = nodo.next;
        nuevo.owner = this;
        nuevo.active = true;

        if (nodo.next != null) {
            nodo.next.prev = nuevo;
        } else {
            // nodo era tail.
            tail = nuevo;
        }

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

        return tail.getDato();
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

    /**
     * Comprueba en O(1) que el nodo sea vigente
     * y pertenezca a esta lista.
     */
    private void validarNodo(NodoDoble<T> nodo) {
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
     * Desconecta e invalida un nodo eliminado.
     */
    private void desactivarNodo(NodoDoble<T> nodo) {
        nodo.prev = null;
        nodo.next = null;
        nodo.owner = null;
        nodo.active = false;
    }
}