\# Decisiones de diseño



\## Contrato general



Las estructuras utilizan tipos genéricos `T`.



No se permiten valores `null`. Las operaciones de extracción o consulta

de extremos sobre una estructura vacía lanzan `IllegalStateException`.



Los valores se comparan mediante `equals()`.



\## Listas enlazadas



Se implementarán cuatro variantes:



1\. Lista simplemente enlazada sin referencia al último nodo.

2\. Lista simplemente enlazada con referencia `tail`.

3\. Lista doblemente enlazada sin referencia al último nodo.

4\. Lista doblemente enlazada con referencia `tail`.



Todas mantienen un contador `size`.



\## Find



`find(valor)` retorna la referencia al primer nodo cuyo valor sea igual

al buscado recorriendo desde `head`. Si no existe, retorna `null`.



Los valores duplicados están permitidos.



\## Operaciones por referencia



`erase(nodo)`, `addBefore(nodo, valor)` y `addAfter(nodo, valor)`

trabajan con la identidad del nodo recibido.



Los nodos mantienen información de propietario y vigencia para validar

en O(1) que pertenecen a la lista y que no han sido eliminados.



Una referencia a un nodo ajeno o eliminado produce

`IllegalArgumentException`.



\## Invariantes generales



\- `size >= 0`.

\- `size == 0` si y solo si `head == null`.

\- `size` coincide con el número de nodos alcanzables desde `head`.



En las variantes con `tail`:



\- `size == 0` si y solo si `head == null \&\& tail == null`.

\- Si `size == 1`, entonces `head == tail`.

\- `tail.next == null`.



En las listas dobles:



\- `head.prev == null`.

\- Si `v.next != null`, entonces `v.next.prev == v`.

\- Si `v.prev != null`, entonces `v.prev.next == v`.



\## Métodos de las listas



\- `pushFront(T valor)`

\- `pushBack(T valor)`

\- `popFront()`

\- `popBack()`

\- `find(T valor)`

\- `erase(nodo)`

\- `addBefore(nodo, T valor)`

\- `addAfter(nodo, T valor)`

\- `topFront()`

\- `topBack()`

\- `size()`

\- `isEmpty()`

