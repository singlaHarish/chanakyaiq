import React, { useState, useEffect } from 'react';
import Dashboard from './components/Dashboard';
import HoldingsTable from './components/HoldingsTable';
import TradingPanel from './components/TradingPanel';
import TransactionHistory from './components/TransactionHistory';

const API_BASE = 'http://localhost:8080';

export default function App() {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [portfolio, setPortfolio] = useState({
    holdings: [],
    cashBalance: 0,
    totalInvested: 0,
    totalCurrentValue: 0,
    overallProfitLoss: 0,
    overallProfitLossPercent: 0,
    totalPortfolioValue: 0
  });
  const [transactions, setTransactions] = useState([]);
  const [selectedStock, setSelectedStock] = useState(null);

  const checkAuth = () => {
    fetch(`${API_BASE}/api/auth/status`, { credentials: 'include' })
      .then((res) => res.json())
      .then((data) => {
        if (data.authenticated) {
          setUser(data);
        } else {
          setUser(null);
        }
        setLoading(false);
      })
      .catch((err) => {
        console.error('Auth verification failed:', err);
        setLoading(false);
      });
  };

  const fetchPortfolioData = () => {
    if (!user) return;
    fetch(`${API_BASE}/api/portfolio/summary`, { credentials: 'include' })
      .then((res) => {
        if (res.status === 401) {
          setUser(null);
          return null;
        }
        return res.json();
      })
      .then((data) => {
        if (data) setPortfolio(data);
      })
      .catch((err) => console.error('Failed to load portfolio:', err));

    fetch(`${API_BASE}/api/portfolio/transactions`, { credentials: 'include' })
      .then((res) => {
        if (res.status === 401) return null;
        return res.json();
      })
      .then((data) => {
        if (data) setTransactions(data);
      })
      .catch((err) => console.error('Failed to load transactions:', err));
  };

  useEffect(() => {
    checkAuth();
  }, []);

  useEffect(() => {
    if (user) {
      fetchPortfolioData();
      // Set up 5s polling for portfolio valuation and live prices
      const interval = setInterval(fetchPortfolioData, 5000);
      return () => clearInterval(interval);
    }
  }, [user]);

  const handleLogout = () => {
    fetch(`${API_BASE}/api/auth/logout`, { method: 'POST', credentials: 'include' })
      .then(() => {
        setUser(null);
      })
      .catch((err) => console.error('Logout error:', err));
  };

  const loginWithGoogle = () => {
    window.location.href = `${API_BASE}/oauth2/authorization/google`;
  };

  if (loading) {
    return (
      <div className="app-loading">
        <div className="spinner"></div>
        <p>Loading ChanakyaIQ...</p>
      </div>
    );
  }

  if (!user) {
    return (
      <div className="landing-page">
        <div className="landing-card card-glass">
          <div className="logo-section">
            <span className="logo-icon">📈</span>
            <h1 className="logo-title">ChanakyaIQ</h1>
          </div>
          <p className="landing-description">
            A premium, real-time stock simulator for Indian markets. Learn to invest, test strategies, and track portfolios without actual capital risk.
          </p>
          <button className="btn-primary btn-login" onClick={loginWithGoogle}>
            <span className="google-icon">G</span> Sign In with Google
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="app-container">
      <header className="app-header card-glass">
        <div className="header-logo">
          <span className="logo-icon">📈</span>
          <span className="header-title">ChanakyaIQ</span>
        </div>
        <div className="header-user">
          <span className="user-email">{user.email}</span>
          <button className="btn-secondary btn-logout" onClick={handleLogout}>
            Logout
          </button>
        </div>
      </header>

      <main className="app-content">
        <div className="layout-left">
          <Dashboard summary={portfolio} />
          <HoldingsTable
            holdings={portfolio.holdings}
            onSelectStock={(symbol) => setSelectedStock(symbol)}
          />
          <TransactionHistory transactions={transactions} />
        </div>

        <div className="layout-right">
          <TradingPanel
            apiBase={API_BASE}
            selectedSymbol={selectedStock}
            clearSelectedSymbol={(sym) => setSelectedStock(sym)}
            onTradeSuccess={fetchPortfolioData}
          />
        </div>
      </main>
    </div>
  );
}
