import React from 'react';

export default function HoldingsTable({ holdings, onSelectStock }) {
  if (!holdings || holdings.length === 0) {
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
            <th>Symbol</th>
            <th className="text-right">Qty</th>
            <th className="text-right">Avg. Price</th>
            <th className="text-right">LTP (Last Traded Price)</th>
            <th className="text-right">Invested</th>
            <th className="text-right">Market Value</th>
            <th className="text-right">P&L</th>
          </tr>
        </thead>
        <tbody>
          {holdings.map((h) => {
            const isProfit = h.profitLoss >= 0;
            return (
              <tr key={h.symbol} className="clickable-row" onClick={() => onSelectStock(h.symbol)}>
                <td className="symbol-cell">{h.symbol}</td>
                <td className="text-right font-mono">{h.quantity}</td>
                <td className="text-right font-mono">₹{Number(h.averagePrice).toFixed(2)}</td>
                <td className="text-right font-mono">₹{Number(h.currentPrice).toFixed(2)}</td>
                <td className="text-right font-mono">₹{Number(h.investedAmount).toLocaleString('en-IN', { minimumFractionDigits: 2 })}</td>
                <td className="text-right font-mono">₹{Number(h.currentValue).toLocaleString('en-IN', { minimumFractionDigits: 2 })}</td>
                <td className={`text-right font-mono ${isProfit ? 'text-profit' : 'text-loss'}`}>
                  {isProfit ? '+' : ''}₹{Number(h.profitLoss).toLocaleString('en-IN', { minimumFractionDigits: 2 })}
                  <div className="small-percent">({Number(h.profitLossPercent).toFixed(2)}%)</div>
                </td>
              </tr>
            );
          })}
        </tbody>
      </table>
    </div>
  );
}
