import React, { useState, useEffect } from 'react';

export default function TradingPanel({ onTradeSuccess, selectedSymbol, clearSelectedSymbol, apiBase }) {
  const [searchQuery, setSearchQuery] = useState('');
  const [searchResults, setSearchResults] = useState([]);
  const [stockDetails, setStockDetails] = useState(null);
  const [quantity, setQuantity] = useState(1);
  const [tradeMessage, setTradeMessage] = useState(null);
  const [tradeError, setTradeError] = useState(null);
  const [historicalData, setHistoricalData] = useState([]);

  // Fetch search results as user types
  useEffect(() => {
    if (searchQuery.trim().length < 2) {
      setSearchResults([]);
      return;
    }
    const delayDebounce = setTimeout(() => {
      fetch(`${apiBase}/api/stocks/search?query=${searchQuery}`)
        .then((res) => res.json())
        .then((data) => setSearchResults(data))
        .catch((err) => console.error(err));
    }, 300);

    return () => clearTimeout(delayDebounce);
  }, [searchQuery]);

  // Load details if symbol changes or is clicked
  useEffect(() => {
    if (!selectedSymbol) {
      setStockDetails(null);
      setHistoricalData([]);
      return;
    }

    const fetchDetails = () => {
      fetch(`${apiBase}/api/stocks/price/${selectedSymbol}`)
        .then((res) => res.json())
        .then((data) => setStockDetails(data))
        .catch((err) => console.error(err));

      fetch(`${apiBase}/api/stocks/history/${selectedSymbol}`)
        .then((res) => res.json())
        .then((data) => setHistoricalData(data))
        .catch((err) => console.error(err));
    };

    fetchDetails();
    // Refresh stock detail price every 5 seconds if selected
    const interval = setInterval(fetchDetails, 5000);
    return () => clearInterval(interval);
  }, [selectedSymbol]);

  const handleOrder = (type) => {
    if (quantity <= 0) {
      setTradeError('Quantity must be greater than 0');
      return;
    }
    setTradeMessage(null);
    setTradeError(null);

    fetch(`${apiBase}/api/trade/${type}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        symbol: stockDetails.symbol,
        quantity: parseInt(quantity),
      }),
    })
      .then(async (res) => {
        const data = await res.json();
        if (res.ok && data.success) {
          setTradeMessage(`Successfully executed market ${type.toUpperCase()} order for ${quantity} shares of ${stockDetails.symbol}`);
          onTradeSuccess();
        } else {
          setTradeError(data.error || 'Failed to execute order');
        }
      })
      .catch((err) => {
        setTradeError('Network error executing trade');
        console.error(err);
      });
  };

  const isProfit = stockDetails?.change >= 0;

  // Simple sparkline visual using historical prices
  const renderSparkline = () => {
    if (historicalData.length === 0) return null;
    const max = Math.max(...historicalData);
    const min = Math.min(...historicalData);
    const range = max - min === 0 ? 1 : max - min;
    const height = 80;
    const width = 280;
    const padding = 5;

    const points = historicalData.map((val, index) => {
      const x = padding + (index * (width - padding * 2)) / (historicalData.length - 1);
      const y = height - padding - ((val - min) * (height - padding * 2)) / range;
      return `${x},${y}`;
    }).join(' ');

    return (
      <svg className="sparkline" width={width} height={height}>
        <polyline
          fill="none"
          stroke={isProfit ? '#00e676' : '#ff1744'}
          strokeWidth="2.5"
          points={points}
        />
      </svg>
    );
  };

  return (
    <div className="trading-panel card-glass">
      <h3>Trading Terminal</h3>
      
      {!selectedSymbol && (
        <div className="search-box">
          <input
            type="text"
            placeholder="Search stock symbol (e.g. RELIANCE, TCS...)"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="search-input"
          />
          {searchResults.length > 0 && (
            <div className="search-results">
              {searchResults.map((stock) => (
                <div
                  key={stock.tradingSymbol}
                  className="search-item"
                  onClick={() => {
                    clearSelectedSymbol();
                    setTimeout(() => clearSelectedSymbol(stock.tradingSymbol), 0);
                  }}
                >
                  <div className="search-symbol">{stock.tradingSymbol}</div>
                  <div className="search-name">{stock.name}</div>
                </div>
              ))}
            </div>
          )}
        </div>
      )}

      {selectedSymbol && stockDetails && (
        <div className="stock-info">
          <button className="btn-secondary btn-back" onClick={() => {
            clearSelectedSymbol(null);
            setTradeMessage(null);
            setTradeError(null);
          }}>
            ← Back to Search
          </button>
          
          <div className="info-header">
            <div>
              <h2 className="info-symbol">{stockDetails.symbol}</h2>
              <p className="info-name">{stockDetails.name}</p>
            </div>
            <div className="info-price-section text-right">
              <h2 className="info-price">₹{Number(stockDetails.price).toFixed(2)}</h2>
              <p className={`info-change ${isProfit ? 'profit' : 'loss'}`}>
                {isProfit ? '+' : ''}{Number(stockDetails.change).toFixed(2)} ({Number(stockDetails.changePercent).toFixed(2)}%)
              </p>
            </div>
          </div>

          <div className="chart-container">
            <p className="chart-title">Last 20 ticks trend</p>
            {renderSparkline()}
          </div>

          <div className="trade-actions">
            <div className="quantity-selector">
              <label>Quantity</label>
              <input
                type="number"
                min="1"
                value={quantity}
                onChange={(e) => setQuantity(Math.max(1, parseInt(e.target.value) || 1))}
                className="quantity-input"
              />
            </div>
            <div className="buttons-row">
              <button className="btn-primary btn-buy" onClick={() => handleOrder('buy')}>
                Market BUY
              </button>
              <button className="btn-danger btn-sell" onClick={() => handleOrder('sell')}>
                Market SELL
              </button>
            </div>
          </div>

          {tradeMessage && <div className="banner-success">{tradeMessage}</div>}
          {tradeError && <div className="banner-error">{tradeError}</div>}
        </div>
      )}

      {!selectedSymbol && (
        <div className="trading-empty">
          <p>Select or search a stock to start trading.</p>
        </div>
      )}
    </div>
  );
}
