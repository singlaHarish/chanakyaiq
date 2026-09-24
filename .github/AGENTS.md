# AGENTS.md - ChanakyaIQ Project Context

> **📚 Additional Documentation:**
> - [OAuth2 Authentication Flow](./OAUTH2-FLOW.md) - Detailed explanation of Google OAuth2 implementation
> - [Documentation Index](./INDEX.md) - Complete file listing and quick navigation

## Project Overview

**ChanakyaIQ** is a premium fintech stock simulator application for the Indian stock market. It allows users to practice trading without real capital risk using virtual money (₹10,00,000 starting balance).

### Core Purpose
- Paper trading platform for Indian stocks (Nifty 50 and more)
- Real-time mock price simulation with market hours awareness
- Portfolio management with P&L calculations
- Transaction history and holdings tracking
- Educational tool for learning stock market trading
- Interactive charting with live market data

---

## Tech Stack

### Backend
- **Language**: Java 21
- **Framework**: Spring Boot 4.0.6 (spring-boot-starter-parent)
- **Build Tool**: Maven (wrapper included, multi-module structure)
  - **`chanakya-iq-api`**: Module for OpenAPI-based code generation containing model classes generated automatically from the API specifications.
  - **`chanakya-iq-service`**: Module for the core Spring Boot application containing business logic, REST APIs, and database integration.
- **Database**: H2 (file-based persistence at `backend/db/chanakyaiq`)
- **Security**: Spring Security with OAuth2
- **Authentication**: Google OAuth2 (session-based with JSESSIONID cookie)
- **WebSocket**: Java-WebSocket client for Upstox market data feed
- **SSE**: Server-Sent Events for real-time price updates and WebSocket status broadcasts
- **Logging**: Log4j2 (via `spring-boot-starter-log4j2`, default Spring Boot starter logging excluded)
- **Web Client**: Spring `RestClient` powered by Apache `HttpClient5` with connection pooling
- **ORM**: JPA/Hibernate
- **Additional Libraries**: 
  - Lombok (for reducing boilerplate in models and entities)
  - Spring Security OAuth2 Client (`spring-boot-starter-security-oauth2-client`)
  - OpenAPI Generator Maven Plugin (generates models from OpenAPI YAML specs)

### Frontend
- **Language**: TypeScript
- **Framework**: React 19.2.6
- **DOM Library**: react-dom 19.2.6
- **Build Tool**: Vite 8.0.12
- **Dev Server**: Vite (runs on port 5173)
- **Styling**: Custom CSS with dark-mode glass-morphic design
- **State Management**: React hooks (useState, useEffect)
- **Type Safety**: Strict TypeScript via defined interfaces in `types.ts`

### Development Environment
- **Backend Port**: 8080
- **Frontend Port**: 5173
- **CORS**: Configured to allow `http://localhost:5173` with credentials
- **Database Console**: `http://localhost:8080/h2-console`

---

## Project Architecture

### Directory Structure
```
chanakyaiq/
├── backend/                           # Parent Maven Project
│   ├── chanakya-iq-api/               # API Module (OpenAPI Model Generation)
│   │   ├── src/main/resources/
│   │   │   └── upstox-api.yaml        # Upstox API OpenAPI specification
│   │   └── pom.xml                    # API module build & generation config
│   │
│   ├── chanakya-iq-service/           # Service Module (Business Logic & App)
│   │   ├── src/main/java/com/chanakyaiq/
│   │   │   ├── ChanakyaIqApplication.java # Spring Boot entry point
│   │   │   ├── config/                # Security, RestClientConfig, Properties
│   │   │   ├── constants/             # Application constants
│   │   │   ├── controller/            # REST controllers (including SSE, WebSocket)
│   │   │   ├── dto/                   # DTO records
│   │   │   ├── model/                 # JPA database entities
│   │   │   ├── repository/            # Repository interfaces
│   │   │   ├── service/               # Services (api & impl)
│   │   │   ├── util/                  # Technical utilities (RestUtil)
│   │   │   └── websocket/             # WebSocket client for Upstox feed
│   │   │   └── cache/                 # In-memory price cache
│   │   ├── src/main/resources/
│   │   │   └── application.properties # Application settings
│   │   ├── db/                        # Local H2 database files
│   │   └── pom.xml                    # Service module build config
│   │
│   └── pom.xml                        # Parent POM (declares modules)
│
├── frontend/                        # Vite + React (TypeScript)
│   ├── src/
│   │   ├── components/
│   │   │   ├── Dashboard.tsx        # Portfolio metrics summary
│   │   │   ├── HoldingsTable.tsx    # Holdings list and P&L view
│   │   │   ├── TradingPanel.tsx     # Buy/sell stock panel
│   │   │   ├── TransactionHistory.jsx # Historical transaction logger
│   │   │   └── WebSocketStatus.tsx  # WebSocket connection status indicator
│   │   ├── App.tsx                  # Core React component with SSE handling
│   │   └── index.css                # Glass-morphism global styling
│   ├── package.json                 # Node dependencies
│   └── vite.config.ts              # Vite configuration
│
└── .github/                         # GitHub repository configuration
    └── AGENTS.md                    # This file
```

### Architecture Pattern
**Layered Architecture** with strict separation of technical and business concerns:
1. **Presentation Layer**: REST controllers (`@RestController`) mapping HTTP request/response DTOs.
2. **Service Layer**: Business logic (`@Service` interfaces + implementations).
3. **Utility Layer**: Helper components (e.g., `RestUtil` for REST call boilerplates) separating technical integrations from business rules.
4. **Data Access Layer**: JPA repositories (`@Repository`).
5. **Domain Layer**: Entity models (`@Entity`) and generated API models.

---

## Steering Files

### `.kiro/steering/development-guidelines.md`

**This file is automatically included in all Kiro interactions.**

Contains critical guidelines for development workflow:

- **Planning Before Implementation** - Never implement without approval
- **Step-by-step process** - Analyze → Plan → Reason → Get approval
- **When to ask questions** - 5 clear scenarios requiring clarification
- **Examples** - Good vs bad implementation approaches

**Key Rule:**
```
🚫 DO NOT IMPLEMENT WITHOUT APPROVAL

When you receive a feature request, bug fix, or implementation task, you MUST follow this process:

1. Analyze the request
2. Make a plan
3. Explain your reasoning
4. Get explicit approval
```

See [.kiro/steering/development-guidelines.md](../.kiro/steering/development-guidelines.md) for full details.

---

## Coding Standards

### Backend (Java/Spring Boot)
- **Logging**: Use Log4j2 via Lombok's `@Log4j2` annotation. Do not use `@Slf4j` or standard SLF4J logger imports directly. Exclude default Spring Boot logging in POM configs.
- **Properties Mapping**: Use `@ConfigurationProperties` configuration classes (such as `ChanakyaIqProperties` with prefix `chanakyaiq`) for application configuration. Avoid raw `@Value` injections in services.
- **Model and DTO Design**: Use auto-generated models from OpenAPI specs (defined under `chanakya-iq-api`) for mapping remote API responses. Use immutable **Java Records** for internal application DTOs and API responses returned to the frontend. Avoid mutable classes or static subclasses inside service implementations.
- **Interface-Driven Design**: Keep service contracts in `api` packages and business logic implementations in `impl` packages.
- **Dependency Injection**: Use constructor injection with Lombok's `@RequiredArgsConstructor` on all Spring beans.
- **Separation of Concerns**: Isolate technical client handling (such as API authorization headers, JSON parsers, RestClient requests, client connection pooling, error handling) in reusable utility classes (`com.chanakyaiq.util.RestUtil`). Keep service classes clean of HTTP code.
- **Constants**: Store all HTTP header values, URLs, endpoint paths, default config values, and segment indicators in a centralized constants file `com.chanakyaiq.constants.AppConstants`.

### Frontend (React/TypeScript)
- **Component Style**: Use Functional Components with React Hooks (`useState`, `useEffect`, etc.).
- **Type Safety**: Enforce strict TypeScript typing via defined interfaces (e.g., `Holding`, `Transaction` in `types.ts`). Note: Some legacy components might be `.jsx`, but new ones should be `.tsx`.
- **Design System**: Adhere to the established dark-mode glass-morphism aesthetic. Use unified styling rather than scattered inline styles.

---

## Key Features & Flows

### 1. Authentication Flow
- **OAuth2 with Google**: User clicks "Sign In with Google"
- Redirected to `http://localhost:8080/oauth2/authorization/google`
- After successful authentication, redirected back to `http://localhost:5173/`
- Session stored as `JSESSIONID` cookie (httpOnly, secure in production)
- User auto-provisioned on first login with ₹10,00,000 virtual cash

> **📖 Detailed Documentation:** See [OAUTH2-FLOW.md](./OAUTH2-FLOW.md) for complete flow diagrams, step-by-step process, security features, and troubleshooting guide.

### 2. Portfolio Management
- **Holdings**: Tracks user's stock positions (symbol, quantity, average price)
- **Real-time Valuation**: Fetches current prices via WebSocket/SSE with polling fallback
- **P&L Calculation**: 
  - Per-holding: `(currentPrice - avgPrice) * quantity`
  - Overall: `totalCurrentValue - totalInvested`
  - Percentage: `(profitLoss / invested) * 100`
- **Total Portfolio Value**: `sum of holdings value + cash balance`

### 3. Trading System
- **Buy Flow**:
  1. User searches for stock (e.g., "RELIANCE")
  2. Enters quantity and clicks Buy
  3. Backend validates: sufficient cash, valid quantity
  4. Deducts cash, updates/creates holding with weighted average price
  5. Logs transaction with timestamp
- **Sell Flow**:
  1. User selects holding or searches symbol
  2. Enters quantity and clicks Sell
  3. Backend validates: sufficient shares owned
  4. Adds proceeds to cash, reduces/deletes holding
  5. Logs transaction

### 4. Upstox API & Market Data Integration
- **Market Hours**: Mon-Fri, 9:15 AM - 3:30 PM IST.
- **Real-time Stock Search**: Calls Upstox API's `/instruments/search` endpoint to fetch matching stock tickers on user input, returning the instrument keys (e.g., `NSE_EQ|INE002A01018`).
- **Live Quotes**: Calls Upstox API's `/market-quote/quotes` endpoint with the selected instrument key to retrieve live OHLC, last traded price, net changes, and trading volume.
- **Historical Data**: Fetches real historical candles from Upstox API (30 days of 1-day candles) via `/v3/historical-candle/{instrumentKey}/days/1/{to_date}/{from_date}` endpoint.
- **WebSocket Integration**: Real-time WebSocket feed from Upstox with automatic reconnection, subscription management, and REST API fallback when WebSocket unavailable. Auto-subscribes to user holdings on login.
- **SSE Broadcasting**: Server-Sent Events for real-time price updates and WebSocket status broadcasts to all connected clients.

---

## API Endpoints

### Authentication
- `GET /api/auth/status` - Check authentication status, auto-provision user if first login (PUBLIC)
  - Returns: `{ authenticated, userId, email, cashBalance }`
- `POST /api/auth/logout` - Clear session and logout (AUTH REQUIRED)

### Portfolio
- `GET /api/portfolio/summary` - Portfolio summary with holdings, P&L, balances (AUTH REQUIRED)
  - Returns: `{ holdings, cashBalance, totalInvested, totalCurrentValue, overallProfitLoss, overallProfitLossPercent, totalPortfolioValue }`
- `GET /api/portfolio/transactions` - Transaction history ordered by timestamp desc (AUTH REQUIRED)
  - Enriches transactions with stock names via Upstox API lookup
  - Returns: Array of transactions with `{ id, symbol, name, quantity, price, type, timestamp }`

### Trading
- `POST /api/trade/buy` - Execute market buy order (AUTH REQUIRED)
  - Body: `{ "symbol": "RELIANCE", "quantity": 10 }`
  - Returns: `{ success, message, symbol, executedPrice, quantity, totalBill, updatedCashBalance }`
- `POST /api/trade/sell` - Execute market sell order (AUTH REQUIRED)
  - Body: `{ "symbol": "RELIANCE", "quantity": 5 }`
  - Returns: `{ success, message, symbol, executedPrice, quantity, totalBill, updatedCashBalance }`

### Stocks
- `GET /api/stocks/search?query=<term>` - Search stocks by symbol/name (PUBLIC)
  - Returns: Array of `{ instrumentKey, tradingSymbol, name }`
- `GET /api/stocks/price/{symbol}` - Get current price and details (PUBLIC)
  - Returns: `{ instrumentKey, symbol, name, lastPrice, netChange, changePercent, open, high, low, close, volume, averagePrice, isMarketOpen }`
- `GET /api/stocks/history/{symbol}` - Get historical prices for charting (PUBLIC)
  - Returns: Array of `{ timestamp, open, high, low, close, volume }`

### Server-Sent Events (SSE)
- `GET /api/sse/connect` - Establish SSE connection for real-time updates (PUBLIC)
  - Returns: Event stream with `connected`, `websocket_status`, `price_update` events
- `POST /api/sse/disconnect` - Close SSE connection (PUBLIC)
  - Body: `{ emitterId: string }`
- `GET /api/sse/status` - Check SSE connection status (PUBLIC)
  - Returns: `{ connected, activeConnections }`

### WebSocket Management
- `GET /api/websocket/status` - Check WebSocket connection status (PUBLIC)
  - Returns: `{ connected, mode (LIVE|FALLBACK_REST), subscribedInstruments }`
- `GET /api/websocket/prices` - Get all cached prices (PUBLIC)
  - Returns: Map of instrumentKey -> stock details
- `POST /api/websocket/subscribe` - Subscribe to instrument prices (PUBLIC)
  - Body: `{ instrumentKeys: string[] }`
- `GET /api/websocket/subscribe/{instrumentKey}` - Subscribe to single instrument (PUBLIC)

---

## Database Schema

### Users Table
```sql
CREATE TABLE users (
    id VARCHAR(255) PRIMARY KEY,      -- Google OAuth2 sub claim
    email VARCHAR(255),
    cash_balance DECIMAL(19,2)
);
```

### Holdings Table
```sql
CREATE TABLE holdings (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id VARCHAR(255),
    symbol VARCHAR(50),
    quantity INTEGER,
    average_price DECIMAL(19,2),
    UNIQUE(user_id, symbol)           -- One row per user-symbol pair
);
```

### Transactions Table
```sql
CREATE TABLE transactions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id VARCHAR(255),
    symbol VARCHAR(50),
    type VARCHAR(10),                 -- 'BUY' or 'SELL'
    quantity INTEGER,
    price DECIMAL(19,2),
    timestamp TIMESTAMP
);
```

---

## Configuration Files

### Backend Configuration
**`application.properties`** (located in `backend/chanakya-iq-service/src/main/resources/`):
- Server port: 8080
- H2 file database: `jdbc:h2:file:./db/chanakyaiq`
- JPA: `ddl-auto=update` (auto-creates tables)
- H2 Console: enabled at `/h2-console`
- OAuth2: Google client credentials (configured with default placeholders or loadable from `.env` or system variables)
- Upstox API: `chanakyaiq.api.token` holds the bearer token for market data requests
- REST client properties: `rest.client.timeout.connect`, `rest.client.timeout.read`, `rest.client.pool.max-total`, etc.

**`.env`** (backend root):
Contains sensitive local environment configs:
```
GOOGLE_CLIENT_ID=<your-client-id>
GOOGLE_CLIENT_SECRET=<your-client-secret>
UPSTOX_API_TOKEN=<your-upstox-token>
```

### Frontend Configuration
**`package.json`**:
- Scripts: `dev`, `build`, `lint`, `preview`
- React 19.2.6, Vite 8.0.12, TypeScript 6.0.3

---

## Development Workflow

### Running the Application

**Backend**:
1. First, build the parent project and compile all Maven modules (this compiles dependencies and runs the OpenAPI generator to produce the models under `chanakya-iq-api`):
   ```cmd
   cd backend
   .\mvnw.cmd clean install -DskipTests
   ```
2. Run the main Spring Boot service module:
   ```cmd
   cd chanakya-iq-service
   ..\mvnw.cmd spring-boot:run
   ```
   (Alternatively, execute from the parent backend folder: `.\mvnw.cmd -pl chanakya-iq-service spring-boot:run`)
   
Backend runs at `http://localhost:8080`

**Frontend**:
```cmd
cd frontend
npm install
npm run dev
```
Frontend runs at `http://localhost:5173`

### Building for Production
**Backend**:
```cmd
cd backend
.\mvnw.cmd clean package -DskipTests
java -jar chanakya-iq-service/target/chanakya-iq-service-0.0.1-SNAPSHOT.jar
```

**Frontend**:
```cmd
cd frontend
npm run build
# Output: frontend/dist/
```

### Database Access
- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:file:./db/chanakyaiq`
- Username: `sa`
- Password: (empty)

---

## Security Configuration

> **🔐 Deep Dive:** For complete OAuth2 authentication flow with diagrams, see [OAUTH2-FLOW.md](./OAUTH2-FLOW.md)

### CORS Settings
- **Allowed Origin**: `http://localhost:5173` (frontend dev server)
- **Allowed Methods**: GET, POST, PUT, DELETE, OPTIONS
- **Allowed Headers**: Authorization, Cache-Control, Content-Type
- **Credentials**: Enabled (required for session cookies)

### Protected Routes
- All `/api/**` routes except:
  - `/api/auth/status` (PUBLIC)
  - `/api/stocks/**` (PUBLIC)
  - `/api/sse/**` (PUBLIC for SSE connections)
  - `/api/websocket/**` (PUBLIC for WebSocket management)
  - `/h2-console/**` (PUBLIC for dev)

### Authentication
- OAuth2 with Google (OpenID Connect)
- Success redirect: `http://localhost:5173/`
- Session-based (JSESSIONID cookie)
- Logout: `/api/auth/logout`
- **Auto-provisioning**: New users get ₹10,00,000 starting balance
- **Session Management**: Server-side sessions (in-memory by default)

**Key Security Features:**
- HttpOnly cookies (prevents XSS attacks)
- CSRF protection (disabled for REST API)
- Credential-based CORS (allows cross-origin cookie sharing)
- Google-managed authentication (no password storage)

---

## Business Logic Highlights

### Trade Execution (TradeServiceImpl)
- **Validation**: Checks quantity > 0, user exists, sufficient funds/shares
- **Transactional**: All DB operations in single transaction
- **Average Price Calculation**: Weighted average when buying additional shares
  ```java
  newAvg = (oldQty * oldAvg + buyQty * buyPrice) / (oldQty + buyQty)
  ```
- **Error Handling**: Throws exceptions with user-friendly messages

### Portfolio Aggregation (PortfolioServiceImpl)
- Fetches all holdings for user
- Gets current prices from UpstoxService
- Calculates per-holding and overall P&L
- Returns comprehensive summary with:
  - Holdings details array
  - Cash balance
  - Total invested
  - Total current value
  - Overall P&L (absolute & percentage)
  - Total portfolio value

### Upstox Market Data (UpstoxServiceImpl)
- **Market Hours**: Uses `ZonedDateTime` with IST timezone (Mon-Fri, 9:15 AM - 3:30 PM IST).
- **Real Upstox Integration**: Connects to the Upstox API via Spring's `RestClient` and maps responses to OpenAPI models.
- **2-Step Workflow**: Performs live stock searches to retrieve instrument keys, and then requests real-time quotes using those keys.
- **Real Historical Data**: Fetches actual historical candles from Upstox API (30 days of 1-day candles), NOT simulated data.

---

## UI/UX Design

### Design System
- **Theme**: Dark mode with glass-morphic cards
- **Colors**: 
  - Background: Dark gradients
  - Cards: Semi-transparent with backdrop blur
  - Profit: Green (#00ff88)
  - Loss: Red (#ff4444)
- **Typography**: Modern sans-serif, hierarchy with font sizes
- **Layout**: 
  - Left column: Dashboard, Holdings, Transactions (scrollable)
  - Right column: Trading Panel (fixed)

### Key Components
1. **Dashboard**: 3-card summary (total value, invested, current, cash)
2. **HoldingsTable**: Real-time holdings grid with P&L indicators, "My Holdings" section
3. **TradingPanel**: Stock search, interactive chart, buy/sell buttons with tooltips
4. **TransactionHistory**: Chronological log with type badges (Order History)

### Real-time Updates (WebSocket + SSE + Polling Fallback)
The application uses a hybrid real-time architecture:

1. **WebSocket Connection** (`UpstoxWebSocketManager`):
   - Connects to Upstox market data feed API for live prices
   - Auto-subscribes to user's holdings on login
   - Handles reconnection with exponential backoff (max 5 attempts)
   - Falls back to REST API when WebSocket unavailable

2. **Server-Sent Events (SSE)** (`SseController`):
   - Real-time broadcast of price updates to all connected clients
   - WebSocket connection status updates (LIVE vs FALLBACK_REST mode)
   - 30-second timeout for SSE connections

3. **Polling Fallback**:
   - Portfolio data polled every 5 seconds when SSE/WebSocket unavailable
   - Enabled automatically when SSE fails or WebSocket disconnects
   - Disabled when WebSocket/SSE reconnected

4. **Frontend Components**:
   - `WebSocketStatus.tsx` shows connection status to users
   - `App.tsx` manages SSE connection and polling intervals
   - Real-time price updates reflected immediately in holdings

### TradingPanel Features
- **Search**: Live search with debouncing (300ms), minimum 2 characters
- **Chart**: Interactive SVG line chart with hover tooltips, gradient fill
- **Order Validation**: 
  - Buy: Checks cash balance with dynamic tooltip
  - Sell: Checks owned shares with dynamic tooltip
- **Success Banner**: Detailed formatted feedback after order execution
- **Error Handling**: User-friendly error messages for trade failures

---

---

## Common Development Tasks

### Adding a New Stock Endpoint
1. Add method to `UpstoxService` interface in `service/api/`
2. Implement in `UpstoxServiceImpl`
3. Add endpoint in `StockController`
4. Update frontend API calls if needed

### Adding a New Portfolio Metric
1. Update `PortfolioServiceImpl.getPortfolioSummary()`
2. Modify return map to include new metric
3. Update frontend `Dashboard.tsx` to display metric

### Modifying Authentication
1. Edit `SecurityConfig.java` for OAuth2 settings
2. Update `.env` file with new credentials
3. Adjust CORS if changing frontend URL

### Database Schema Changes
1. Modify entity classes (`@Entity` models)
2. Hibernate auto-updates schema (ddl-auto=update)
3. For production: Use Liquibase/Flyway migrations

### Adding New UI Features
1. Add TypeScript interface in `types.ts` if new data types needed
2. Create new component in `frontend/src/components/`
3. Add route/section in `App.tsx` if needed
4. Follow existing styling conventions (glass-morphism)

### Updating Trading Logic
1. Modify `TradeService` interface in `service/api/`
2. Implement in `TradeServiceImpl`
3. Update controller endpoint if behavior changes
4. Test with frontend `TradingPanel.tsx`

---

## Testing

### Current Test Setup
- Basic test class: `DemoApplicationTests.java`
- Test dependencies: Spring Boot Test, Security Test, OAuth2 Test

### Running Tests
```cmd
cd backend
.\mvnw.cmd test
```

---

## Known Limitations & Future Enhancements

### Current Limitations
1. **WebSocket Binary Data**: WebSocket feed is connected but binary Protobuf data parsing requires implementation for full real-time capability (currently falls back to REST API).
2. **Single User Session**: No multi-session support per user.
3. **No Order Types**: Only market orders (no limit/stop orders).
4. **No Watchlist**: Can't save favorite stocks.
5. **No Advanced Charts**: Basic historical line charts, no candlesticks.
6. **WebSocket Reconnection Delay**: Max 5 reconnect attempts with exponential backoff before falling back to REST.

### Potential Enhancements
- Complete WebSocket Protobuf parsing for full real-time price updates
- Advanced order types (limit, stop-loss, bracket)
- Watchlist and alerts
- Technical indicators (RSI, MACD, Moving Averages)
- Portfolio analytics and reports
- Social features (leaderboards, strategy sharing)
- Mobile app (React Native)
- Persistent stock database (cache real market data)

---

## Troubleshooting

### Backend Won't Start
- Check Java 21 is installed: `java -version`
- Verify `GOOGLE_CLIENT_ID` and `GOOGLE_CLIENT_SECRET` in `.env`
- Check if port 8080 is available
- Delete `./db/chanakyaiq.mv.db` and restart if corrupted

### Frontend Can't Connect
- Verify backend is running on port 8080
- Check CORS configuration in `SecurityConfig.java`
- Clear browser cookies (JSESSIONID)
- Check browser console for CORS errors

### OAuth2 Redirect Issues
- Verify Google OAuth2 redirect URI: `http://localhost:8080/login/oauth2/code/google`
- Check frontend success redirect: `http://localhost:5173/`
- Ensure `.env` credentials are correct

### Database Issues
- Access H2 console: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:file:./db/chanakyaiq`
- Check `application.properties` for correct config

---

## Git Repository

### Repository Info
- **Location**: `C:\Users\Admin\IdeaProjects\chanakyaiq`
- **Remote**: Not yet configured (local only)
- **Ignored Files**: 
  - `backend/.env` (sensitive credentials)
  - `backend/target/` (build artifacts)
  - `frontend/node_modules/` (dependencies)
  - `frontend/dist/` (build output)
  - `backend/db/` (database files)

### Branching Strategy (Recommended)
- `main`: Production-ready code
- `develop`: Development branch
- `feature/*`: Feature branches
- `bugfix/*`: Bug fix branches

---

## 📚 Documentation Index

This file provides the complete project context. For specialized topics, refer to these detailed guides:

### Core Documentation
- **[AGENTS.md](./AGENTS.md)** _(this file)_ - Complete project overview, architecture, and development guide
- **[OAUTH2-FLOW.md](./OAUTH2-FLOW.md)** - Detailed OAuth2 authentication flow with diagrams

### Quick References
| Topic | Location | Description |
|-------|----------|-------------|
| Authentication Flow | [OAUTH2-FLOW.md](./OAUTH2-FLOW.md) | Step-by-step OAuth2 flow, security features, troubleshooting |
| API Endpoints | [Above](#api-endpoints) | Complete REST API reference |
| Database Schema | [Above](#database-schema) | Table structures and relationships |
| Security Config | [Above](#security-configuration) | CORS, protected routes, session management |
| Business Logic | [Above](#business-logic-highlights) | Trade execution, portfolio calculations, market simulation |
| Development Setup | [Above](#development-workflow) | Running, building, and testing the application |
| Troubleshooting | [Above](#troubleshooting) | Common issues and solutions |

### Configuration Files
- `.env` - Environment variables (Google OAuth2 credentials, API tokens)
- `application.properties` - Spring Boot configuration
- `SecurityConfig.java` - Security and OAuth2 setup
- `package.json` - Frontend dependencies and scripts
- `pom.xml` - Backend Maven dependencies

### Key Concepts
1. **Session-Based Auth**: Uses JSESSIONID cookies, not JWT tokens ([details](./OAUTH2-FLOW.md#session-management))
2. **Auto-Provisioning**: New users automatically created with ₹10L balance ([details](#1-authentication-flow))
3. **Mock Market Data**: In-memory price simulation with IST market hours ([details](#4-mock-market-data-upstoxservice))
4. **Weighted Average**: Holdings use weighted average price on buy ([details](#trade-execution-tradeserviceimpl))
5. **Real-time Updates**: Frontend polls portfolio data every 5 seconds ([details](#real-time-updates))

---

## Contact & Contribution

### Project Maintainers
- Developer: Harish Singla

### Contributing Guidelines
1. Fork the repository
2. Create feature branch: `git checkout -b feature/amazing-feature`
3. Commit changes: `git commit -m 'Add amazing feature'`
4. Push to branch: `git push origin feature/amazing-feature`
5. Open Pull Request

---

## License
MIT License - See project README for details

---

**Last Updated**: September 18, 2026  
**Project Version**: 0.0.1-SNAPSHOT  
**Status**: Active Development
