$ErrorActionPreference = "Stop"

Write-Host ""
Write-Host "========================================"
Write-Host " Compilando proyecto"
Write-Host "========================================"
Write-Host ""

if (Test-Path "out") {
    Remove-Item -Recurse -Force "out"
}

New-Item -ItemType Directory -Force "out" | Out-Null

$fuentes = (Get-ChildItem -Path "src" -Recurse -Filter "*.java").FullName

javac -d out $fuentes

if ($LASTEXITCODE -ne 0) {
    Write-Host ""
    Write-Host "ERROR: la compilacion fallo."
    exit $LASTEXITCODE
}

Write-Host "Compilacion correcta."
Write-Host ""

$pruebas = @(
    "estructuras.pruebas.PruebaListaSimpleSinCola",
    "estructuras.pruebas.PruebaListaSimpleConCola",
    "estructuras.pruebas.PruebaListaDobleSinCola",
    "estructuras.pruebas.PruebaListaDobleConCola",
    "estructuras.pruebas.PruebaPilas",
    "estructuras.pruebas.PruebaColas"
)

foreach ($prueba in $pruebas) {

    Write-Host "----------------------------------------"
    Write-Host "Ejecutando $prueba"
    Write-Host "----------------------------------------"

    java -cp out $prueba

    if ($LASTEXITCODE -ne 0) {
        Write-Host ""
        Write-Host "ERROR: fallo $prueba"
        exit $LASTEXITCODE
    }

    Write-Host ""
}

Write-Host "========================================"
Write-Host " TODAS LAS PRUEBAS FUERON SUPERADAS"
Write-Host "========================================"