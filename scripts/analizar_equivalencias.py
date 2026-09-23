from __future__ import annotations

import csv
import math
from collections import defaultdict
from pathlib import Path
from statistics import median

import matplotlib.pyplot as plt


ENTRADA = Path(
    "resultados/equivalencias_raw.csv"
)

SALIDA_RESUMEN = Path(
    "resultados/equivalencias_resumen.csv"
)

SALIDA_RATIOS = Path(
    "resultados/equivalencias_ratios.csv"
)

SALIDA_DIAGNOSTICO = Path(
    "resultados/equivalencias_diagnostico.txt"
)

CARPETA_GRAFICOS = Path(
    "graficos/equivalencias"
)


STACK_IMPLEMENTACIONES = [
    "ListaSimpleSinCola",
    "PilaArreglo"
]

QUEUE_IMPLEMENTACIONES = [
    "ListaSimpleConCola",
    "ColaArreglo"
]


STACK_METODOS = [
    ("push", "PushFront vs Push"),
    ("pop", "PopFront vs Pop"),
    ("peek", "TopFront vs Peek"),
    ("delete", "Find + Erase vs Delete"),
    ("size", "Size"),
    ("isEmpty", "IsEmpty")
]


QUEUE_METODOS = [
    ("enqueue", "PushBack vs Enqueue"),
    ("dequeue", "PopFront vs Dequeue"),
    ("front", "TopFront vs Front"),
    ("delete", "Find + Erase vs Delete"),
    ("size", "Size"),
    ("isEmpty", "IsEmpty")
]


def percentil_lineal(
    valores: list[float],
    p: float
) -> float:

    if not valores:
        raise ValueError(
            "No hay datos para calcular el percentil."
        )

    ordenados = sorted(valores)

    if len(ordenados) == 1:
        return ordenados[0]

    posicion = (
        len(ordenados) - 1
    ) * p

    inferior = math.floor(posicion)
    superior = math.ceil(posicion)

    if inferior == superior:
        return ordenados[inferior]

    fraccion = (
        posicion - inferior
    )

    return (
        ordenados[inferior]
        + fraccion
        * (
            ordenados[superior]
            - ordenados[inferior]
        )
    )


def leer_datos() -> list[dict]:

    if not ENTRADA.exists():
        raise FileNotFoundError(
            f"No se encontró {ENTRADA}"
        )

    with ENTRADA.open(
        "r",
        encoding="utf-8",
        newline=""
    ) as archivo:

        datos = list(
            csv.DictReader(archivo)
        )

    if not datos:
        raise ValueError(
            "El archivo de entrada está vacío."
        )

    return datos


def resumir(
    datos: list[dict]
) -> list[dict]:

    grupos = defaultdict(list)
    metadatos = {}

    for fila in datos:

        clave = (
            fila["familia"],
            fila["implementacion"],
            fila["metodo_comun"],
            fila["operacion"],
            fila["escenario"],
            int(fila["n"])
        )

        tiempo = float(
            fila["ns_por_operacion"]
        )

        grupos[clave].append(
            tiempo
        )

        metadatos[clave] = {
            "familia":
                fila["familia"],

            "implementacion":
                fila["implementacion"],

            "metodo_comun":
                fila["metodo_comun"],

            "operacion":
                fila["operacion"],

            "escenario":
                fila["escenario"],

            "semilla":
                fila["semilla"],

            "n":
                int(fila["n"]),

            "k":
                int(fila["k"])
        }

    resumen = []

    for clave, tiempos in grupos.items():

        q1 = percentil_lineal(
            tiempos,
            0.25
        )

        med = median(
            tiempos
        )

        q3 = percentil_lineal(
            tiempos,
            0.75
        )

        media = (
            sum(tiempos)
            / len(tiempos)
        )

        resumen.append({
            **metadatos[clave],

            "observaciones":
                len(tiempos),

            "min_ns":
                min(tiempos),

            "q1_ns":
                q1,

            "mediana_ns":
                med,

            "q3_ns":
                q3,

            "max_ns":
                max(tiempos),

            "iqr_ns":
                q3 - q1,

            "media_ns":
                media
        })

    resumen.sort(
        key=lambda fila: (
            fila["familia"],
            fila["metodo_comun"],
            fila["implementacion"],
            fila["n"]
        )
    )

    return resumen


def calcular_ratios(
    resumen: list[dict]
) -> list[dict]:

    grupos = defaultdict(list)

    for fila in resumen:

        clave = (
            fila["familia"],
            fila["implementacion"],
            fila["metodo_comun"],
            fila["operacion"],
            fila["escenario"]
        )

        grupos[clave].append(
            fila
        )

    ratios = []

    for clave, filas in grupos.items():

        filas.sort(
            key=lambda fila:
                fila["n"]
        )

        for anterior, actual in zip(
            filas,
            filas[1:]
        ):

            mediana_anterior = (
                anterior["mediana_ns"]
            )

            mediana_actual = (
                actual["mediana_ns"]
            )

            if mediana_anterior == 0:
                ratio = float("nan")
            else:
                ratio = (
                    mediana_actual
                    / mediana_anterior
                )

            ratios.append({
                "familia":
                    clave[0],

                "implementacion":
                    clave[1],

                "metodo_comun":
                    clave[2],

                "operacion":
                    clave[3],

                "escenario":
                    clave[4],

                "n_anterior":
                    anterior["n"],

                "n_actual":
                    actual["n"],

                "factor_n":
                    actual["n"]
                    / anterior["n"],

                "mediana_anterior_ns":
                    mediana_anterior,

                "mediana_actual_ns":
                    mediana_actual,

                "ratio_tiempo":
                    ratio
            })

    ratios.sort(
        key=lambda fila: (
            fila["familia"],
            fila["metodo_comun"],
            fila["implementacion"],
            fila["n_actual"]
        )
    )

    return ratios


def escribir_csv(
    ruta: Path,
    filas: list[dict]
) -> None:

    if not filas:
        raise ValueError(
            f"No existen datos para escribir en {ruta}"
        )

    ruta.parent.mkdir(
        parents=True,
        exist_ok=True
    )

    with ruta.open(
        "w",
        encoding="utf-8",
        newline=""
    ) as archivo:

        escritor = csv.DictWriter(
            archivo,
            fieldnames=list(
                filas[0].keys()
            )
        )

        escritor.writeheader()
        escritor.writerows(
            filas
        )


def obtener_serie(
    resumen: list[dict],
    familia: str,
    implementacion: str,
    metodo: str
) -> list[dict]:

    filas = [
        fila
        for fila in resumen
        if fila["familia"] == familia
        and fila["implementacion"]
        == implementacion
        and fila["metodo_comun"]
        == metodo
    ]

    filas.sort(
        key=lambda fila:
            fila["n"]
    )

    return filas


def usar_log_y(
    resumen: list[dict],
    familia: str,
    metodo: str
) -> bool:

    valores = [
        fila["mediana_ns"]
        for fila in resumen
        if fila["familia"] == familia
        and fila["metodo_comun"] == metodo
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
    familia: str,
    implementaciones: list[str],
    metodo: str,
    titulo: str
) -> None:

    for implementacion in implementaciones:

        serie = obtener_serie(
            resumen,
            familia,
            implementacion,
            metodo
        )

        if not serie:
            continue

        x = [
            fila["n"]
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

    ax.set_title(
        titulo
    )

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
        familia,
        metodo
    ):
        ax.set_yscale(
            "log"
        )

    ax.grid(
        True,
        which="both",
        alpha=0.25
    )


def graficar_familia(
    resumen: list[dict],
    familia: str,
    implementaciones: list[str],
    metodos: list[tuple[str, str]],
    titulo_general: str,
    nombre_archivo: str
) -> None:

    fig, axes = plt.subplots(
        2,
        3,
        figsize=(15, 9)
    )

    for ax, (
        metodo,
        titulo
    ) in zip(
        axes.flat,
        metodos
    ):

        dibujar_panel(
            ax,
            resumen,
            familia,
            implementaciones,
            metodo,
            titulo
        )

    handles, labels = (
        axes[0, 0]
        .get_legend_handles_labels()
    )

    fig.legend(
        handles,
        labels,
        loc="lower center",
        ncol=2,
        bbox_to_anchor=(
            0.5,
            -0.01
        )
    )

    fig.suptitle(
        titulo_general,
        fontsize=14
    )

    fig.tight_layout()

    fig.subplots_adjust(
        bottom=0.12
    )

    CARPETA_GRAFICOS.mkdir(
        parents=True,
        exist_ok=True
    )

    ruta = (
        CARPETA_GRAFICOS
        / nombre_archivo
    )

    fig.savefig(
        ruta,
        dpi=220,
        bbox_inches="tight"
    )

    plt.close(
        fig
    )

    print(
        f"Generado: {ruta}"
    )


def ratios_de(
    ratios: list[dict],
    familia: str,
    implementacion: str,
    metodo: str
) -> list[dict]:

    filas = [
        fila
        for fila in ratios
        if fila["familia"] == familia
        and fila["implementacion"]
        == implementacion
        and fila["metodo_comun"]
        == metodo
    ]

    filas.sort(
        key=lambda fila:
            fila["n_actual"]
    )

    return filas


def escribir_diagnostico_familia(
    salida,
    resumen: list[dict],
    ratios: list[dict],
    familia: str,
    implementaciones: list[str],
    metodos: list[tuple[str, str]]
) -> None:

    salida.write(
        familia.upper()
        + "\n"
    )

    salida.write(
        "=" * len(familia)
        + "\n\n"
    )

    for metodo, titulo in metodos:

        salida.write(
            titulo + "\n"
        )

        salida.write(
            "-" * len(titulo)
            + "\n"
        )

        for implementacion in implementaciones:

            serie = obtener_serie(
                resumen,
                familia,
                implementacion,
                metodo
            )

            if not serie:
                continue

            ultima = serie[-1]

            salida.write(
                f"{implementacion}: "
                f"mediana(n={ultima['n']}) = "
                f"{ultima['mediana_ns']:.3f} ns/op\n"
            )

            filas_ratio = ratios_de(
                ratios,
                familia,
                implementacion,
                metodo
            )

            texto_ratios = ", ".join(
                (
                    f"{fila['n_anterior']}"
                    f"->{fila['n_actual']}: "
                    f"{fila['ratio_tiempo']:.3f}"
                )
                for fila in filas_ratio
            )

            salida.write(
                "  ratios: "
                + texto_ratios
                + "\n"
            )

        salida.write(
            "\n"
        )


def escribir_diagnostico(
    resumen: list[dict],
    ratios: list[dict]
) -> None:

    SALIDA_DIAGNOSTICO.parent.mkdir(
        parents=True,
        exist_ok=True
    )

    with SALIDA_DIAGNOSTICO.open(
        "w",
        encoding="utf-8"
    ) as salida:

        salida.write(
            "DIAGNOSTICO EXPERIMENTAL - "
            "MÉTODOS EQUIVALENTES\n"
        )

        salida.write(
            "=" * 72
            + "\n\n"
        )

        salida.write(
            "Comparación List / Stack\n"
        )

        salida.write(
            "Lista seleccionada: "
            "ListaSimpleSinCola\n\n"
        )

        escribir_diagnostico_familia(
            salida,
            resumen,
            ratios,
            "stack",
            STACK_IMPLEMENTACIONES,
            STACK_METODOS
        )

        salida.write(
            "\n"
        )

        salida.write(
            "Comparación List / Queue\n"
        )

        salida.write(
            "Lista seleccionada: "
            "ListaSimpleConCola\n\n"
        )

        escribir_diagnostico_familia(
            salida,
            resumen,
            ratios,
            "queue",
            QUEUE_IMPLEMENTACIONES,
            QUEUE_METODOS
        )

    print(
        f"Generado: {SALIDA_DIAGNOSTICO}"
    )


def imprimir_resumen_general(
    datos: list[dict],
    resumen: list[dict],
    ratios: list[dict]
) -> None:

    print()
    print("=" * 68)

    print(
        " RESUMEN DEL PROCESAMIENTO - "
        "EQUIVALENCIAS"
    )

    print("=" * 68)

    print(
        f"Observaciones crudas: {len(datos)}"
    )

    print(
        f"Grupos resumidos: {len(resumen)}"
    )

    print(
        f"Ratios calculados: {len(ratios)}"
    )

    tamanos = sorted({
        int(fila["n"])
        for fila in datos
    })

    print(
        "Tamaños: "
        + ", ".join(
            str(n)
            for n in tamanos
        )
    )

    print(
        f"Resumen: {SALIDA_RESUMEN}"
    )

    print(
        f"Ratios: {SALIDA_RATIOS}"
    )

    print(
        f"Diagnóstico: {SALIDA_DIAGNOSTICO}"
    )

    print("=" * 68)


def main() -> None:

    datos = leer_datos()

    resumen = resumir(
        datos
    )

    ratios = calcular_ratios(
        resumen
    )

    escribir_csv(
        SALIDA_RESUMEN,
        resumen
    )

    escribir_csv(
        SALIDA_RATIOS,
        ratios
    )

    graficar_familia(
        resumen,
        "stack",
        STACK_IMPLEMENTACIONES,
        STACK_METODOS,
        "Métodos equivalentes: List vs Stack",
        "01_list_vs_stack.png"
    )

    graficar_familia(
        resumen,
        "queue",
        QUEUE_IMPLEMENTACIONES,
        QUEUE_METODOS,
        "Métodos equivalentes: List vs Queue",
        "02_list_vs_queue.png"
    )

    escribir_diagnostico(
        resumen,
        ratios
    )

    imprimir_resumen_general(
        datos,
        resumen,
        ratios
    )


if __name__ == "__main__":
    main()