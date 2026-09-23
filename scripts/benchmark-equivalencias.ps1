param(
    [switch]$Rapido
)

$ErrorActionPreference = "Stop"

if (Test-Path "out") {
    Remove-Item -Recurse -Force "out"
}

New-Item -ItemType Directory -Force "out" | Out-Null

$fuentes = (
    Get-ChildItem -Path "src" -Recurse -Filter "*.java"
).FullName

javac -d out $fuentes

if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR: fallo la compilacion."
    exit $LASTEXITCODE
}

Write-Host "Compilacion correcta."

if ($Rapido) {

    java -cp out `
        estructuras.benchmark.BenchmarkEquivalencias `
        --rapido

} else {

    java -cp out `
        estructuras.benchmark.BenchmarkEquivalencias
}

if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR: fallo el benchmark."
    exit $LASTEXITCODE
}

Write-Host "Benchmark terminado correctamente."