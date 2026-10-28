# ===================================================================
# Supply Chain P2P - Stop Application & Containers
# Opreste aplicatia si containerele Docker
# ===================================================================
param(
    [switch]$All,
    [switch]$OnlyApp
)

Write-Host "=== Supply Chain P2P Stopper ===" -ForegroundColor Cyan

if ($OnlyApp) {
    Write-Host "Oprire doar container aplicatie (lasa baza de date si rabbitmq pornite)..." -ForegroundColor Yellow
    docker compose stop app
} elseif ($All) {
    Write-Host "Oprire si stergere toate containerele si volumele Docker..." -ForegroundColor Red
    docker compose down -v
} else {
    Write-Host "Oprire toate containerele Docker Compose (postgres, rabbitmq, app)..." -ForegroundColor Yellow
    docker compose down
}

Write-Host "Gata!" -ForegroundColor Green
