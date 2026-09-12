@echo off
cd /d "%~dp0"
set "PYTHON_PATH=%LOCALAPPDATA%\Programs\Python\Python312\python.exe"

if exist "%PYTHON_PATH%" (
    "%PYTHON_PATH%" main.py
) else (
    python main.py
)
pause
