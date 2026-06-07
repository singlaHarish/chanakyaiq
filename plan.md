# ChanakyaIQ - Stock Market Simulator Project Plan

This document serves as the project plan and architecture design space for ChanakyaIQ.

## System Architecture

### 1. Database Schema (H2 Database)
* **User**:
  * `id`: String (Google OAuth2 user ID)
  * `email`: String (User's email)
  * `cashBalance`: Decimal (Default starting balance, e.g., ₹10,00,000)
* **Holding**:
  * `id`: Long (Primary Key)
  * `userId`: String (Foreign Key to User)
  * `symbol`: String (Stock symbol, e.g., RELIANCE)
  * `quantity`: Integer (Number of shares owned)
  * `averagePrice`: Decimal (Average buy price)
* **Transaction (Audit Trail)**:
  * `id`: Long (Primary Key)
  * `userId`: String (Foreign Key to User)
  * `symbol`: String (Stock symbol)
  * `type`: String (BUY or SELL)
  * `quantity`: Integer (Number of shares)
  * `price`: Decimal (Execution price)
  * `timestamp`: LocalDateTime (Time of transaction)

### 2. Core Modules
1. **Frontend / UI**:
   * **Dashboard**: Overall portfolio value, total investment, total profit/loss (amount & percentage), and virtual cash balance.
   * **Holdings Table**: Displays each stock symbol, quantity, average buy price, current market price, and profit/loss (amount & percentage) for that holding.
   * **Transaction History**: Audit trail table showing past buy/sell transactions.
   * **Trading Panel**: Input fields to search for stocks, view real-time prices, and place Market BUY/SELL orders.
2. **Backend Services**:
   * **Authentication Service**: Handles Google OAuth2 flow and returns user ID & email, automatically creating a profile with default virtual cash balance if it doesn't exist.
   * **Portfolio Service**: Calculates current portfolio value, holdings cost, and live profit/loss based on current stock prices.
   * **Order Service**: Executes BUY and SELL orders 24/7. Validates funds/shares availability and executes trades instantly using the last known market price.
   * **Data Pipeline (Upstox API)**: Fetches live/latest stock price data from Upstox API.
3. **Data Polling & Market Hours Rule**:
   * **Trading Availability**: 24/7. Users can execute trades at any hour using the last available price.
   * **Live Price Refresh**: Frontend polls backend for price updates every 5 seconds.
   * **Business Hours Restriction**: Backend/Frontend only fetches live updates from the Upstox API during Indian market hours (Monday to Friday, 09:15 AM to 03:30 PM IST). Outside these hours, the dashboard calculations and prices remain frozen at the last market close price.

## Tech Stack Preferences
- **Frontend**: React (Vite)
- **Backend**: Java (Spring Boot)
- **Database**: H2 Database (File-based persistence)

## Design & Aesthetics
- **Theme**: Sleek Dark Mode fintech aesthetic (similar to Zerodha Kite or Groww dark themes)
- **Visuals**: Glassmorphism, modern typography (e.g., Inter/Outfit), high-contrast status colors (neon greens/reds for stock trends), and smooth micro-animations/transitions.
