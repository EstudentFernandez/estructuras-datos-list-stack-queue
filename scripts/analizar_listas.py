from __future__ import annotations

import csv
import math
from collections import defaultdict
from pathlib import Path
from statistics import median


ENTRADA = Path("resultados/listas_raw.csv")
SALIDA_RESUMEN = Path("resultados/listas_resumen.csv")
SALIDA_RATIOS = Path("resultados/listas_ratios.csv")


def percentil_lineal(valores: list[float], p: float) -> float:
    """
    Calcula un percentil mediante interpolación lineal.

    p debe estar entre 0 y 1.
    """
    if not valores:
        raise ValueError("No se puede calcular un percentil sin datos.")

    ordenados = sorted(valores)

    if len(ordenados) == 1:
        return ordenados[0]

    posicion = (len(ordenados) - 1) * p

    inferior = math.floor(posicion)
    superior = math.ceil(posicion)

    if inferior == superior:
        return ordenados[inferior]

    fraccion = posicion - inferior

    return (
        ordenados[inferior]
        + fraccion
        * (ordenados[superior] - ordenados[inferior])
    )


def leer_datos() -> list[dict]:
    if not ENTRADA.exists():
        raise FileNotFoundError(
            f"No se encontró el archivo {ENTRADA}"
        )

    with ENTRADA.open(
        "r",
        encoding="utf-8",
        newline=""
    ) as archivo:

        lector = csv.DictReader(archivo)

        datos = list(lector)

    if not datos:
        raise ValueError(
            "El archivo de resultados no contiene observaciones."
        )

    return datos


def resumir(datos: list[dict]) -> list[dict]:
    """
    Agrupa por:
        implementación
        método
        escenario
        n

    y calcula:
        cantidad de observaciones
        mínimo
        Q1
        mediana
        Q3
        máximo
        IQR
        media
    """

    grupos = defaultdict(list)

    metadatos = {}

    for fila in datos:

        clave = (
            fila["implementacion"],
            fila["metodo"],
            fila["escenario"],
            int(fila["n_inicial"])
        )

        tiempo = float(fila["ns_por_operacion"])

        grupos[clave].append(tiempo)

        metadatos[clave] = {
            "bloque": fila["bloque"],
            "implementacion": fila["implementacion"],
            "metodo": fila["metodo"],
            "escenario": fila["escenario"],
            "semilla": fila["semilla"],
            "n_inicial": int(fila["n_inicial"]),
            "k": int(fila["k"])
        }

    resumen = []

    for clave, tiempos in grupos.items():

        q1 = percentil_lineal(tiempos, 0.25)
        med = median(tiempos)
        q3 = percentil_lineal(tiempos, 0.75)

        promedio = sum(tiempos) / len(tiempos)

        registro = {
            **metadatos[clave],
            "observaciones": len(tiempos),
            "min_ns": min(tiempos),
            "q1_ns": q1,
            "mediana_ns": med,
            "q3_ns": q3,
            "max_ns": max(tiempos),
            "iqr_ns": q3 - q1,
            "media_ns": promedio
        }

        resumen.append(registro)

    resumen.sort(
        key=lambda r: (
            r["metodo"],
            r["escenario"],
            r["implementacion"],
            r["n_inicial"]
        )
    )

    return resumen


def calcular_ratios(resumen: list[dict]) -> list[dict]:
    """
    Calcula la razón entre medianas de tamaños
    consecutivos para la misma combinación:

        implementación + método + escenario

    ratio = mediana(n_actual) / mediana(n_anterior)
    """

    grupos = defaultdict(list)

    for fila in resumen:

        clave = (
            fila["implementacion"],
            fila["metodo"],
            fila["escenario"]
        )

        grupos[clave].append(fila)

    resultados = []

    for clave, filas in grupos.items():

        filas.sort(
            key=lambda r: r["n_inicial"]
        )

        for anterior, actual in zip(
            filas,
            filas[1:]
        ):

            mediana_anterior = anterior["mediana_ns"]
            mediana_actual = actual["mediana_ns"]

            if mediana_anterior == 0:
                ratio = float("nan")
            else:
                ratio = (
                    mediana_actual
                    / mediana_anterior
                )

            resultados.append({
                "implementacion": clave[0],
                "metodo": clave[1],
                "escenario": clave[2],
                "n_anterior": anterior["n_inicial"],
                "n_actual": actual["n_inicial"],
                "factor_n":
                    actual["n_inicial"]
                    / anterior["n_inicial"],
                "mediana_anterior_ns":
                    mediana_anterior,
                "mediana_actual_ns":
                    mediana_actual,
                "ratio_tiempo": ratio
            })

    resultados.sort(
        key=lambda r: (
            r["metodo"],
            r["escenario"],
            r["implementacion"],
            r["n_actual"]
        )
    )

    return resultados


def escribir_csv(
    ruta: Path,
    filas: list[dict]
) -> None:

    if not filas:
        raise ValueError(
            f"No hay filas para escribir en {ruta}"
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
            fieldnames=list(filas[0].keys())
        )

        escritor.writeheader()
        escritor.writerows(filas)


def imprimir_resumen_general(
    datos: list[dict],
    resumen: list[dict],
    ratios: list[dict]
) -> None:

    print()
    print("=" * 60)
    print(" RESUMEN DEL PROCESAMIENTO")
    print("=" * 60)

    print(
        f"Observaciones crudas: {len(datos)}"
    )

    print(
        f"Grupos resumidos: {len(resumen)}"
    )

    print(
        f"Ratios calculados: {len(ratios)}"
    )

    implementaciones = sorted({
        fila["implementacion"]
        for fila in datos
    })

    metodos = sorted({
        fila["metodo"]
        for fila in datos
    })

    tamanos = sorted({
        int(fila["n_inicial"])
        for fila in datos
    })

    print()
    print("Implementaciones:")

    for implementacion in implementaciones:
        print(
            f"  - {implementacion}"
        )

    print()
    print(
        "Métodos: "
        + ", ".join(metodos)
    )

    print()
    print(
        "Tamaños: "
        + ", ".join(
            str(n)
            for n in tamanos
        )
    )

    print()
    print(
        f"Resumen guardado en: {SALIDA_RESUMEN}"
    )

    print(
        f"Ratios guardados en: {SALIDA_RATIOS}"
    )

    print("=" * 60)


def main() -> None:

    datos = leer_datos()

    resumen = resumir(datos)

    ratios = calcular_ratios(resumen)

    escribir_csv(
        SALIDA_RESUMEN,
        resumen
    )

    escribir_csv(
        SALIDA_RATIOS,
        ratios
    )

    imprimir_resumen_general(
        datos,
        resumen,
        ratios
    )


if __name__ == "__main__":
    main()