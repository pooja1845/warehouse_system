@echo off
echo ============================================
echo   Warehouse Management System - BUILD
echo ============================================
echo.

cd /d "%~dp0"

set MVN=%USERPROFILE%\maven\apache-maven-3.9.6\bin\mvn.cmd

echo [1/2] Cleaning previous build...
call "%MVN%" clean
if %ERRORLEVEL% neq 0 (
    echo ERROR: Clean failed!
    pause
    exit /b 1
)

echo.
echo [2/2] Compiling and packaging...
call "%MVN%" package -DskipTests
if %ERRORLEVEL% neq 0 (
    echo ERROR: Build failed! Check the output above for errors.
    pause
    exit /b 1
)

echo.
echo ============================================
echo   BUILD SUCCESSFUL!
echo   JAR: target\WarehouseSystem.jar
echo ============================================
echo.
pause
