# Load environment variables from .env file
if (Test-Path ".env") {
    Write-Host "Loading environment variables from .env file..." -ForegroundColor Green
    Get-Content ".env" | ForEach-Object {
        if ($_ -match '^\s*([^#][^=]+)=(.*)$') {
            $name = $matches[1].Trim()
            $value = $matches[2].Trim()
            [Environment]::SetEnvironmentVariable($name, $value, "Process")
            Write-Host "  Set $name" -ForegroundColor Cyan
        }
    }
} else {
    Write-Host "Warning: .env file not found!" -ForegroundColor Yellow
}

# Start Spring Boot application
Write-Host "`nStarting Spring Boot application..." -ForegroundColor Green
Set-Location -Path $PSScriptRoot
& "..\..\mvnw.cmd" spring-boot:run
