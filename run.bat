@echo off
echo ============================================
echo   Warehouse Management System - RUN
echo ============================================
echo.

cd /d "%~dp0"

set MVN=%USERPROFILE%\maven\apache-maven-3.9.6\bin\mvn.cmd
set JAR_PATH=target\WarehouseSystem.jar

if not exist "%JAR_PATH%" (
    echo JAR not found. Building project first...
    echo.
    call "%MVN%" clean package -DskipTests
    if %ERRORLEVEL% neq 0 (
        echo ERROR: Build failed! Please check errors above.
        pause
        exit /b 1
    )
    echo.
)

echo Starting Warehouse Management System...
echo.
java -jar "%JAR_PATH%"

if %ERRORLEVEL% neq 0 (
    echo.
    echo ERROR: Application exited with an error (code %ERRORLEVEL%).
    pause
)
