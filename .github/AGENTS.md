# AGENTS.md - ChanakyaIQ Project Context

## Project Overview

**ChanakyaIQ** is a premium fintech stock simulator application for the Indian stock market. It allows users to practice trading without real capital risk using virtual money (₹10,00,000 starting balance).

### Core Purpose
- Paper trading platform for Indian stocks (Nifty 50 and more)
- Real-time mock price simulation with market hours awareness
- Portfolio management with P&L calculations
- Transaction history and holdings tracking
- Educational tool for learning stock market trading

---

## Tech Stack

### Backend
- **Language**: Java 21
- **Framework**: Spring Boot 4.0.6
- **Build Tool**: Maven (wrapper included)
- **Database**: H2 (file-based persistence at `./db/chanakyaiq`)
- **Security**: Spring Security with OAuth2
- **Authentication**: Google OAuth2 (session-based with JSESSIONID cookie)
- **ORM**: JPA/Hibernate
- **Additional Libraries**: 
  - Lombok (for reducing boilerplate)
  - H2 Console (database management UI)

### Frontend
- **Language**: TypeScript
- **Framework**: React 19.2.6
- **Build Tool**: Vite 8.0.12
- **Dev Server**: Vite (runs on port 5173)
- **Styling**: Custom CSS with dark-mode glass-morphic design
- **State Management**: React hooks (useState, useEffect)

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
├── backend/                           # Spring Boot application
│   ├── src/main/java/com/chanakyaiq/
│   │   ├── config/
│   │   │   └── SecurityConfig.java   # Security, OAuth2, CORS configuration
│   │   ├── controller/               # REST API endpoints
│   │   │   ├── UserController.java   # Auth status, user provisioning
│   │   │   ├── PortfolioController.java  # Portfolio summary, transactions
│   │   │   ├── TradeController.java  # Buy/sell operations
│   │   │   └── StockController.java  # Stock search, prices, details
│   │   ├── model/                    # JPA entities
│   │   │   ├── User.java            # User (id, email, cashBalance)
│   │   │   ├── Holding.java         # Stock holdings (userId, symbol, quantity, avgPrice)
│   │   │   └── Transaction.java     # Trade history (userId, symbol, type, quantity, price, timestamp)
│   │   ├── repository/              # JPA repositories
│   │   │   ├── UserRepository.java
│   │   │   ├── HoldingRepository.java
│   │   │   └── TransactionRepository.java
│   │   └── service/
│   │       ├── api/                 # Service interfaces
│   │       │   ├── UpstoxService.java      # Market data operations
│   │       │   ├── TradeService.java       # Trade execution logic
│   │       │   └── PortfolioService.java   # Portfolio aggregation
│   │       └── impl/                # Service implementations
│   │           ├── UpstoxServiceImpl.java  # Mock market data simulator
│   │           ├── TradeServiceImpl.java   # Buy/sell logic
│   │           └── PortfolioServiceImpl.java # Portfolio calculations
│   ├── src/main/resources/
│   │   └── application.properties   # App config, database, OAuth2 settings
│   ├── db/                          # H2 database files (file-based persistence)
│   ├── pom.xml                      # Maven dependencies
│   └── .env                         # Google OAuth2 credentials
│
├── frontend/                        # Vite + React (TypeScript)
│   ├── src/
│   │   ├── components/
│   │   │   ├── Dashboard.tsx        # Portfolio summary cards (total value, P&L, cash)
│   │   │   ├── HoldingsTable.tsx    # Current holdings grid with live prices
│   │   │   ├── TradingPanel.tsx     # Stock search, buy/sell interface
│   │   │   └── TransactionHistory.jsx # Past transactions log
│   │   ├── App.tsx                  # Main app component (auth, routing, state)
│   │   ├── main.tsx                 # React entry point
│   │   └── index.css                # Global styles (dark glass-morphic theme)
│   ├── package.json                 # NPM dependencies
│   └── vite.config.ts              # Vite build configuration
│
└── .github/                         # GitHub configuration
    └── AGENTS.md                    # This file
```

### Architecture Pattern
**Layered Architecture** with clear separation of concerns:
1. **Presentation Layer**: REST controllers (`@RestController`)
2. **Service Layer**: Business logic (`@Service` interfaces + implementations)
3. **Data Access Layer**: JPA repositories (`@Repository`)
4. **Domain Layer**: Entity models (`@Entity`)

---

## Key Features & Flows

### 1. Authentication Flow
- **OAuth2 with Google**: User clicks "Sign In with Google"
- Redirected to `http://localhost:8080/oauth2/authorization/google`
- After successful authentication, redirected back to `http://localhost:5173/`
- Session stored as `JSESSIONID` cookie (httpOnly, secure in production)
- User auto-provisioned on first login with ₹10,00,000 virtual cash

### 2. Portfolio Management
- **Holdings**: Tracks user's stock positions (symbol, quantity, average price)
- **Real-time Valuation**: Fetches current prices every 5 seconds (polling)
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

### 4. Mock Market Data (UpstoxService)
- **Market Hours**: Mon-Fri, 9:15 AM - 3:30 PM IST
- **Price Simulation**: 
  - Random walk with ±0.2% tick changes during market hours
  - Prices frozen outside market hours
  - Initial prices: ₹100-₹2000 randomly generated
- **Stock Database**: In-memory map, dynamically creates stocks on-demand
- **Historical Data**: Generates 20 data points for charting (simulated)

---

## API Endpoints

### Authentication
- `GET /api/auth/status` - Check auth status, auto-provision user (PUBLIC)
- `POST /api/auth/logout` - Clear session and logout

### Portfolio
- `GET /api/portfolio/summary` - Portfolio summary with holdings, P&L, balances (AUTH REQUIRED)
- `GET /api/portfolio/transactions` - Transaction history ordered by timestamp desc (AUTH REQUIRED)

### Trading
- `POST /api/trade/buy` - Execute market buy order (AUTH REQUIRED)
  - Body: `{ "symbol": "RELIANCE", "quantity": 10 }`
- `POST /api/trade/sell` - Execute market sell order (AUTH REQUIRED)
  - Body: `{ "symbol": "RELIANCE", "quantity": 5 }`

### Stocks
- `GET /api/stocks/search?query=<term>` - Search stocks by symbol/name (PUBLIC)
- `GET /api/stocks/price/{symbol}` - Get current price and details (PUBLIC)
- `GET /api/stocks/history/{symbol}` - Get historical prices for charting (PUBLIC)

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
**`application.properties`**:
- Server port: 8080
- H2 file database: `jdbc:h2:file:./db/chanakyaiq`
- JPA: `ddl-auto=update` (auto-creates tables)
- H2 Console: enabled at `/h2-console`
- OAuth2: Google client credentials from `.env` file

**`.env`** (backend root):
```
GOOGLE_CLIENT_ID=<your-client-id>
GOOGLE_CLIENT_SECRET=<your-client-secret>
```

### Frontend Configuration
**`package.json`**:
- Scripts: `dev`, `build`, `lint`, `preview`
- React 19.2.6, Vite 8.0.12, TypeScript 6.0.3

---

## Development Workflow

### Running the Application

**Backend**:
```cmd
cd backend
mvnw.cmd spring-boot:run
```
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
mvnw.cmd clean package
java -jar target/chanakyaiq-0.0.1-SNAPSHOT.jar
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

### CORS Settings
- **Allowed Origin**: `http://localhost:5173` (frontend dev server)
- **Allowed Methods**: GET, POST, PUT, DELETE, OPTIONS
- **Allowed Headers**: Authorization, Cache-Control, Content-Type
- **Credentials**: Enabled (required for session cookies)

### Protected Routes
- All `/api/**` routes except:
  - `/api/auth/status` (PUBLIC)
  - `/api/stocks/price/**` (PUBLIC)
  - `/h2-console/**` (PUBLIC for dev)

### Authentication
- OAuth2 with Google (OpenID Connect)
- Success redirect: `http://localhost:5173/`
- Session-based (JSESSIONID cookie)
- Logout: `/api/auth/logout`

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

### Mock Market (UpstoxServiceImpl)
- **Market Hours**: Uses `ZonedDateTime` with IST timezone
- **Price Volatility**: Random walk ±0.2% per tick
- **Dynamic Stock Creation**: Auto-creates stocks if not in database
- **Thread-Safe**: Synchronized methods for price updates

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
2. **HoldingsTable**: Real-time holdings grid with P&L indicators
3. **TradingPanel**: Stock search, price chart, buy/sell buttons
4. **TransactionHistory**: Chronological log with type badges

### Real-time Updates
- Portfolio data polled every 5 seconds when user is authenticated
- Prices update automatically during market hours
- UI reflects changes without page refresh

---

## Common Development Tasks

### Adding a New Stock Endpoint
1. Add method to `UpstoxService` interface
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

---

## Testing

### Current Test Setup
- Basic test class: `DemoApplicationTests.java`
- Test dependencies: Spring Boot Test, Security Test, OAuth2 Test

### Running Tests
```cmd
cd backend
mvnw.cmd test
```

---

## Known Limitations & Future Enhancements

### Current Limitations
1. **Mock Data Only**: No real market data integration (Upstox API planned)
2. **Single User Session**: No multi-session support per user
3. **In-Memory Stocks**: Stock database resets on restart
4. **Basic Price Simulation**: Simple random walk, no realistic patterns
5. **No Order Types**: Only market orders (no limit/stop orders)
6. **No Watchlist**: Can't save favorite stocks
7. **No Advanced Charts**: Basic historical data, no candlesticks

### Potential Enhancements
- Real Upstox API integration
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

## Contact & Contribution

### Project Maintainers
- Developer: Admin (local development)

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

**Last Updated**: June 7, 2026
**Project Version**: 0.0.1-SNAPSHOT
**Status**: Active Development
