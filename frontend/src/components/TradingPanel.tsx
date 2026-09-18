import React, { useState, useEffect } from 'react';
import { StockSearchResponse, StockDetails, StockCandle, Holding, TradeExecutionResponseDTO } from '../types';

interface TradingPanelProps {
  onTradeSuccess: () => void;
  selectedSymbol: string | null;
  clearSelectedSymbol: (symbol?: string | null) => void;
  apiBase: string;
  cashBalance?: number;
  holdings?: Holding[];
}

export default function TradingPanel({
  onTradeSuccess,
  selectedSymbol,
  clearSelectedSymbol,
  apiBase,
  cashBalance = 0,
  holdings = [],
}: TradingPanelProps) {
  const [searchQuery, setSearchQuery] = useState('');
  const [searchResults, setSearchResults] = useState<StockSearchResponse[]>([]);
  const [stockDetails, setStockDetails] = useState<StockDetails | null>(null);
  const [quantity, setQuantity] = useState(1);
  const [tradeSuccessResponse, setTradeSuccessResponse] = useState<TradeExecutionResponseDTO | null>(null);
  const [tradeError, setTradeError] = useState<string | null>(null);
  const [historicalData, setHistoricalData] = useState<StockCandle[]>([]);
  const [hoveredIndex, setHoveredIndex] = useState<number | null>(null);

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
  }, [searchQuery, apiBase]);

  // Load details if symbol changes or is clicked
  useEffect(() => {
    if (!selectedSymbol) {
      setStockDetails(null);
      setHistoricalData([]);
      setHoveredIndex(null);
      setTradeSuccessResponse(null);
      setTradeError(null);
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
  }, [selectedSymbol, apiBase]);

  const handleOrder = (type: 'buy' | 'sell') => {
    if (!stockDetails) return;
    if (quantity <= 0) {
      setTradeError('Quantity must be greater than 0');
      return;
    }
    setTradeSuccessResponse(null);
    setTradeError(null);

    fetch(`${apiBase}/api/trade/${type}`, {
      method: 'POST',
      credentials: 'include',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        symbol: stockDetails.instrumentKey,
        quantity: quantity,
      }),
    })
      .then(async (res) => {
        const data: TradeExecutionResponseDTO = await res.json();
        if (res.ok && (data.success || data.success === undefined)) {
          const executedPrice = data.executedPrice ?? stockDetails.lastPrice;
          const executedQty = data.quantity ?? quantity;
          const totalBill = data.totalBill ?? (executedQty * executedPrice);
          const updatedCash = data.updatedCashBalance ?? (type === 'buy' ? cashBalance - totalBill : cashBalance + totalBill);

          setTradeSuccessResponse({
            success: true,
            message: data.message || `Market ${type.toUpperCase()} order executed successfully`,
            symbol: data.symbol || stockDetails.name || stockDetails.symbol,
            executedPrice,
            quantity: executedQty,
            totalBill,
            updatedCashBalance: updatedCash,
          });
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

  const currentPrice = stockDetails?.lastPrice ?? 0;
  const totalEstimatedBill = quantity * currentPrice;

  // Buy disable check
  const isBuyDisabled = cashBalance < totalEstimatedBill || quantity <= 0;
  const buyTooltip = cashBalance < totalEstimatedBill
    ? `Insufficient cash balance. Required: ₹${totalEstimatedBill.toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}, Available: ₹${cashBalance.toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`
    : '';

  // Sell disable check
  const userHolding = (holdings || []).find(
    (h) => h.symbol === stockDetails?.symbol || h.symbol === stockDetails?.tradingSymbol || h.symbol === stockDetails?.instrumentKey
  );
  const ownedQty = userHolding ? userHolding.quantity : 0;
  const isSellDisabled = ownedQty <= 0 || quantity > ownedQty || quantity <= 0;
  const sellTooltip = ownedQty <= 0
    ? `You do not own any shares of ${stockDetails?.symbol || 'this stock'}.`
    : quantity > ownedQty
    ? `Requested quantity (${quantity}) exceeds owned shares (${ownedQty}).`
    : '';

  const isProfit = (stockDetails?.netChange ?? 0) >= 0;

  const renderInteractiveChart = () => {
    if (historicalData.length === 0) {
      return (
        <div className="chart-empty">
          <p>No historical data available</p>
        </div>
      );
    }

    const closePrices = historicalData.map((candle) => candle.close);
    const rawMin = Math.min(...closePrices);
    const rawMax = Math.max(...closePrices);
    const rawRange = rawMax - rawMin || 1;

    // Add 5% padding top and bottom so line doesn't clip
    const min = rawMin - rawRange * 0.05;
    const max = rawMax + rawRange * 0.05;
    const range = max - min;

    const svgWidth = 520;
    const svgHeight = 260;
    const marginTop = 20;
    const marginBottom = 40;
    const marginLeft = 65;
    const marginRight = 20;

    const plotWidth = svgWidth - marginLeft - marginRight;
    const plotHeight = svgHeight - marginTop - marginBottom;

    const points = historicalData.map((candle, index) => {
      const x = marginLeft + (index * plotWidth) / (historicalData.length - 1 || 1);
      const y = marginTop + plotHeight - ((candle.close - min) * plotHeight) / range;
      return { x, y, candle, index };
    });

    const pointsString = points.map((p) => `${p.x.toFixed(1)},${p.y.toFixed(1)}`).join(' ');
    const areaString = `M ${points[0].x.toFixed(1)},${marginTop + plotHeight} L ${pointsString} L ${points[points.length - 1].x.toFixed(1)},${marginTop + plotHeight} Z`;
    const linePath = `M ${pointsString}`;

    // Y-Axis Ticks (5 Ticks)
    const yTickCount = 5;
    const yTicks = Array.from({ length: yTickCount }, (_, i) => {
      const val = min + (range * i) / (yTickCount - 1);
      const y = marginTop + plotHeight - (i * plotHeight) / (yTickCount - 1);
      return { val, y };
    });

    // X-Axis Ticks (~5 evenly spaced ticks)
    const xTickCount = Math.min(5, historicalData.length);
    const xTicks = Array.from({ length: xTickCount }, (_, i) => {
      const idx = Math.round((i * (historicalData.length - 1)) / (xTickCount - 1 || 1));
      const point = points[idx];
      const dateStr = new Date(point.candle.timestamp).toLocaleDateString('en-US', {
        month: 'short',
        day: 'numeric',
      });
      return { dateStr, x: point.x, idx };
    });

    const activeIndex = hoveredIndex !== null && hoveredIndex >= 0 && hoveredIndex < historicalData.length
      ? hoveredIndex
      : historicalData.length - 1;
    const activeCandle = historicalData[activeIndex];
    const activePoint = points[activeIndex];

    const chartColor = isProfit ? '#00e676' : '#ff1744';
    const gradientId = isProfit ? 'profitGradient' : 'lossGradient';

    const handleMouseMove = (e: React.MouseEvent<SVGSVGElement>) => {
      const rect = e.currentTarget.getBoundingClientRect();
      const mouseX = e.clientX - rect.left;
      const scaleX = svgWidth / rect.width;
      const svgMouseX = mouseX * scaleX;

      const clampedX = Math.max(marginLeft, Math.min(svgWidth - marginRight, svgMouseX));
      const ratio = (clampedX - marginLeft) / plotWidth;
      const nearestIdx = Math.round(ratio * (historicalData.length - 1));
      setHoveredIndex(Math.max(0, Math.min(historicalData.length - 1, nearestIdx)));
    };

    return (
      <div className="interactive-chart-wrapper">
        {/* Tooltip Header Bar */}
        <div className="chart-tooltip-bar">
          <div className="tooltip-item">
            <span className="tooltip-label">Date</span>
            <span className="tooltip-value font-mono">
              {new Date(activeCandle.timestamp).toLocaleDateString('en-US', {
                month: 'short',
                day: 'numeric',
                year: 'numeric',
              })}
            </span>
          </div>
          <div className="tooltip-item">
            <span className="tooltip-label">Close</span>
            <span className="tooltip-value font-mono" style={{ color: chartColor }}>
              ₹{activeCandle.close.toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
            </span>
          </div>
          <div className="tooltip-item">
            <span className="tooltip-label">High</span>
            <span className="tooltip-value font-mono">
              ₹{activeCandle.high.toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
            </span>
          </div>
          <div className="tooltip-item">
            <span className="tooltip-label">Low</span>
            <span className="tooltip-value font-mono">
              ₹{activeCandle.low.toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
            </span>
          </div>
          <div className="tooltip-item">
            <span className="tooltip-label">Volume</span>
            <span className="tooltip-value font-mono">
              {activeCandle.volume.toLocaleString('en-IN')}
            </span>
          </div>
        </div>

        {/* SVG Chart Canvas */}
        <svg
          className="interactive-chart-svg"
          viewBox={`0 0 ${svgWidth} ${svgHeight}`}
          onMouseMove={handleMouseMove}
          onMouseLeave={() => setHoveredIndex(null)}
        >
          <defs>
            <linearGradient id="profitGradient" x1="0" y1="0" x2="0" y2="1">
              <stop offset="0%" stopColor="#00e676" stopOpacity="0.25" />
              <stop offset="100%" stopColor="#00e676" stopOpacity="0.0" />
            </linearGradient>
            <linearGradient id="lossGradient" x1="0" y1="0" x2="0" y2="1">
              <stop offset="0%" stopColor="#ff1744" stopOpacity="0.25" />
              <stop offset="100%" stopColor="#ff1744" stopOpacity="0.0" />
            </linearGradient>
          </defs>

          {/* Grid Lines & Y-Axis Ticks */}
          {yTicks.map((tick, i) => (
            <g key={`y-tick-${i}`}>
              <line
                x1={marginLeft}
                y1={tick.y}
                x2={svgWidth - marginRight}
                y2={tick.y}
                stroke="rgba(255, 255, 255, 0.07)"
                strokeDasharray="3 3"
              />
              <text
                x={marginLeft - 8}
                y={tick.y + 4}
                textAnchor="end"
                fill="var(--text-secondary)"
                fontSize="10"
                fontFamily="'Roboto Mono', monospace"
              >
                ₹{tick.val >= 1000 ? tick.val.toFixed(0) : tick.val.toFixed(2)}
              </text>
            </g>
          ))}

          {/* X-Axis Ticks */}
          {xTicks.map((tick, i) => (
            <g key={`x-tick-${i}`}>
              <line
                x1={tick.x}
                y1={marginTop + plotHeight}
                x2={tick.x}
                y2={marginTop + plotHeight + 5}
                stroke="rgba(255, 255, 255, 0.2)"
              />
              <text
                x={tick.x}
                y={marginTop + plotHeight + 20}
                textAnchor="middle"
                fill="var(--text-secondary)"
                fontSize="10"
                fontFamily="'Outfit', sans-serif"
              >
                {tick.dateStr}
              </text>
            </g>
          ))}

          {/* Axis border line */}
          <line
            x1={marginLeft}
            y1={marginTop + plotHeight}
            x2={svgWidth - marginRight}
            y2={marginTop + plotHeight}
            stroke="rgba(255, 255, 255, 0.15)"
          />
          <line
            x1={marginLeft}
            y1={marginTop}
            x2={marginLeft}
            y2={marginTop + plotHeight}
            stroke="rgba(255, 255, 255, 0.15)"
          />

          {/* Gradient Area under curve */}
          <path d={areaString} fill={`url(#${gradientId})`} />

          {/* Trend Line */}
          <path
            d={linePath}
            fill="none"
            stroke={chartColor}
            strokeWidth="2.5"
            strokeLinecap="round"
            strokeLinejoin="round"
          />

          {/* Interactive Hover Guides & Indicator */}
          {hoveredIndex !== null && activePoint && (
            <g className="hover-indicators">
              <line
                x1={activePoint.x}
                y1={marginTop}
                x2={activePoint.x}
                y2={marginTop + plotHeight}
                stroke="rgba(255, 255, 255, 0.35)"
                strokeDasharray="3 3"
              />
              <line
                x1={marginLeft}
                y1={activePoint.y}
                x2={svgWidth - marginRight}
                y2={activePoint.y}
                stroke="rgba(255, 255, 255, 0.25)"
                strokeDasharray="3 3"
              />
              <circle
                cx={activePoint.x}
                cy={activePoint.y}
                r="6"
                fill={chartColor}
                stroke="#ffffff"
                strokeWidth="2"
              />
            </g>
          )}
        </svg>
      </div>
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
                  key={stock.instrumentKey}
                  className="search-item"
                  onClick={() => {
                    clearSelectedSymbol();
                    setTimeout(() => clearSelectedSymbol(stock.instrumentKey), 0);
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
          <button
            className="btn-secondary btn-back"
            onClick={() => {
              clearSelectedSymbol(null);
              setTradeSuccessResponse(null);
              setTradeError(null);
            }}
          >
            ← Back to Search
          </button>

          <div className="info-header">
            <div>
              <h2 className="info-symbol">{stockDetails.name}</h2>
              <p className="info-name">{stockDetails.symbol}</p>
            </div>
            <div className="info-price-section text-right">
              <h2 className="info-price">₹{Number(stockDetails.lastPrice).toFixed(2)}</h2>
              <p className={`info-change ${isProfit ? 'profit' : 'loss'}`}>
                {isProfit ? '+' : ''}{Number(stockDetails.netChange).toFixed(2)} ({Number(stockDetails.changePercent).toFixed(2)}%)
              </p>
            </div>
          </div>

          <div className="chart-container">
            <p className="chart-title">Daily Historical Trend (Last 30 Days)</p>
            {renderInteractiveChart()}
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

            {/* Live estimated order cost calculation directly above Buy/Sell buttons */}
            <div className="total-bill-container">
              <span className="total-bill-label">Total Bill:</span>
              <span className="total-bill-calc font-mono">
                {quantity} × ₹{currentPrice.toFixed(2)} = <strong className="total-bill-amount">₹{totalEstimatedBill.toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}</strong>
              </span>
            </div>

            <div className="buttons-row">
              <div className="button-wrapper" title={buyTooltip}>
                <button
                  className="btn-primary btn-buy"
                  onClick={() => handleOrder('buy')}
                  disabled={isBuyDisabled}
                >
                  Market BUY
                </button>
                {cashBalance < totalEstimatedBill && (
                  <div className="button-indicator indicator-warning">Insufficient Cash</div>
                )}
              </div>

              <div className="button-wrapper" title={sellTooltip}>
                <button
                  className="btn-danger btn-sell"
                  onClick={() => handleOrder('sell')}
                  disabled={isSellDisabled}
                >
                  Market SELL
                </button>
                {ownedQty <= 0 ? (
                  <div className="button-indicator indicator-muted">0 Shares Owned</div>
                ) : quantity > ownedQty ? (
                  <div className="button-indicator indicator-warning">Max {ownedQty} Shares</div>
                ) : null}
              </div>
            </div>
          </div>

          {/* Detailed, formatted success banner upon execution */}
          {tradeSuccessResponse && (
            <div className="banner-success-detailed">
              <div className="banner-header">
                <span className="success-badge font-mono">ORDER EXECUTED</span>
                <p className="banner-msg">{tradeSuccessResponse.message}</p>
              </div>
              <div className="banner-details-grid">
                <div className="banner-detail-item">
                  <span className="detail-label">Symbol</span>
                  <span className="detail-value font-mono">{tradeSuccessResponse.symbol}</span>
                </div>
                <div className="banner-detail-item">
                  <span className="detail-label">Executed Price</span>
                  <span className="detail-value font-mono">₹{Number(tradeSuccessResponse.executedPrice).toFixed(2)}</span>
                </div>
                <div className="banner-detail-item">
                  <span className="detail-label">Quantity</span>
                  <span className="detail-value font-mono">{tradeSuccessResponse.quantity}</span>
                </div>
                <div className="banner-detail-item">
                  <span className="detail-label">Total Bill</span>
                  <span className="detail-value font-mono">₹{Number(tradeSuccessResponse.totalBill).toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}</span>
                </div>
                <div className="banner-detail-item">
                  <span className="detail-label">Updated Cash Balance</span>
                  <span className="detail-value font-mono highlight-cash">₹{Number(tradeSuccessResponse.updatedCashBalance).toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}</span>
                </div>
              </div>
            </div>
          )}

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
