@echo off
setlocal
cd /d "%~dp0"

set "JAR="
for %%f in (build\libs\*-all.jar) do set "JAR=%%f"

if not defined JAR (
    echo Shadow jar not found in build\libs\. Building...
    call gradlew.bat shadowJar
    if errorlevel 1 (
        echo.
        echo Build failed. See output above.
        pause
        exit /b 1
    )
    for %%f in (build\libs\*-all.jar) do set "JAR=%%f"
)

if not defined JAR (
    echo Build succeeded but no jar matching build\libs\*-all.jar was found.
    pause
    exit /b 1
)

echo Launching %JAR%
java -ea -jar "%JAR%" --developer-mode --debug

if errorlevel 1 pause
endlocal
