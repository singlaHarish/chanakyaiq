@echo off
echo Loading environment variables from .env file...

REM Check if .env file exists
if not exist ".env" (
    echo Warning: .env file not found!
    goto :start_app
)

REM Load environment variables from .env file
for /f "usebackq tokens=1,2 delims==" %%a in (".env") do (
    REM Skip lines starting with # (comments)
    echo %%a | findstr /r "^#" >nul
    if errorlevel 1 (
        set "%%a=%%b"
        echo   Set %%a
    )
)

:start_app
echo.
echo Starting Spring Boot application...
cd /d "%~dp0"
call "..\..\mvnw.cmd" spring-boot:run
