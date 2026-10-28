# ===================================================================
# Database Manager: Gestioneaza PostgreSQL Local si Docker
# ===================================================================
param(
    [ValidateSet("status", "local-create-db", "docker-up", "docker-down")]
    [string]$Action = "status"
)

Write-Host "=== Supply Chain P2P Database Manager ===" -ForegroundColor Cyan

switch ($Action) {
    "status" {
        Write-Host "`n1. PostgreSQL Local (Windows Service):" -ForegroundColor Yellow
        $svc = Get-Service *postgres* -ErrorAction SilentlyContinue
        if ($svc) {
            Write-Host "   Service: $($svc.Name) - $($svc.Status)" -ForegroundColor Green
        } else {
            Write-Host "   Nu este gasit serviciul Windows postgres." -ForegroundColor Red
        }

        $port5432 = Get-NetTCPConnection -LocalPort 5432 -ErrorAction SilentlyContinue
        if ($port5432) {
            Write-Host "   Port 5432: ACTIV (Asculta conexiuni - PID $($port5432[0].OwningProcess))" -ForegroundColor Green
        } else {
            Write-Host "   Port 5432: INACTIV" -ForegroundColor Gray
        }

        Write-Host "`n2. Docker Container PostgreSQL:" -ForegroundColor Yellow
        try {
            $dockerPs = docker ps --filter "name=supplychain-postgres" --format "{{.Names}} - {{.Status}} - Ports: {{.Ports}}" 2>$null
            if ($dockerPs) {
                Write-Host "   Container: $dockerPs" -ForegroundColor Green
            } else {
                Write-Host "   Containerul supplychain-postgres nu este pornit." -ForegroundColor Gray
            }
        } catch {
            Write-Host "   Docker daemon nu raspunde (porneste Docker Desktop)." -ForegroundColor Red
        }

        $port5433 = Get-NetTCPConnection -LocalPort 5433 -ErrorAction SilentlyContinue
        if ($port5433) {
            Write-Host "   Port 5433 (Docker Host Port): ACTIV" -ForegroundColor Green
        } else {
            Write-Host "   Port 5433 (Docker Host Port): INACTIV" -ForegroundColor Gray
        }
    }

    "docker-up" {
        Write-Host "Pornire containere PostgreSQL si RabbitMQ prin Docker Compose..." -ForegroundColor Yellow
        docker compose up -d postgres rabbitmq
        Write-Host "Containere pornite! PostgreSQL asculta pe localhost:5433 (User: supplychain, Pass: supplychain_secret)" -ForegroundColor Green
    }

    "docker-down" {
        Write-Host "Oprire containere Docker..." -ForegroundColor Yellow
        docker compose down
    }

    "local-create-db" {
        Write-Host "Creare baza de date supplychain_db in PostgreSQL local..." -ForegroundColor Yellow
        $createdbPath = "C:\Program Files\PostgreSQL\17\bin\createdb.exe"
        if (Test-Path $createdbPath) {
            & $createdbPath -U postgres supplychain_db
        } else {
            Write-Host "Nu s-a gasit createdb.exe la calea standard. Creaza baza 'supplychain_db' din pgAdmin." -ForegroundColor Red
        }
    }
}
