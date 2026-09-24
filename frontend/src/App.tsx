import React, { useState, useEffect, useRef } from 'react';
import Dashboard from './components/Dashboard';
import HoldingsTable from './components/HoldingsTable';
import TradingPanel from './components/TradingPanel';
import TransactionHistory from './components/TransactionHistory';
import WebSocketStatus from './components/WebSocketStatus';

const API_BASE = 'http://localhost:8080';

export default function App() {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [portfolio, setPortfolio] = useState({
    holdings: [] as any[],
    cashBalance: 0,
    totalInvested: 0,
    totalCurrentValue: 0,
    overallProfitLoss: 0,
    overallProfitLossPercent: 0,
    totalPortfolioValue: 0
  });
  const [transactions, setTransactions] = useState([]);
  const [selectedStock, setSelectedStock] = useState<string | null>(null);
  
  // WebSocket/SSE Status
  const [websocketStatus, setWebsocketStatus] = useState<{
    connected: boolean;
    mode: 'LIVE' | 'FALLBACK_REST';
  } | undefined>(undefined);
  
  const [pollingEnabled, setPollingEnabled] = useState(false);  // Only enabled when SSE fails
  const pollingIntervalRef = useRef<NodeJS.Timeout | null>(null);
  
  const sseRef = useRef<EventSource | null>(null);

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

  const fetchPortfolioData = async () => {
    if (!user?.email) return;
    const response = await fetch(`${API_BASE}/api/portfolio/summary`, { credentials: 'include' });
    
    if (response.status === 401) {
      setUser(null);
      return;
    }
    
    const data = await response.json();
    if (data) {
      setPortfolio(data);
      // Removed: subscribeToUserHoldings() - called once on login instead
    }
    
    // Also fetch transactions
    const transResponse = await fetch(`${API_BASE}/api/portfolio/transactions`, { credentials: 'include' });
    if (transResponse.status !== 401) {
      const transData = await transResponse.json();
      if (transData) setTransactions(transData);
    }
  };

  const subscribeToUserHoldings = async (holdings: any[]) => {
    if (holdings.length === 0 || !user?.email) return;
    
    const instruments = holdings.map(h => h.symbol);
    try {
      const response = await fetch(`${API_BASE}/api/websocket/subscribe`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify({ instrumentKeys: instruments })
      });
      
      if (response.ok) {
        const data = await response.json();
        console.log(`Subscribed to ${data.subscribed} instruments on login`);
      }
    } catch (err) {
      console.error('Failed to subscribe to user holdings:', err);
    }
  };

  useEffect(() => {
    checkAuth();
  }, []);

  useEffect(() => {
    if (user?.email) {
      fetchPortfolioData();
      setupSSE();
      subscribeToUserHoldings(user?.holdings || []);  // Subscribe once on login
      
      // Don't set up polling here - it will be controlled by SSE status
      return () => {
        if (pollingIntervalRef.current) {
          clearInterval(pollingIntervalRef.current);
        }
        if (sseRef.current) {
          sseRef.current.close();
        }
      };
    }
  }, [user?.email]);

  const setupSSE = () => {
    const eventSource = new EventSource(`${API_BASE}/api/sse/connect`, {
      withCredentials: true
    });
    
    eventSource.onmessage = (event) => {
      console.log('SSE Message:', event.data);
    };
    
    eventSource.onerror = (error) => {
      console.error('SSE Error:', error);
      setWebsocketStatus({ connected: false, mode: 'FALLBACK_REST' });
      setPollingEnabled(true);  // Enable polling when SSE fails
    };
    
    eventSource.addEventListener('connected', (event) => {
      console.log('SSE Connected:', event.data);
      setWebsocketStatus({ connected: true, mode: 'LIVE' });
      setPollingEnabled(false);  // Disable polling when SSE works
    });
    
    eventSource.addEventListener('websocket_status', (event) => {
      const data = JSON.parse(event.data);
      setWebsocketStatus(data);
      if (data.connected) {
        setPollingEnabled(false);  // Disable polling when WebSocket reconnects
      } else {
        setPollingEnabled(true);  // Enable polling when WebSocket disconnects
      }
    });
    
    eventSource.addEventListener('price_update', (event) => {
      const data = JSON.parse(event.data);
      console.log('Price update received:', data);
      
      // Update price in holdings
      updatePriceInHoldings(data.instrumentKey, data.price);
    });
    
    sseRef.current = eventSource;
  };
  
  // Start/stop polling based on SSE status
  useEffect(() => {
    if (!pollingEnabled) {
      if (pollingIntervalRef.current) {
        clearInterval(pollingIntervalRef.current);
        pollingIntervalRef.current = null;
      }
      return;
    }
    
    // Polling is enabled, start interval
    pollingIntervalRef.current = setInterval(fetchPortfolioData, 5000);
    
    return () => {
      if (pollingIntervalRef.current) {
        clearInterval(pollingIntervalRef.current);
        pollingIntervalRef.current = null;
      }
    };
  }, [pollingEnabled]);
  
  const updatePriceInHoldings = (instrumentKey: string, priceData: any) => {
    setPortfolio(prev => {
      const updatedHoldings = prev.holdings.map(h => {
        if (h.symbol === instrumentKey) {
          return {
            ...h,
            currentPrice: priceData.lastPrice,
            currentValue: h.quantity * priceData.lastPrice,
            profitLoss: (priceData.lastPrice - h.averagePrice) * h.quantity,
            profitLossPercent: ((priceData.lastPrice - h.averagePrice) / h.averagePrice) * 100
          };
        }
        return h;
      });
      
      const totalInvested = updatedHoldings.reduce((sum, h) => sum + (h.quantity * h.averagePrice), 0);
      const totalCurrentValue = updatedHoldings.reduce((sum, h) => sum + (h.quantity * h.currentPrice), 0);
      
      return {
        ...prev,
        holdings: updatedHoldings,
        totalInvested,
        totalCurrentValue,
        totalPortfolioValue: totalCurrentValue + prev.cashBalance,
        overallProfitLoss: totalCurrentValue - totalInvested,
        overallProfitLossPercent: totalInvested > 0 ? ((totalCurrentValue - totalInvested) / totalInvested) * 100 : 0
      };
    });
  };

  const handleLogout = () => {
    fetch(`${API_BASE}/api/auth/logout`, { method: 'POST', credentials: 'include' })
      .then(() => {
        if (sseRef.current) {
          sseRef.current.close();
        }
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
      {/* WebSocket Status Banner - only show when degraded */}
      {websocketStatus && (
        <div className="websocket-status-wrapper">
          <WebSocketStatus 
            connected={websocketStatus.connected} 
            mode={websocketStatus.mode} 
          />
        </div>
      )}
      
      <header className="app-header card-glass">
        <div className="header-logo">
          <span className="logo-icon">📈</span>
          <span className="header-title">ChanakyaIQ</span>
        </div>
        <div className="header-user">
          <span className="user-email">{user?.email}</span>
          <button className="btn-secondary btn-logout" onClick={handleLogout}>
            Logout
          </button>
        </div>
      </header>

      <main className="app-content">
        <div className="layout-top">
          <Dashboard summary={portfolio} />
        </div>

        <div className="layout-grid">
          <div className="layout-main-terminal">
            <TradingPanel
              apiBase={API_BASE}
              selectedSymbol={selectedStock}
              clearSelectedSymbol={(sym) => setSelectedStock(sym || null)}
              onTradeSuccess={fetchPortfolioData}
              cashBalance={portfolio.cashBalance}
              holdings={portfolio.holdings}
            />
          </div>

          <div className="layout-side-panels">
            <HoldingsTable
              holdings={portfolio.holdings}
              onSelectStock={(symbol) => setSelectedStock(symbol)}
              websocketStatus={websocketStatus || undefined}
            />
            <TransactionHistory transactions={transactions} />
          </div>
        </div>
      </main>
    </div>
  );
}
