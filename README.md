# ChanakyaIQ

## Overview
ChanakyaIQ is a **premium fintech stock‑simulator** built to let users practice Indian‑market trading without real capital risk. It combines a **Java 21 / Spring Boot 4** backend with a **React + Vite** frontend written in **TypeScript**. The UI follows a sleek dark‑mode, glass‑morphic design.

## Features
- Google OAuth2 login (session‑based, `JSESSIONID` cookie).
- Real‑time mock price feed (simulates Nifty 50 market hours).
- Portfolio management with holdings, cash balance, P&L calculations.
- Buy / sell workflow via a trading panel.
- Transaction history with timestamps.
- H2 file‑based database (`./db/chanakyaiq`).
- CORS configured to allow the frontend (`http://localhost:5173`) to send credentials.
- Fully typed React components (`.tsx`).

## Architecture
```
chanakyaiq/
├─ backend/            # Spring Boot application
│   ├─ src/main/java/com/chanakyaiq/
│   │   ├─ config/           # SecurityConfig, CORS
│   │   ├─ controller/       # Auth, Portfolio, Trade, Stock endpoints
│   │   ├─ model/            # JPA entities (User, Holding, Transaction)
│   │   └─ service/          # Upstox mock service, TradeService, PortfolioService
│   └─ src/main/resources/   # application.properties, static assets
│
├─ frontend/           # Vite + React (TypeScript)
│   ├─ src/
│   │   ├─ components/       # Dashboard, HoldingsTable, TradingPanel, TransactionHistory
│   │   ├─ types.ts          # TypeScript interfaces for domain models
│   │   ├─ App.tsx
│   │   └─ main.tsx
│   └─ vite.config.ts
│
└─ README.md
```

## Prerequisites
- **Java 21** (JDK installed and `JAVA_HOME` set).
- **Maven** (wrapper is included, use `./mvnw.cmd`).
- **Node .js ≥ 20** and **npm**.
- A **Google OAuth2** client (client‑id & client‑secret) configured with the redirect URI `http://localhost:8080/login/oauth2/code/google`.

## Setup
### 1. Clone the repository (if you haven’t already)
```cmd
git clone <repo‑url>
cd chanakyaiq
```
### 2. Configure Google OAuth
Create a file `backend/.env` (add to `.gitignore`) with:
```
GOOGLE_CLIENT_ID=your‑client‑id
GOOGLE_CLIENT_SECRET=your‑client‑secret
```
Alternatively, edit `backend/src/main/resources/application.properties` and replace the placeholder values.
### 3. Install frontend dependencies
```cmd
cd frontend
npm install
```
### 4. Build the frontend (optional – for production)
```cmd
npm run build
```
The static files will be placed in `frontend/dist`.

## Running the application (development mode)
### Backend
```cmd
cd backend
cmd /c .\mvnw.cmd spring-boot:run   # Windows
# or: ./mvnw spring-boot:run on Unix
```
The API will be available at `http://localhost:8080`.

### Frontend
```cmd
cd frontend
npm run dev
```
The Vite dev server will be at `http://localhost:5173`. Open that URL in a browser.

## Usage
1. Click **“Sign In with Google”** – you’ll be redirected to Google, log in, and be returned to the app.
2. After authentication you’ll see the dashboard with your cash balance.
3. Use the **Trading Panel** to place BUY/SELL orders; the mock Upstox service supplies price updates.
4. View holdings in the **Holdings Table** and past trades in **Transaction History**.

## Database
- H2 runs in file mode and stores data under `./db/chanakyaiq`.  
- Access the console at `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:file:./db/chanakyaiq`).

## Git repository
Only the `chanakyaiq` folder is a Git repo. Initialize it (if not already) with:
```cmd
cd C:\Users\Admin\IdeaProjects\chanakyaiq
git init
git add .
git commit -m "Initial commit – ChanakyaIQ setup"
```
Make sure there is **no** `.git` folder in the parent `IdeaProjects` directory.

## License
MIT – feel free to fork, modify, and deploy.

---
*Happy trading!*
