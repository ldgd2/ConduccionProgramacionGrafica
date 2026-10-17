@echo off
title Simulador de Conduccion Urbana 3D
cd /d "%~dp0"

echo ========================================================
echo   Iniciando Simulador de Conduccion Urbana 3D
echo ========================================================
echo.

where mvn >nul 2>nul
if %errorlevel% neq 0 (
    echo [ERROR] No se encontro Maven en las variables de entorno PATH.
    echo Asegurate de tener Maven instalado y configurado.
    echo.
    pause
    exit /b 1
)

echo Compilando y ejecutando...
call mvn compile exec:exec

if %errorlevel% neq 0 (
    echo.
    echo [AVISO] El simulador termino con codigo de error %errorlevel%.
    pause
)
