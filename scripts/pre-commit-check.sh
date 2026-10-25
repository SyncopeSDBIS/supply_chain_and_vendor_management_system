#!/usr/bin/env bash
set -e

echo "=========================================================="
echo "🔍 [1/3] Compilare proiect & teste (Java 21)..."
echo "=========================================================="
./mvnw test-compile -B -q

echo "=========================================================="
echo "🏛️ [2/3] Verificare reguli de arhitectură (ArchUnit)..."
echo "=========================================================="
./mvnw test -Dtest="ro.uaic.feaa.sdbis.supplychain.architecture.**.*Test" -DfailIfNoTests=false -B -q

echo "=========================================================="
echo "✅ [3/3] Rulare teste unitare & de integrare..."
echo "=========================================================="
./mvnw test -Dtest="ro.uaic.feaa.sdbis.supplychain.unit.**.*Test,ro.uaic.feaa.sdbis.supplychain.integration.**.*Test" -DfailIfNoTests=false -B -q

echo "=========================================================="
echo "🎉 TOTUL ESTE VERDE! Poți face git commit & git push în siguranță."
echo "=========================================================="
