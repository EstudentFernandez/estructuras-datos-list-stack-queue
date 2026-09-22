from __future__ import annotations

import csv
import math
from collections import defaultdict
from pathlib import Path
from statistics import median


ENTRADA = Path("resultados/pilas_colas_raw.csv")

SALIDA_RESUMEN = Path(
    "resultados/pilas_colas_resumen.csv"
)

SALIDA_RATIOS = Path(
    "resultados/pilas_colas_ratios.csv"
)


def percentil_lineal(
    valores: list[float],
    p: float
) -> float:

    if not valores:
        raise ValueError(
            "No se puede calcular un percentil sin datos."
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

    fraccion = posicion - inferior

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
            "El archivo no contiene observaciones."
        )

    return datos


def resumir(
    datos: list[dict]
) -> list[dict]:

    grupos = defaultdict(list)
    metadatos = {}

    for fila in datos:

        n_inicial = int(
        fila["n_inicial"]
)

        n_final = int(
            fila["n_final"]
        )

        if (
            fila["escenario"]
            == "secuencia_amortizada"
        ):
            n_efectivo = n_final
        else:
            n_efectivo = n_inicial

        clave = (
            fila["bloque"],
            fila["implementacion"],
            fila["metodo"],
            fila["escenario"],
            n_efectivo
        )

        tiempo = float(
            fila["ns_por_operacion"]
        )

        grupos[clave].append(
            tiempo
        )

        metadatos[clave] = {
            "bloque":
                fila["bloque"],

            "implementacion":
                fila["implementacion"],

            "metodo":
                fila["metodo"],

            "escenario":
                fila["escenario"],

            "semilla":
                fila["semilla"],

            "n_inicial":
                int(fila["n_inicial"]),

            "n_final":
                int(fila["n_final"]),

            "k":
                int(fila["k"]),

            "capacidad_antes":
                int(fila["capacidad_antes"]),

            "capacidad_despues":
                int(fila["capacidad_despues"]),

            "crecimiento":
                fila["crecimiento"],

            "n_efectivo":
                n_efectivo
        }

    resumen = []

    for clave, tiempos in grupos.items():

        q1 = percentil_lineal(
            tiempos,
            0.25
        )

        med = median(tiempos)

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
            fila["bloque"],
            fila["metodo"],
            fila["escenario"],
            fila["implementacion"],
            fila["n_efectivo"]
        )
    )

    return resumen


def calcular_ratios(
    resumen: list[dict]
) -> list[dict]:

    grupos = defaultdict(list)

    for fila in resumen:

        clave = (
            fila["bloque"],
            fila["implementacion"],
            fila["metodo"],
            fila["escenario"]
        )

        grupos[clave].append(
            fila
        )

    ratios = []

    for clave, filas in grupos.items():

        filas.sort(
            key=lambda fila:
                fila["n_efectivo"]
        )

        for anterior, actual in zip(
            filas,
            filas[1:]
        ):

            n_anterior = (
                anterior["n_efectivo"]
            )

            n_actual = (
                actual["n_efectivo"]
            )

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
                "bloque":
                    clave[0],

                "implementacion":
                    clave[1],

                "metodo":
                    clave[2],

                "escenario":
                    clave[3],

                "n_anterior":
                    n_anterior,

                "n_actual":
                    n_actual,

                "factor_n":
                    n_actual / n_anterior,

                "mediana_anterior_ns":
                    mediana_anterior,

                "mediana_actual_ns":
                    mediana_actual,

                "ratio_tiempo":
                    ratio
            })

    ratios.sort(
        key=lambda fila: (
            fila["bloque"],
            fila["metodo"],
            fila["escenario"],
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
            fieldnames=list(
                filas[0].keys()
            )
        )

        escritor.writeheader()
        escritor.writerows(filas)


def imprimir_resumen(
    datos: list[dict],
    resumen: list[dict],
    ratios: list[dict]
) -> None:

    print()
    print("=" * 64)
    print(
        " RESUMEN DEL PROCESAMIENTO - PILAS Y COLAS"
    )
    print("=" * 64)

    print(
        f"Observaciones crudas: {len(datos)}"
    )

    print(
        f"Grupos resumidos: {len(resumen)}"
    )

    print(
        f"Ratios calculados: {len(ratios)}"
    )

    print()

    for bloque in [
        "pilas",
        "colas"
    ]:

        implementaciones = sorted({
            fila["implementacion"]
            for fila in datos
            if fila["bloque"] == bloque
        })

        print(
            bloque.upper() + ":"
        )

        for implementacion in implementaciones:
            print(
                f"  - {implementacion}"
            )

        print()

    tamanos = sorted({
        int(fila["n_inicial"])
        for fila in datos
        if int(fila["n_inicial"]) > 0
    })

    print(
        "Tamaños iniciales no vacíos: "
        + ", ".join(
            str(n)
            for n in tamanos
        )
    )

    print()

    print(
        f"Resumen: {SALIDA_RESUMEN}"
    )

    print(
        f"Ratios: {SALIDA_RATIOS}"
    )

    print("=" * 64)


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

    imprimir_resumen(
        datos,
        resumen,
        ratios
    )


if __name__ == "__main__":
    main()