# Análisis teórico de complejidad

## 1. Convenciones

Sea `n` el número actual de elementos almacenados en la estructura.

Se supone que:

- La comparación mediante `equals()` tiene costo O(1).
- Las estructuras mantienen un contador `size`.
- En las operaciones que reciben una referencia a nodo, dicha referencia ya está disponible.
- La validación de pertenencia de un nodo se realiza en O(1) mediante
  los campos `owner` y `active`.
- Los valores `null` no están permitidos.

Se emplea notación Θ cuando puede establecerse una cota asintótica
ajustada. En particular, si una operación es Θ(f(n)), también pertenece
a O(f(n)).

---

# 2. Listas enlazadas

## 2.1. Tabla de complejidades

| Método | Simple sin cola | Simple con cola | Doble sin cola | Doble con cola |
|---|---:|---:|---:|---:|
| pushFront | Θ(1) | Θ(1) | Θ(1) | Θ(1) |
| pushBack | Θ(n) | Θ(1) | Θ(n) | Θ(1) |
| popFront | Θ(1) | Θ(1) | Θ(1) | Θ(1) |
| popBack | Θ(n) | Θ(n) | Θ(n) | Θ(1) |
| find | Θ(n) | Θ(n) | Θ(n) | Θ(n) |
| erase(nodo) | Θ(n) | Θ(n) | Θ(1) | Θ(1) |
| addBefore(nodo) | Θ(n) | Θ(n) | Θ(1) | Θ(1) |
| addAfter(nodo) | Θ(1) | Θ(1) | Θ(1) | Θ(1) |
| topFront | Θ(1) | Θ(1) | Θ(1) | Θ(1) |
| topBack | Θ(n) | Θ(1) | Θ(n) | Θ(1) |
| size | Θ(1) | Θ(1) | Θ(1) | Θ(1) |
| isEmpty | Θ(1) | Θ(1) | Θ(1) | Θ(1) |

## 2.2. Efecto de la referencia tail

En una lista sin referencia al último nodo, encontrar el final requiere
recorrer la cadena desde `head`.

Para una lista de `n` nodos:

T(n) = a(n - 1) + b

por lo que:

T(n) ∈ Θ(n).

Al almacenar `tail`, el último nodo se obtiene directamente. Por ello:

pushBack: Θ(n) -> Θ(1)

topBack: Θ(n) -> Θ(1)

Sin embargo, en una lista simplemente enlazada:

popBack ∈ Θ(n)

incluso cuando existe `tail`, porque el último nodo no almacena una
referencia a su predecesor. Para eliminarlo debe localizarse el
penúltimo nodo mediante un recorrido desde `head`.

## 2.3. Efecto del enlace prev

En una lista simplemente enlazada, disponer de una referencia a un nodo
no proporciona directamente una referencia a su predecesor.

Por esta razón:

erase(nodo) ∈ Θ(n)

addBefore(nodo) ∈ Θ(n)

en el peor caso.

En una lista doblemente enlazada cada nodo contiene `prev` y `next`.

Si el nodo `v` se encuentra entre `u` y `w`, puede eliminarse mediante
un número constante de modificaciones:

u.next = w
w.prev = u

Por tanto:

erase(nodo) ∈ Θ(1).

Análogamente, `addBefore` puede acceder directamente al predecesor y
también pertenece a Θ(1).

## 2.4. Búsqueda

Las cuatro listas implementan una búsqueda secuencial desde `head`.

En el peor caso se examinan los `n` elementos:

T(n) = an + b

y por tanto:

find ∈ Θ(n).

La presencia de `tail` o `prev` no introduce ningún mecanismo de
indexación por valor, por lo que no mejora la complejidad de `find`.

## 2.5. Búsqueda más eliminación

Debe distinguirse:

erase(nodo)

de:

find(valor) + erase(nodo).

Por ejemplo, en una lista doble:

find(valor) ∈ Θ(n)

erase(nodo) ∈ Θ(1)

por lo que:

Θ(n) + Θ(1) = Θ(n).

---

# 3. Pilas

## 3.1. Pila enlazada

La cima de la pila corresponde al inicio de una lista simplemente
enlazada.

Por tanto:

| Método | Complejidad |
|---|---:|
| push | Θ(1) |
| pop | Θ(1) |
| peek | Θ(1) |
| isEmpty | Θ(1) |
| size | Θ(1) |
| delete | Θ(n) |

`push`, `pop` y `peek` operan directamente sobre el primer nodo.

`delete(valor)` busca la primera coincidencia desde la cima y conserva
el orden relativo de los demás elementos.

En el peor caso debe examinar un número proporcional a `n` elementos.

---

## 3.2. Pila con arreglo dinámico

La pila utiliza un arreglo dinámico propio. La cima corresponde a la
posición lógica:

size - 1.

Por ello:

| Método | Complejidad |
|---|---:|
| push con espacio disponible | Θ(1) |
| push que provoca crecimiento | Θ(n) |
| push amortizado | Θ(1) |
| pop | Θ(1) |
| peek | Θ(1) |
| isEmpty | Θ(1) |
| size | Θ(1) |
| delete | Θ(n) |

La representación puede interpretarse como un caso particular de
arreglo circular cuyo inicio lógico permanece en cero. Para una pila
LIFO no es necesario desplazar dicho inicio.

---

# 4. Crecimiento geométrico del arreglo

Cuando un arreglo se llena, su capacidad se duplica:

C, 2C, 4C, 8C, ...

Una inserción que fuerza crecimiento debe copiar los elementos
existentes y tiene costo Θ(n).

Sin embargo, para una secuencia de N inserciones, el número total de
elementos copiados está acotado por una suma geométrica.

En el caso simplificado de capacidad inicial 1:

1 + 2 + 4 + ... + N/2 < N.

Por tanto, las copias acumuladas cuestan O(N).

Las propias N inserciones también representan O(N).

Así:

T(N) = O(N) + O(N) = O(N).

El costo amortizado por inserción es entonces:

T(N) / N = O(1).

Por esta razón `push` y `enqueue` son O(1) amortizados, aunque una
operación particular que provoque crecimiento sea O(n).

Si la capacidad creciera en una constante `k` en vez de duplicarse, las
copias serían aproximadamente:

k + 2k + 3k + ... + mk,

una suma aritmética de orden Θ(m²). Esa estrategia no proporciona el
mismo comportamiento amortizado.

---

# 5. Colas

## 5.1. Cola enlazada

La cola enlazada mantiene acceso directo al primer y al último nodo.

`enqueue` inserta en el final y `dequeue` elimina desde el inicio.

| Método | Complejidad |
|---|---:|
| enqueue | Θ(1) |
| dequeue | Θ(1) |
| front | Θ(1) |
| isEmpty | Θ(1) |
| size | Θ(1) |
| delete | Θ(n) |

No es necesario recorrer la lista para insertar o extraer elementos.

---

# 6. Cola con arreglo circular dinámico

La cola con arreglo utiliza:

- un arreglo de capacidad `C`;
- un índice `head`;
- un contador `size`.

La posición física correspondiente a la posición lógica `i` es:

p(i) = (head + i) mod C.

Esta expresión permite que la secuencia lógica atraviese el final físico
del arreglo.

Por ejemplo, si:

C = 8
head = 6

entonces:

p(0) = 6
p(1) = 7
p(2) = 0
p(3) = 1.

De esta manera `dequeue` no necesita desplazar todos los elementos.

Únicamente actualiza:

head = (head + 1) mod C.

Por tanto:

dequeue ∈ Θ(1).

La tabla de complejidad es:

| Método | Complejidad |
|---|---:|
| enqueue con capacidad disponible | Θ(1) |
| enqueue que provoca crecimiento | Θ(n) |
| enqueue amortizado | Θ(1) |
| dequeue | Θ(1) |
| front | Θ(1) |
| isEmpty | Θ(1) |
| size | Θ(1) |
| delete | Θ(n) |

`delete` necesita buscar la primera coincidencia desde el frente y puede
requerir desplazar una cantidad lineal de elementos lógicos para
conservar el orden FIFO.

---

# 7. Espacio utilizado

Las listas enlazadas utilizan Θ(n) nodos.

Una lista doble necesita una referencia adicional `prev` en cada nodo,
por lo que conserva el mismo orden espacial Θ(n), aunque con una
constante mayor.

Agregar `tail` representa únicamente una referencia adicional por lista:

Θ(1) de espacio adicional.

Las estructuras con arreglo reservan una capacidad que puede ser mayor
que el tamaño lógico actual. Como no se reduce automáticamente la
capacidad, el espacio reservado depende de la ocupación máxima histórica.

Si `m` representa dicha ocupación máxima:

espacio = O(m).

---

# 8. Hipótesis para la fase experimental

Se espera que las operaciones Θ(1) muestren tiempos aproximadamente
independientes de `n`, dentro del ruido experimental.

Para una operación Θ(n), si el tamaño se multiplica por diez, se espera
que en un rango suficientemente estable el tiempo también crezca
aproximadamente en un factor cercano a diez:

t(10n) / t(n) ≈ 10.

Este comportamiento empírico es evidencia compatible con el modelo
lineal, pero no constituye una demostración de la complejidad
asintótica. La justificación Big-O proviene del análisis del algoritmo.

Las operaciones amortizadas deben analizarse por separado en al menos
tres escenarios:

1. inserción con capacidad disponible;
2. inserción que provoca crecimiento;
3. secuencia larga de inserciones.

Esto permite distinguir el costo de una operación concreta del costo
amortizado de una secuencia.