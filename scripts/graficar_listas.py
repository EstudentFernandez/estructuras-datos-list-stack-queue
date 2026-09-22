from __future__ import annotations

import csv
from collections import defaultdict
from pathlib import Path

import matplotlib.pyplot as plt


ENTRADA_RESUMEN = Path("resultados/listas_resumen.csv")
ENTRADA_RATIOS = Path("resultados/listas_ratios.csv")

CARPETA_GRAFICOS = Path("graficos/listas")
SALIDA_DIAGNOSTICO = Path("resultados/listas_diagnostico.txt")


IMPLEMENTACIONES = [
    "ListaSimpleSinCola",
    "ListaSimpleConCola",
    "ListaDobleSinCola",
    "ListaDobleConCola",
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
        return list(csv.DictReader(archivo))


def convertir_resumen(filas: list[dict]) -> list[dict]:
    resultado = []

    for fila in filas:
        resultado.append({
            **fila,
            "n_inicial": int(fila["n_inicial"]),
            "mediana_ns": float(fila["mediana_ns"]),
            "q1_ns": float(fila["q1_ns"]),
            "q3_ns": float(fila["q3_ns"]),
            "iqr_ns": float(fila["iqr_ns"]),
        })

    return resultado


def convertir_ratios(filas: list[dict]) -> list[dict]:
    resultado = []

    for fila in filas:
        resultado.append({
            **fila,
            "n_anterior": int(fila["n_anterior"]),
            "n_actual": int(fila["n_actual"]),
            "factor_n": float(fila["factor_n"]),
            "mediana_anterior_ns":
                float(fila["mediana_anterior_ns"]),
            "mediana_actual_ns":
                float(fila["mediana_actual_ns"]),
            "ratio_tiempo":
                float(fila["ratio_tiempo"]),
        })

    return resultado


def obtener_serie(
    resumen: list[dict],
    implementacion: str,
    metodo: str,
    escenario: str
) -> list[dict]:

    filas = [
        fila
        for fila in resumen
        if fila["implementacion"] == implementacion
        and fila["metodo"] == metodo
        and fila["escenario"] == escenario
    ]

    filas.sort(
        key=lambda fila: fila["n_inicial"]
    )

    return filas


def configurar_eje(
    ax,
    titulo: str,
    usar_log_y: bool = False
) -> None:

    ax.set_title(titulo)
    ax.set_xlabel("Tamaño inicial n")
    ax.set_ylabel("Tiempo mediano (ns/op)")

    ax.set_xscale("log")

    if usar_log_y:
        ax.set_yscale("log")

    ax.grid(
        True,
        which="both",
        alpha=0.25
    )


def debe_usar_log_y(
    resumen: list[dict],
    metodo: str,
    escenario: str
) -> bool:

    valores = [
        fila["mediana_ns"]
        for fila in resumen
        if fila["metodo"] == metodo
        and fila["escenario"] == escenario
        and fila["mediana_ns"] > 0
    ]

    if not valores:
        return False

    minimo = min(valores)
    maximo = max(valores)

    if minimo <= 0:
        return False

    return maximo / minimo >= 50


def dibujar_panel(
    ax,
    resumen: list[dict],
    metodo: str,
    escenario: str,
    titulo: str
) -> None:

    for implementacion in IMPLEMENTACIONES:

        serie = obtener_serie(
            resumen,
            implementacion,
            metodo,
            escenario
        )

        if not serie:
            continue

        x = [
            fila["n_inicial"]
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

    configurar_eje(
        ax,
        titulo,
        debe_usar_log_y(
            resumen,
            metodo,
            escenario
        )
    )


def guardar_figura(
    nombre: str,
    figura
) -> None:

    CARPETA_GRAFICOS.mkdir(
        parents=True,
        exist_ok=True
    )

    ruta = CARPETA_GRAFICOS / nombre

    figura.tight_layout()

    figura.savefig(
        ruta,
        dpi=220,
        bbox_inches="tight"
    )

    plt.close(figura)

    print(f"Generado: {ruta}")


def graficar_extremos(resumen: list[dict]) -> None:

    paneles = [
        ("pushFront", "general", "PushFront"),
        ("pushBack", "general", "PushBack"),
        ("popFront", "general", "PopFront"),
        ("popBack", "general", "PopBack"),
        ("topFront", "general", "TopFront"),
        ("topBack", "general", "TopBack"),
    ]

    fig, axes = plt.subplots(
        2,
        3,
        figsize=(15, 9)
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
            metodo,
            escenario,
            titulo
        )

    handles, labels = axes[0, 0].get_legend_handles_labels()

    fig.legend(
        handles,
        labels,
        loc="lower center",
        ncol=2,
        bbox_to_anchor=(0.5, -0.01)
    )

    fig.suptitle(
        "Listas enlazadas: operaciones sobre extremos",
        fontsize=14
    )

    fig.subplots_adjust(
        bottom=0.13
    )

    guardar_figura(
        "01_extremos.png",
        fig
    )


def graficar_find(resumen: list[dict]) -> None:

    escenarios = [
        ("inicio", "Find: elemento al inicio"),
        ("medio", "Find: elemento en el medio"),
        ("final", "Find: elemento al final"),
        ("ausente", "Find: elemento ausente"),
    ]

    fig, axes = plt.subplots(
        2,
        2,
        figsize=(12, 9)
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
            "find",
            escenario,
            titulo
        )

    handles, labels = axes[0, 0].get_legend_handles_labels()

    fig.legend(
        handles,
        labels,
        loc="lower center",
        ncol=2,
        bbox_to_anchor=(0.5, -0.01)
    )

    fig.suptitle(
        "Listas enlazadas: búsqueda por valor",
        fontsize=14
    )

    fig.subplots_adjust(
        bottom=0.13
    )

    guardar_figura(
        "02_find.png",
        fig
    )


def graficar_operacion_por_nodo(
    resumen: list[dict],
    metodo: str,
    nombre_archivo: str,
    titulo_general: str
) -> None:

    escenarios = [
        (
            "inicio_nodo_prelocalizado",
            "Nodo al inicio"
        ),
        (
            "medio_nodo_prelocalizado",
            "Nodo en el medio"
        ),
        (
            "final_nodo_prelocalizado",
            "Nodo al final"
        ),
    ]

    fig, axes = plt.subplots(
        1,
        3,
        figsize=(15, 5)
    )

    for ax, (
        escenario,
        descripcion
    ) in zip(
        axes,
        escenarios
    ):
        dibujar_panel(
            ax,
            resumen,
            metodo,
            escenario,
            f"{metodo}: {descripcion}"
        )

    handles, labels = axes[0].get_legend_handles_labels()

    fig.legend(
        handles,
        labels,
        loc="lower center",
        ncol=2,
        bbox_to_anchor=(0.5, -0.04)
    )

    fig.suptitle(
        titulo_general,
        fontsize=14
    )

    fig.subplots_adjust(
        bottom=0.20
    )

    guardar_figura(
        nombre_archivo,
        fig
    )


def graficar_auxiliares(resumen: list[dict]) -> None:

    paneles = [
        ("size", "no_vacia", "Size"),
        ("isEmpty", "no_vacia", "IsEmpty"),
    ]

    fig, axes = plt.subplots(
        1,
        2,
        figsize=(11, 5)
    )

    for ax, (
        metodo,
        escenario,
        titulo
    ) in zip(
        axes,
        paneles
    ):
        dibujar_panel(
            ax,
            resumen,
            metodo,
            escenario,
            titulo
        )

    handles, labels = axes[0].get_legend_handles_labels()

    fig.legend(
        handles,
        labels,
        loc="lower center",
        ncol=2,
        bbox_to_anchor=(0.5, -0.05)
    )

    fig.suptitle(
        "Listas enlazadas: operaciones auxiliares",
        fontsize=14
    )

    fig.subplots_adjust(
        bottom=0.20
    )

    guardar_figura(
        "06_auxiliares.png",
        fig
    )


def mediana_para(
    resumen: list[dict],
    implementacion: str,
    metodo: str,
    escenario: str,
    n: int
) -> float | None:

    for fila in resumen:
        if (
            fila["implementacion"] == implementacion
            and fila["metodo"] == metodo
            and fila["escenario"] == escenario
            and fila["n_inicial"] == n
        ):
            return fila["mediana_ns"]

    return None


def ratios_para(
    ratios: list[dict],
    implementacion: str,
    metodo: str,
    escenario: str
) -> list[dict]:

    filas = [
        fila
        for fila in ratios
        if fila["implementacion"] == implementacion
        and fila["metodo"] == metodo
        and fila["escenario"] == escenario
    ]

    filas.sort(
        key=lambda fila: fila["n_actual"]
    )

    return filas


def escribir_diagnostico(
    resumen: list[dict],
    ratios: list[dict]
) -> None:

    casos = [
        (
            "pushBack",
            "general",
            "Efecto de tail en PushBack"
        ),
        (
            "popBack",
            "general",
            "Efecto combinado de tail y prev en PopBack"
        ),
        (
            "topBack",
            "general",
            "Efecto de tail en TopBack"
        ),
        (
            "find",
            "final",
            "Búsqueda al final"
        ),
        (
            "find",
            "ausente",
            "Búsqueda ausente"
        ),
        (
            "erase",
            "medio_nodo_prelocalizado",
            "Erase con nodo prelocalizado"
        ),
        (
            "addBefore",
            "medio_nodo_prelocalizado",
            "AddBefore con nodo prelocalizado"
        ),
        (
            "addAfter",
            "medio_nodo_prelocalizado",
            "AddAfter con nodo prelocalizado"
        ),
    ]

    tamanos = sorted({
        fila["n_inicial"]
        for fila in resumen
    })

    n_max = max(tamanos)

    with SALIDA_DIAGNOSTICO.open(
        "w",
        encoding="utf-8"
    ) as salida:

        salida.write(
            "DIAGNOSTICO EXPERIMENTAL - LISTAS\n"
        )

        salida.write(
            "=" * 70 + "\n\n"
        )

        salida.write(
            f"Tamaño máximo analizado: {n_max}\n\n"
        )

        for metodo, escenario, titulo in casos:

            salida.write(
                titulo + "\n"
            )

            salida.write(
                "-" * len(titulo) + "\n"
            )

            for implementacion in IMPLEMENTACIONES:

                mediana = mediana_para(
                    resumen,
                    implementacion,
                    metodo,
                    escenario,
                    n_max
                )

                salida.write(
                    f"{implementacion}: "
                    f"mediana(n={n_max}) = "
                    f"{mediana:.3f} ns/op\n"
                )

                filas_ratio = ratios_para(
                    ratios,
                    implementacion,
                    metodo,
                    escenario
                )

                cadena_ratios = ", ".join(
                    (
                        f"{fila['n_anterior']}"
                        f"->{fila['n_actual']}: "
                        f"{fila['ratio_tiempo']:.3f}"
                    )
                    for fila in filas_ratio
                )

                salida.write(
                    "  ratios: "
                    + cadena_ratios
                    + "\n"
                )

            salida.write("\n")

    print(
        f"Generado: {SALIDA_DIAGNOSTICO}"
    )


def main() -> None:

    resumen = convertir_resumen(
        leer_csv(
            ENTRADA_RESUMEN
        )
    )

    ratios = convertir_ratios(
        leer_csv(
            ENTRADA_RATIOS
        )
    )

    graficar_extremos(resumen)

    graficar_find(resumen)

    graficar_operacion_por_nodo(
        resumen,
        "erase",
        "03_erase.png",
        "Listas enlazadas: Erase con nodo prelocalizado"
    )

    graficar_operacion_por_nodo(
        resumen,
        "addBefore",
        "04_addBefore.png",
        "Listas enlazadas: AddBefore con nodo prelocalizado"
    )

    graficar_operacion_por_nodo(
        resumen,
        "addAfter",
        "05_addAfter.png",
        "Listas enlazadas: AddAfter con nodo prelocalizado"
    )

    graficar_auxiliares(resumen)

    escribir_diagnostico(
        resumen,
        ratios
    )

    print()
    print(
        "Gráficos de listas generados correctamente."
    )


if __name__ == "__main__":
    main()