import React from 'react';
import { Holding, formatSymbolKey } from '../types';

interface HoldingsTableProps {
  holdings: Holding[];
  onSelectStock: (symbol: string) => void;
}

export default function HoldingsTable({ holdings, onSelectStock }: HoldingsTableProps) {
  const activeHoldings = (holdings || []).filter((h) => h.quantity > 0);

  if (!activeHoldings || activeHoldings.length === 0) {
    return (
      <div className="holdings-empty card-glass">
        <p>You don't own any stocks yet. Use the Search panel to buy your first stock!</p>
      </div>
    );
  }

  return (
    <div className="table-container card-glass">
      <h3>My Holdings</h3>
      <table className="custom-table">
        <thead>
          <tr>
            <th>Asset / Symbol</th>
            <th className="text-right">Qty</th>
            <th className="text-right">Avg. Price</th>
            <th className="text-right">LTP (Last Traded Price)</th>
            <th className="text-right">Invested</th>
            <th className="text-right">Market Value</th>
            <th className="text-right">P&L</th>
            <th className="text-right">Action</th>
          </tr>
        </thead>
        <tbody>
          {activeHoldings.map((h) => {
            const pnl = h.profitLoss ?? 0;
            const isProfit = pnl >= 0;
            const avgPrice = h.averagePrice ?? h.avgPrice ?? 0;
            const curPrice = h.currentPrice ?? 0;
            const invested = h.investedAmount ?? (h.quantity * avgPrice);
            const value = h.currentValue ?? (h.quantity * curPrice);
            const pnlPercent = h.profitLossPercent ?? (invested > 0 ? (pnl / invested) * 100 : 0);
            const compressedKey = formatSymbolKey(h.symbol);
            const displayName = h.name || h.tradingSymbol || compressedKey;

            return (
              <tr key={h.symbol} className="clickable-row" onClick={() => onSelectStock(h.symbol)}>
                <td className="symbol-cell">
                  <div className="stock-display-name">{displayName}</div>
                  <div className="stock-display-key">{compressedKey}</div>
                </td>
                <td className="text-right font-mono">{h.quantity}</td>
                <td className="text-right font-mono">₹{Number(avgPrice).toFixed(2)}</td>
                <td className="text-right font-mono">₹{Number(curPrice).toFixed(2)}</td>
                <td className="text-right font-mono">₹{Number(invested).toLocaleString('en-IN', { minimumFractionDigits: 2 })}</td>
                <td className="text-right font-mono">₹{Number(value).toLocaleString('en-IN', { minimumFractionDigits: 2 })}</td>
                <td className={`text-right font-mono ${isProfit ? 'text-profit' : 'text-loss'}`}>
                  {isProfit ? '+' : ''}₹{Number(pnl).toLocaleString('en-IN', { minimumFractionDigits: 2 })}
                  <div className="small-percent">({Number(pnlPercent).toFixed(2)}%)</div>
                </td>
                <td className="text-right" onClick={(e) => e.stopPropagation()}>
                  <button
                    className="btn-secondary btn-sell-action"
                    onClick={() => onSelectStock(h.symbol)}
                  >
                    Sell
                  </button>
                </td>
              </tr>
            );
          })}
        </tbody>
      </table>
    </div>
  );
}

