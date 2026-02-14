@echo off
echo Compiling Ocean Resort System...
echo.

REM Create resort directory if it doesn't exist
if not exist "resort" mkdir resort

REM Copy all java files to resort package
copy *.java resort\

REM Compile all files
javac resort\*.java

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo Compilation Successful!
    echo ========================================
    echo.
    echo To run the application, use:
    echo java resort.OceanResortSystemMain
    echo.
) else (
    echo.
    echo ========================================
    echo Compilation Failed!
    echo ========================================
    echo Please check the error messages above.
)

pause
