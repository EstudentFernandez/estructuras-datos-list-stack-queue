from __future__ import annotations

import csv
from pathlib import Path

import matplotlib.pyplot as plt


RESUMEN = Path(
    "resultados/pilas_colas_resumen.csv"
)

RATIOS = Path(
    "resultados/pilas_colas_ratios.csv"
)

CARPETA_GRAFICOS = Path(
    "graficos/pilas_colas"
)

DIAGNOSTICO = Path(
    "resultados/pilas_colas_diagnostico.txt"
)


PILAS = [
    "PilaEnlazada",
    "PilaArreglo"
]

COLAS = [
    "ColaEnlazada",
    "ColaArreglo"
]


def leer_csv(ruta: Path) -> list[dict]:

    if not ruta.exists():
        raise FileNotFoundError(
            f"No se encontró {ruta}"
        )

    with ruta.open(
        "r",
        encoding="utf-8",
        newline=""
    ) as archivo:

        return list(
            csv.DictReader(archivo)
        )


def convertir_resumen(
    filas: list[dict]
) -> list[dict]:

    resultado = []

    for fila in filas:

        resultado.append({
            **fila,
            "n_inicial":
                int(fila["n_inicial"]),
            "n_final":
                int(fila["n_final"]),
            "n_efectivo":
                int(fila["n_efectivo"]),
            "q1_ns":
                float(fila["q1_ns"]),
            "mediana_ns":
                float(fila["mediana_ns"]),
            "q3_ns":
                float(fila["q3_ns"]),
            "iqr_ns":
                float(fila["iqr_ns"])
        })

    return resultado


def convertir_ratios(
    filas: list[dict]
) -> list[dict]:

    resultado = []

    for fila in filas:

        resultado.append({
            **fila,
            "n_anterior":
                int(fila["n_anterior"]),
            "n_actual":
                int(fila["n_actual"]),
            "factor_n":
                float(fila["factor_n"]),
            "mediana_anterior_ns":
                float(
                    fila["mediana_anterior_ns"]
                ),
            "mediana_actual_ns":
                float(
                    fila["mediana_actual_ns"]
                ),
            "ratio_tiempo":
                float(fila["ratio_tiempo"])
        })

    return resultado


def obtener_serie(
    resumen: list[dict],
    bloque: str,
    implementacion: str,
    metodo: str,
    escenario: str
) -> list[dict]:

    serie = [
        fila
        for fila in resumen
        if fila["bloque"] == bloque
        and fila["implementacion"]
        == implementacion
        and fila["metodo"] == metodo
        and fila["escenario"] == escenario
    ]

    serie.sort(
        key=lambda fila:
            fila["n_efectivo"]
    )

    return serie


def usar_log_y(
    resumen: list[dict],
    bloque: str,
    metodo: str,
    escenario: str
) -> bool:

    valores = [
        fila["mediana_ns"]
        for fila in resumen
        if fila["bloque"] == bloque
        and fila["metodo"] == metodo
        and fila["escenario"] == escenario
        and fila["mediana_ns"] > 0
    ]

    if not valores:
        return False

    minimo = min(valores)
    maximo = max(valores)

    return (
        minimo > 0
        and maximo / minimo >= 50
    )


def dibujar_panel(
    ax,
    resumen: list[dict],
    bloque: str,
    implementaciones: list[str],
    metodo: str,
    escenario: str,
    titulo: str
) -> None:

    for implementacion in implementaciones:

        serie = obtener_serie(
            resumen,
            bloque,
            implementacion,
            metodo,
            escenario
        )

        if not serie:
            continue

        x = [
            fila["n_efectivo"]
            for fila in serie
        ]

        mediana = [
            fila["mediana_ns"]
            for fila in serie
        ]

        q1 = [
            fila["q1_ns"]
            for fila in serie
        ]

        q3 = [
            fila["q3_ns"]
            for fila in serie
        ]

        linea, = ax.plot(
            x,
            mediana,
            marker="o",
            label=implementacion
        )

        ax.fill_between(
            x,
            q1,
            q3,
            alpha=0.12,
            color=linea.get_color()
        )

    ax.set_title(titulo)

    ax.set_xlabel(
        "Tamaño n"
    )

    ax.set_ylabel(
        "Tiempo mediano (ns/op)"
    )

    ax.set_xscale(
        "log"
    )

    if usar_log_y(
        resumen,
        bloque,
        metodo,
        escenario
    ):
        ax.set_yscale(
            "log"
        )

    ax.grid(
        True,
        which="both",
        alpha=0.25
    )


def agregar_leyenda(
    figura,
    axes,
    columnas: int = 2
) -> None:

    encontrados = {}

    for ax in axes:

        handles, labels = (
            ax.get_legend_handles_labels()
        )

        for handle, label in zip(
            handles,
            labels
        ):
            encontrados[label] = handle

    if encontrados:

        figura.legend(
            encontrados.values(),
            encontrados.keys(),
            loc="lower center",
            ncol=columnas,
            bbox_to_anchor=(0.5, -0.01)
        )


def guardar(
    figura,
    nombre: str
) -> None:

    CARPETA_GRAFICOS.mkdir(
        parents=True,
        exist_ok=True
    )

    ruta = (
        CARPETA_GRAFICOS
        / nombre
    )

    figura.tight_layout()

    figura.subplots_adjust(
        bottom=0.15
    )

    figura.savefig(
        ruta,
        dpi=220,
        bbox_inches="tight"
    )

    plt.close(
        figura
    )

    print(
        f"Generado: {ruta}"
    )


# =========================================================
# PILAS
# =========================================================

def graficar_push_pilas(
    resumen: list[dict]
) -> None:

    paneles = [
        (
            "sin_redimensionamiento",
            "Push con capacidad disponible"
        ),
        (
            "crecimiento_forzado",
            "Push con crecimiento forzado"
        ),
        (
            "secuencia_amortizada",
            "Push: costo por operación en secuencia"
        )
    ]

    fig, axes = plt.subplots(
        1,
        3,
        figsize=(15, 5)
    )

    for ax, (
        escenario,
        titulo
    ) in zip(
        axes,
        paneles
    ):

        dibujar_panel(
            ax,
            resumen,
            "pilas",
            PILAS,
            "push",
            escenario,
            titulo
        )

    agregar_leyenda(
        fig,
        axes
    )

    fig.suptitle(
        "Pilas: análisis de Push",
        fontsize=14
    )

    guardar(
        fig,
        "01_pilas_push.png"
    )


def graficar_basicas_pilas(
    resumen: list[dict]
) -> None:

    paneles = [
        (
            "pop",
            "general",
            "Pop"
        ),
        (
            "peek",
            "general",
            "Peek"
        ),
        (
            "size",
            "no_vacia",
            "Size"
        ),
        (
            "isEmpty",
            "no_vacia",
            "IsEmpty"
        )
    ]

    fig, axes = plt.subplots(
        2,
        2,
        figsize=(11, 9)
    )

    for ax, (
        metodo,
        escenario,
        titulo
    ) in zip(
        axes.flat,
        paneles
    ):

        dibujar_panel(
            ax,
            resumen,
            "pilas",
            PILAS,
            metodo,
            escenario,
            titulo
        )

    agregar_leyenda(
        fig,
        list(axes.flat)
    )

    fig.suptitle(
        "Pilas: operaciones fundamentales",
        fontsize=14
    )

    guardar(
        fig,
        "02_pilas_basicas.png"
    )


def graficar_delete_pilas(
    resumen: list[dict]
) -> None:

    escenarios = [
        (
            "cima",
            "Delete: valor en la cima"
        ),
        (
            "medio",
            "Delete: valor en el medio"
        ),
        (
            "base",
            "Delete: valor en la base"
        ),
        (
            "ausente",
            "Delete: valor ausente"
        )
    ]

    fig, axes = plt.subplots(
        2,
        2,
        figsize=(11, 9)
    )

    for ax, (
        escenario,
        titulo
    ) in zip(
        axes.flat,
        escenarios
    ):

        dibujar_panel(
            ax,
            resumen,
            "pilas",
            PILAS,
            "delete",
            escenario,
            titulo
        )

    agregar_leyenda(
        fig,
        list(axes.flat)
    )

    fig.suptitle(
        "Pilas: Delete según posición",
        fontsize=14
    )

    guardar(
        fig,
        "03_pilas_delete.png"
    )


# =========================================================
# COLAS
# =========================================================

def graficar_enqueue_colas(
    resumen: list[dict]
) -> None:

    paneles = [
        (
            "sin_redimensionamiento",
            "Enqueue con capacidad disponible"
        ),
        (
            "crecimiento_forzado",
            "Enqueue con crecimiento forzado"
        ),
        (
            "secuencia_amortizada",
            "Enqueue: costo por operación en secuencia"
        )
    ]

    fig, axes = plt.subplots(
        1,
        3,
        figsize=(15, 5)
    )

    for ax, (
        escenario,
        titulo
    ) in zip(
        axes,
        paneles
    ):

        dibujar_panel(
            ax,
            resumen,
            "colas",
            COLAS,
            "enqueue",
            escenario,
            titulo
        )

    agregar_leyenda(
        fig,
        axes
    )

    fig.suptitle(
        "Colas: análisis de Enqueue",
        fontsize=14
    )

    guardar(
        fig,
        "04_colas_enqueue.png"
    )


def graficar_basicas_colas(
    resumen: list[dict]
) -> None:

    paneles = [
        (
            "dequeue",
            "general",
            "Dequeue"
        ),
        (
            "front",
            "general",
            "Front"
        ),
        (
            "size",
            "no_vacia",
            "Size"
        ),
        (
            "isEmpty",
            "no_vacia",
            "IsEmpty"
        )
    ]

    fig, axes = plt.subplots(
        2,
        2,
        figsize=(11, 9)
    )

    for ax, (
        metodo,
        escenario,
        titulo
    ) in zip(
        axes.flat,
        paneles
    ):

        dibujar_panel(
            ax,
            resumen,
            "colas",
            COLAS,
            metodo,
            escenario,
            titulo
        )

    agregar_leyenda(
        fig,
        list(axes.flat)
    )

    fig.suptitle(
        "Colas: operaciones fundamentales",
        fontsize=14
    )

    guardar(
        fig,
        "05_colas_basicas.png"
    )


def graficar_delete_colas(
    resumen: list[dict]
) -> None:

    escenarios = [
        (
            "frente",
            "Delete: valor en el frente"
        ),
        (
            "medio",
            "Delete: valor en el medio"
        ),
        (
            "final",
            "Delete: valor al final"
        ),
        (
            "ausente",
            "Delete: valor ausente"
        )
    ]

    fig, axes = plt.subplots(
        2,
        2,
        figsize=(11, 9)
    )

    for ax, (
        escenario,
        titulo
    ) in zip(
        axes.flat,
        escenarios
    ):

        dibujar_panel(
            ax,
            resumen,
            "colas",
            COLAS,
            "delete",
            escenario,
            titulo
        )

    agregar_leyenda(
        fig,
        list(axes.flat)
    )

    fig.suptitle(
        "Colas: Delete según posición",
        fontsize=14
    )

    guardar(
        fig,
        "06_colas_delete.png"
    )


# =========================================================
# DIAGNÓSTICO
# =========================================================

def obtener_mediana_maxima(
    resumen: list[dict],
    bloque: str,
    implementacion: str,
    metodo: str,
    escenario: str
):

    serie = obtener_serie(
        resumen,
        bloque,
        implementacion,
        metodo,
        escenario
    )

    if not serie:
        return None

    fila = max(
        serie,
        key=lambda r:
            r["n_efectivo"]
    )

    return (
        fila["n_efectivo"],
        fila["mediana_ns"]
    )


def obtener_ratios(
    ratios: list[dict],
    bloque: str,
    implementacion: str,
    metodo: str,
    escenario: str
) -> list[dict]:

    filas = [
        fila
        for fila in ratios
        if fila["bloque"] == bloque
        and fila["implementacion"]
        == implementacion
        and fila["metodo"] == metodo
        and fila["escenario"] == escenario
    ]

    filas.sort(
        key=lambda fila:
            fila["n_actual"]
    )

    return filas


def escribir_caso(
    salida,
    resumen: list[dict],
    ratios: list[dict],
    titulo: str,
    bloque: str,
    implementaciones: list[str],
    metodo: str,
    escenario: str
) -> None:

    salida.write(
        titulo + "\n"
    )

    salida.write(
        "-" * len(titulo)
        + "\n"
    )

    for implementacion in implementaciones:

        resultado = obtener_mediana_maxima(
            resumen,
            bloque,
            implementacion,
            metodo,
            escenario
        )

        if resultado is None:
            continue

        n_max, mediana = resultado

        salida.write(
            f"{implementacion}: "
            f"mediana(n={n_max}) = "
            f"{mediana:.3f} ns/op\n"
        )

        filas_ratio = obtener_ratios(
            ratios,
            bloque,
            implementacion,
            metodo,
            escenario
        )

        if filas_ratio:

            texto = ", ".join(
                (
                    f"{fila['n_anterior']}"
                    f"->{fila['n_actual']}: "
                    f"{fila['ratio_tiempo']:.3f}"
                )
                for fila
                in filas_ratio
            )

            salida.write(
                f"  ratios: {texto}\n"
            )

    salida.write(
        "\n"
    )


def escribir_diagnostico(
    resumen: list[dict],
    ratios: list[dict]
) -> None:

    DIAGNOSTICO.parent.mkdir(
        parents=True,
        exist_ok=True
    )

    with DIAGNOSTICO.open(
        "w",
        encoding="utf-8"
    ) as salida:

        salida.write(
            "DIAGNOSTICO EXPERIMENTAL - "
            "PILAS Y COLAS\n"
        )

        salida.write(
            "=" * 72
            + "\n\n"
        )

        # PILAS

        escribir_caso(
            salida,
            resumen,
            ratios,
            "Pila - Push sin redimensionamiento",
            "pilas",
            PILAS,
            "push",
            "sin_redimensionamiento"
        )

        escribir_caso(
            salida,
            resumen,
            ratios,
            "Pila - Push con crecimiento forzado",
            "pilas",
            PILAS,
            "push",
            "crecimiento_forzado"
        )

        escribir_caso(
            salida,
            resumen,
            ratios,
            "Pila - Push en secuencia amortizada",
            "pilas",
            PILAS,
            "push",
            "secuencia_amortizada"
        )

        escribir_caso(
            salida,
            resumen,
            ratios,
            "Pila - Pop",
            "pilas",
            PILAS,
            "pop",
            "general"
        )

        escribir_caso(
            salida,
            resumen,
            ratios,
            "Pila - Peek",
            "pilas",
            PILAS,
            "peek",
            "general"
        )

        escribir_caso(
            salida,
            resumen,
            ratios,
            "Pila - Delete en la cima",
            "pilas",
            PILAS,
            "delete",
            "cima"
        )

        escribir_caso(
            salida,
            resumen,
            ratios,
            "Pila - Delete en el medio",
            "pilas",
            PILAS,
            "delete",
            "medio"
        )

        escribir_caso(
            salida,
            resumen,
            ratios,
            "Pila - Delete en la base",
            "pilas",
            PILAS,
            "delete",
            "base"
        )

        escribir_caso(
            salida,
            resumen,
            ratios,
            "Pila - Delete ausente",
            "pilas",
            PILAS,
            "delete",
            "ausente"
        )

        # COLAS

        escribir_caso(
            salida,
            resumen,
            ratios,
            "Cola - Enqueue sin redimensionamiento",
            "colas",
            COLAS,
            "enqueue",
            "sin_redimensionamiento"
        )

        escribir_caso(
            salida,
            resumen,
            ratios,
            "Cola - Enqueue con crecimiento forzado",
            "colas",
            COLAS,
            "enqueue",
            "crecimiento_forzado"
        )

        escribir_caso(
            salida,
            resumen,
            ratios,
            "Cola - Enqueue en secuencia amortizada",
            "colas",
            COLAS,
            "enqueue",
            "secuencia_amortizada"
        )

        escribir_caso(
            salida,
            resumen,
            ratios,
            "Cola - Dequeue",
            "colas",
            COLAS,
            "dequeue",
            "general"
        )

        escribir_caso(
            salida,
            resumen,
            ratios,
            "Cola - Front",
            "colas",
            COLAS,
            "front",
            "general"
        )

        escribir_caso(
            salida,
            resumen,
            ratios,
            "Cola - Delete en el frente",
            "colas",
            COLAS,
            "delete",
            "frente"
        )

        escribir_caso(
            salida,
            resumen,
            ratios,
            "Cola - Delete en el medio",
            "colas",
            COLAS,
            "delete",
            "medio"
        )

        escribir_caso(
            salida,
            resumen,
            ratios,
            "Cola - Delete al final",
            "colas",
            COLAS,
            "delete",
            "final"
        )

        escribir_caso(
            salida,
            resumen,
            ratios,
            "Cola - Delete ausente",
            "colas",
            COLAS,
            "delete",
            "ausente"
        )

    print(
        f"Generado: {DIAGNOSTICO}"
    )


def main() -> None:

    resumen = convertir_resumen(
        leer_csv(RESUMEN)
    )

    ratios = convertir_ratios(
        leer_csv(RATIOS)
    )

    graficar_push_pilas(
        resumen
    )

    graficar_basicas_pilas(
        resumen
    )

    graficar_delete_pilas(
        resumen
    )

    graficar_enqueue_colas(
        resumen
    )

    graficar_basicas_colas(
        resumen
    )

    graficar_delete_colas(
        resumen
    )

    escribir_diagnostico(
        resumen,
        ratios
    )

    print()
    print(
        "Gráficos de pilas y colas "
        "generados correctamente."
    )


if __name__ == "__main__":
    main()