# ===================================================================
# Supply Chain P2P - Application Runner
# Permite rularea rapida in oricare dintre optiunile suportate:
#   - Optiunea 2: Postgres & RabbitMQ in Docker + Aplicatia Spring Boot Local
#   - Optiunea 3: Full-Stack Docker Compose (App + Postgres + RabbitMQ)
#   - Optiunea 1: Postgres 17 Windows Local (5432) + Aplicatia Local
#   - Optiunea 4: In-Memory H2 (pentru teste rapide)
# ===================================================================
param(
    [Alias("Option", "Opt", "p")]
    [string]$Profile = ""
)

$env:JAVA_HOME = "C:\Users\bmugu\.jdks\ms-21.0.9"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"

if ([string]::IsNullOrWhiteSpace($Profile)) {
    Write-Host "`n========================================================" -ForegroundColor Cyan
    Write-Host "   Supply Chain P2P - Selecteaza Modul de Rulare       " -ForegroundColor Cyan
    Write-Host "========================================================" -ForegroundColor Cyan
    Write-Host " [2] Optiunea 2: Postgres & RabbitMQ Docker + App Local  (RECOMANDAT DEV/DEBUG)" -ForegroundColor Green
    Write-Host "     - PostgreSQL pe port 5433, RabbitMQ pe 5672/15672" -ForegroundColor Gray
    Write-Host "     - Aplicatia ruleaza pe Windows (port 8080), live debug, reload rapid" -ForegroundColor Gray
    Write-Host ""
    Write-Host " [3] Optiunea 3: Full-Stack Docker Compose (App + DB + RabbitMQ)" -ForegroundColor Magenta
    Write-Host "     - Toate cele 3 servicii ruleaza containerizat" -ForegroundColor Gray
    Write-Host "     - Accesibil pe http://localhost:8080" -ForegroundColor Gray
    Write-Host ""
    Write-Host " [1] Optiunea 1: PostgreSQL 17 Windows Local (Port 5432) + App Local" -ForegroundColor White
    Write-Host " [4] H2 In-Memory (fara nicio baza de date externa)" -ForegroundColor Yellow
    Write-Host "========================================================" -ForegroundColor Cyan
    $choice = Read-Host "Alege optiunea [2, 3, 1, 4] (implicit: 2)"
    if ([string]::IsNullOrWhiteSpace($choice)) {
        $Profile = "2"
    } else {
        $Profile = $choice.Trim()
    }
}

# Normalizare input
switch ($Profile.ToLower()) {
    { $_ -in @("2", "docker-db", "postgres-docker", "dev") } {
        Write-Host "`n>>> Pornire OPTIUNEA 2: PostgreSQL in Docker (5433) + RabbitMQ (5672) + App Local..." -ForegroundColor Green

        # 1. Daca containerul app (Optiunea 3) ruleaza, il oprim pentru a elibera portul 8080
        $runningApp = docker ps -q --filter "name=supplychain-app" 2>$null
        if ($runningApp) {
            Write-Host "Se opreste containerul 'supplychain-app' pentru a elibera portul 8080..." -ForegroundColor Yellow
            docker compose stop app | Out-Null
        }

        # 2. Ne asiguram ca postgres si rabbitmq ruleaza in Docker
        Write-Host "Pornire containere infrastructura (PostgreSQL & RabbitMQ)..." -ForegroundColor Cyan
        docker compose up -d postgres rabbitmq

        # 3. Pornim aplicatia local cu profilul postgres-docker
        Write-Host "Lansare aplicatie Spring Boot local (profil: postgres-docker)..." -ForegroundColor Green
        Write-Host "Swagger UI:       http://localhost:8080/swagger-ui.html" -ForegroundColor Yellow
        Write-Host "RabbitMQ Admin:   http://localhost:15672 (guest/guest)" -ForegroundColor Yellow
        Write-Host "Health Check:     http://localhost:8080/actuator/health" -ForegroundColor Yellow
        .\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=postgres-docker"
    }

    { $_ -in @("3", "full", "docker-full", "docker", "prod") } {
        Write-Host "`n>>> Pornire OPTIUNEA 3: Full-Stack in Docker Compose (App + PostgreSQL + RabbitMQ)..." -ForegroundColor Magenta
        docker compose up -d --build
        Write-Host "`nToate containerele sunt active in Docker!" -ForegroundColor Green
        docker compose ps
        Write-Host "`nLink-uri utile:" -ForegroundColor Cyan
        Write-Host "Swagger UI:       http://localhost:8080/swagger-ui.html" -ForegroundColor Yellow
        Write-Host "RabbitMQ Admin:   http://localhost:15672 (guest/guest)" -ForegroundColor Yellow
        Write-Host "Health Check:     http://localhost:8080/actuator/health" -ForegroundColor Yellow
        Write-Host "Pentru log-uri in timp real: docker compose logs -f app" -ForegroundColor Gray
    }

    { $_ -in @("1", "local", "postgres-local") } {
        Write-Host "`n>>> Pornire OPTIUNEA 1: PostgreSQL 17 Windows Local (Port 5432) + App Local..." -ForegroundColor Green
        .\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=postgres-local"
    }

    { $_ -in @("4", "h2", "test", "mem") } {
        Write-Host "`n>>> Pornire In-Memory H2..." -ForegroundColor Yellow
        .\mvnw.cmd spring-boot:run
    }

    default {
        Write-Host "Optiune necunoscuta: $Profile. Foloseste: 2, 3, 1, 4." -ForegroundColor Red
    }
}
