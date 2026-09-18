import React from 'react';
import { formatSymbolKey } from '../types';

export default function TransactionHistory({ transactions }) {
  if (!transactions || transactions.length === 0) {
    return (
      <div className="table-container card-glass">
        <h3>Order History</h3>
        <div className="holdings-empty">
          <p>No order history found.</p>
        </div>
      </div>
    );
  }

  return (
    <div className="table-container card-glass">
      <h3>Order History</h3>
      <table className="custom-table">
        <thead>
          <tr>
            <th>Time</th>
            <th>Asset / Symbol</th>
            <th>Type</th>
            <th className="text-right">Qty</th>
            <th className="text-right">Price</th>
            <th className="text-right">Total</th>
          </tr>
        </thead>
        <tbody>
          {transactions.map((tx) => {
            const isBuy = tx.type === 'BUY';
            const total = tx.price * tx.quantity;
            const compressedKey = formatSymbolKey(tx.symbol);
            const displayName = tx.name || tx.tradingSymbol || compressedKey;
            return (
              <tr key={tx.id}>
                <td className="font-mono text-secondary">
                  {(() => {
                    const d = new Date(tx.timestamp);
                    const day = String(d.getDate()).padStart(2, '0');
                    const months = ['Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec'];
                    const mon = months[d.getMonth()];
                    const yr = String(d.getFullYear()).slice(-2);
                    const hh = String(d.getHours()).padStart(2, '0');
                    const mm = String(d.getMinutes()).padStart(2, '0');
                    const ss = String(d.getSeconds()).padStart(2, '0');
                    return `${day} ${mon} ${yr}, ${hh}:${mm}:${ss}`;
                  })()}
                </td>
                <td className="symbol-cell">
                  <div className="stock-display-name">{displayName}</div>
                  <div className="stock-display-key">{compressedKey}</div>
                </td>
                <td>
                  <span className={`badge-type ${isBuy ? 'badge-buy' : 'badge-sell'}`}>
                    {tx.type}
                  </span>
                </td>
                <td className="text-right font-mono">{tx.quantity}</td>
                <td className="text-right font-mono">₹{Number(tx.price).toFixed(2)}</td>
                <td className="text-right font-mono">₹{Number(total).toLocaleString('en-IN', { minimumFractionDigits: 2 })}</td>
              </tr>
            );
          })}
        </tbody>
      </table>
    </div>
  );
}
