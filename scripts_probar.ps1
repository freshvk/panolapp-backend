$ErrorActionPreference = "Stop"
foreach ($s in "usuarios","equipos","prestamos","reportes") {
    Write-Host "=== pruebas de $s-service ==="
    docker run --rm -v "${PWD}\$s-service:/app" -w /app maven:3.9-eclipse-temurin-21 mvn -B -q test
    if ($LASTEXITCODE -ne 0) { throw "Fallaron las pruebas de $s-service" }
}
Write-Host "Todas las pruebas pasaron"
