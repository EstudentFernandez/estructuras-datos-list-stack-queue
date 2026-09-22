param(
    [switch]$Rapido
)

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

$fuentes = (
    Get-ChildItem -Path "src" -Recurse -Filter "*.java"
).FullName

javac -d out $fuentes

if ($LASTEXITCODE -ne 0) {
    Write-Host ""
    Write-Host "ERROR: fallo la compilacion."
    exit $LASTEXITCODE
}

Write-Host "Compilacion correcta."
Write-Host ""

if ($Rapido) {

    Write-Host "Ejecutando benchmark rapido..."
    Write-Host ""

    java -cp out estructuras.benchmark.BenchmarkListas --rapido

} else {

    Write-Host "Ejecutando benchmark completo..."
    Write-Host ""

    java -cp out estructuras.benchmark.BenchmarkListas
}

if ($LASTEXITCODE -ne 0) {
    Write-Host ""
    Write-Host "ERROR: fallo el benchmark."
    exit $LASTEXITCODE
}

Write-Host ""
Write-Host "Benchmark terminado correctamente."