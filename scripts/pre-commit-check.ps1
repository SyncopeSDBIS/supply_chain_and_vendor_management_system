# PowerShell Pre-Commit Quality Check
$ErrorActionPreference = "Stop"

# Detectare automata si portabila a JAVA_HOME (JDK 21)
if (-not $env:JAVA_HOME -or -not (Test-Path $env:JAVA_HOME)) {
    $candidateJdks = @(
        "$env:USERPROFILE\.jdks\ms-21*",
        "$env:USERPROFILE\.jdks\jdk-21*",
        "C:\Program Files\Java\jdk-21*",
        "C:\Program Files\Eclipse Adoptium\jdk-21*",
        "C:\Program Files\Microsoft\jdk-21*",
        "C:\Program Files\Amazon Corretto\jdk21*",
        "C:\Program Files\BellSoft\LibericaJDK-21*"
    )
    foreach ($cand in $candidateJdks) {
        $resolved = Resolve-Path $cand -ErrorAction SilentlyContinue | Select-Object -First 1
        if ($resolved -and (Test-Path $resolved.Path)) {
            $env:JAVA_HOME = $resolved.Path
            break
        }
    }
}
if ($env:JAVA_HOME) {
    $env:Path = "$env:JAVA_HOME\bin;$env:Path"
}

$mvnCmd = if ($IsWindows -or $env:OS -like "*Windows*") { ".\mvnw.cmd" } else { "./mvnw" }

Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host "[1/3] Compilare proiect si teste (Java 21)..." -ForegroundColor Yellow
Write-Host "==========================================================" -ForegroundColor Cyan
& $mvnCmd test-compile -B -q
if ($LASTEXITCODE -ne 0) { exit 1 }

Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host "[2/3] Verificare reguli de arhitectura (ArchUnit)..." -ForegroundColor Yellow
Write-Host "==========================================================" -ForegroundColor Cyan
& $mvnCmd test -Dtest="ro.uaic.feaa.sdbis.supplychain.architecture.**.*Test" -DfailIfNoTests=false -B -q
if ($LASTEXITCODE -ne 0) { exit 1 }

Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host "[3/3] Rulare teste unitare si servicii domeniu..." -ForegroundColor Yellow
Write-Host "==========================================================" -ForegroundColor Cyan
& $mvnCmd test -Dtest="ro.uaic.feaa.sdbis.supplychain.unit.**.*Test,ro.uaic.feaa.sdbis.supplychain.integration.**.*Test" -DfailIfNoTests=false -B -q
if ($LASTEXITCODE -ne 0) { exit 1 }

Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host "TOTUL ESTE VERDE! Poti face git commit si git push in siguranta." -ForegroundColor Green
Write-Host "==========================================================" -ForegroundColor Cyan
