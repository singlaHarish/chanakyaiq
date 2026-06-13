# Environment Variables Setup for Windows

This guide explains how to automatically load `.env` file values when starting the Spring Boot backend on Windows.

## ✅ Current Setup

The `.env` file is located at: `c:\Users\Admin\IdeaProjects\chanakyaiq\backend\chanakya-iq-service\.env`

Spring Boot 4.0.6 **automatically loads `.env` files** from the current working directory when the application starts.

## 🚀 Quick Start Options

### Option 1: Using CMD Batch File (Recommended for Windows)

Simply run:
```cmd
cd c:\Users\Admin\IdeaProjects\chanakyaiq\backend\chanakya-iq-service
start-backend.cmd
```

This script:
- Loads all variables from `.env` file
- Sets them as environment variables
- Starts the Spring Boot application with Maven

### Option 2: Using PowerShell Script

Run:
```powershell
cd c:\Users\Admin\IdeaProjects\chanakyaiq\backend\chanakya-iq-service
.\start-backend.ps1
```

### Option 3: Direct Maven (Spring Boot Auto-loads .env)

Since Spring Boot 4.x automatically loads `.env` files, you can simply run:
```cmd
cd c:\Users\Admin\IdeaProjects\chanakyaiq\backend\chanakya-iq-service
..\..\mvnw.cmd spring-boot:run
```

Spring Boot will automatically find and load the `.env` file in the same directory.

## 📝 .env File Format

Your `.env` file should contain:
```env
GOOGLE_CLIENT_ID=your-client-id-here
GOOGLE_CLIENT_SECRET=your-client-secret-here
UPSTOX_API_TOKEN=your-upstox-token-here
```

## 🔍 How application.properties References These

In `application.properties`, use:
```properties
spring.security.oauth2.client.registration.google.client-id=${GOOGLE_CLIENT_ID}
spring.security.oauth2.client.registration.google.client-secret=${GOOGLE_CLIENT_SECRET}
chanakyaiq.api.token=${UPSTOX_API_TOKEN}
```

Spring Boot will:
1. First check for `.env` file in current directory
2. Load variables from `.env`
3. Replace `${VARIABLE_NAME}` placeholders with values

## 🛠️ Troubleshooting

### Problem: Variables not loading
**Solution**: Ensure `.env` file is in the same directory where you run `mvnw.cmd`

### Problem: Permission denied on PowerShell script
**Solution**: Run:
```powershell
Set-ExecutionPolicy -Scope CurrentUser -ExecutionPolicy RemoteSigned
```

### Problem: Variables still not found
**Solution**: Manually set environment variables before running:
```cmd
set GOOGLE_CLIENT_ID=your-id
set GOOGLE_CLIENT_SECRET=your-secret
..\..\mvnw.cmd spring-boot:run
```

## ⚙️ IDE Configuration

### IntelliJ IDEA
1. Right-click on `ChanakyaIqApplication.java`
2. Select "Modify Run Configuration..."
3. In "Environment variables", click folder icon
4. Load from `.env` file or add manually

### VS Code
In `.vscode/launch.json`:
```json
{
  "type": "java",
  "request": "launch",
  "mainClass": "com.chanakyaiq.ChanakyaIqApplication",
  "envFile": "${workspaceFolder}/backend/chanakya-iq-service/.env"
}
```

## 🔒 Security Notes

- **Never commit `.env` to Git** - it's already in `.gitignore`
- Store sensitive credentials only in `.env`
- For production, use proper secret management (Azure Key Vault, AWS Secrets Manager, etc.)

## 📦 Dependencies Required

No additional dependencies needed! Spring Boot 4.x includes built-in `.env` file support.

---

**Last Updated**: June 13, 2026
