# PowerShell Pre-Commit Quality Check
$ErrorActionPreference = "Stop"

Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host "[1/3] Compilare proiect si teste (Java 21)..." -ForegroundColor Yellow
Write-Host "==========================================================" -ForegroundColor Cyan
cmd.exe /c mvn.bat test-compile -B -q
if ($LASTEXITCODE -ne 0) { exit 1 }

Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host "[2/3] Verificare reguli de arhitectura (ArchUnit)..." -ForegroundColor Yellow
Write-Host "==========================================================" -ForegroundColor Cyan
cmd.exe /c mvn.bat test -Dtest="ro.uaic.feaa.sdbis.supplychain.architecture.**.*Test" -DfailIfNoTests=false -B -q
if ($LASTEXITCODE -ne 0) { exit 1 }

Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host "[3/3] Rulare teste unitare si servicii domeniu..." -ForegroundColor Yellow
Write-Host "==========================================================" -ForegroundColor Cyan
cmd.exe /c mvn.bat test -Dtest="ro.uaic.feaa.sdbis.supplychain.unit.**.*Test,ro.uaic.feaa.sdbis.supplychain.integration.**.*Test" -DfailIfNoTests=false -B -q
if ($LASTEXITCODE -ne 0) { exit 1 }

Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host "TOTUL ESTE VERDE! Poti face git commit si git push in siguranta." -ForegroundColor Green
Write-Host "==========================================================" -ForegroundColor Cyan
