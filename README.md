# ChanakyaIQ

## Overview
ChanakyaIQ is a **premium fintech stock‑simulator** built to let users practice Indian‑market trading without real capital risk. It combines a **Java 21 / Spring Boot 4** backend with a **React + Vite** frontend written in **TypeScript**. The UI follows a sleek dark‑mode, glass‑morphic design.

## Features
- Google OAuth2 login (session‑based, `JSESSIONID` cookie).
- Real-time stock integration via Upstox API.
- Fully typed data models generated via OpenAPI generator plugin from schema specs.
- Portfolio management with holdings, cash balance, P&L calculations.
- 2-step search and buy/sell trading workflow via a clean trading panel.
- Transaction history with timestamps.
- H2 file‑based database (`./db/chanakyaiq`).
- CORS configured to allow the frontend (`http://localhost:5173`) to send credentials.
- Fully typed React components (`.tsx`).

## Architecture
```
chanakyaiq/
├─ backend/                           # Parent Maven module
│   ├─ chanakya-iq-api/               # OpenAPI module (generates model classes)
│   │   ├─ src/main/resources/
│   │   │   └─ upstox-api.yaml        # Upstox API OpenAPI specs
│   │   └─ pom.xml
│   │
│   ├─ chanakya-iq-service/           # Service module (app & business logic)
│   │   ├─ src/main/java/com/chanakyaiq/
│   │   │   ├─ config/                # Security, RestClientConfig, Properties
│   │   │   ├─ constants/             # Centralized AppConstants
│   │   │   ├─ controller/            # Controllers
│   │   │   ├─ dto/                   # DTO records
│   │   │   ├─ model/                 # JPA Entities
│   │   │   ├─ service/               # UpstoxService & trading logic
│   │   │   └─ util/                  # RestUtil helper
│   │   └─ pom.xml
│   │
│   └─ pom.xml                        # Parent POM (declares modules)
│
├─ frontend/                          # Vite + React (TypeScript)
│   ├─ src/
│   │   ├─ components/                # Dashboard, HoldingsTable, TradingPanel, TransactionHistory
│   │   ├─ types.ts                   # TypeScript interfaces for domain models
│   │   ├─ App.tsx
│   │   └─ main.tsx
│   └─ vite.config.ts
│
└─ README.md
```

## Prerequisites
- **Java 21** (JDK installed and `JAVA_HOME` set).
- **Maven** (wrapper is included, use `./mvnw.cmd` on Windows or `./mvnw` on Unix).
- **Node.js ≥ 20** and **npm**.
- A **Google OAuth2** client (client‑id & client‑secret) configured with the redirect URI `http://localhost:8080/login/oauth2/code/google`.

## Setup
### 1. Clone the repository (if you haven’t already)
```cmd
git clone <repo‑url>
cd chanakyaiq
```
### 2. Configure Google OAuth & Upstox Token
Create a file `backend/.env` (add to `.gitignore` if not present) with:
```
GOOGLE_CLIENT_ID=your‑client‑id
GOOGLE_CLIENT_SECRET=your‑client‑secret
UPSTOX_API_TOKEN=your-upstox-token
```
Alternatively, edit `backend/chanakya-iq-service/src/main/resources/application.properties` and replace the placeholder values.

### 3. Install frontend dependencies
```cmd
cd frontend
npm install
```

### 4. Build and Compile Backend Modules (Required)
Since the models are generated dynamically via the OpenAPI plugin, you must compile the backend parent project before running the application:
```cmd
cd backend
.\mvnw.cmd clean install -DskipTests
```

## Running the application (development mode)
### Backend
```cmd
cd backend/chanakya-iq-service
..\mvnw.cmd spring-boot:run
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
3. Use the **Trading Panel** to search for stocks; when you select a stock from the search dropdown, it fetches real-time market quotes from Upstox.
4. Place BUY/SELL orders; the portfolio computes your holdings and P&L in real time.
5. View holdings in the **Holdings Table** and past trades in **Transaction History**.

## Database
- H2 runs in file mode and stores data under `backend/chanakya-iq-service/db/chanakyaiq`.  
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
