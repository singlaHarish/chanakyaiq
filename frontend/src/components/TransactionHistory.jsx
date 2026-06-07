import React from 'react';

export default function TransactionHistory({ transactions }) {
  if (!transactions || transactions.length === 0) {
    return (
      <div className="holdings-empty card-glass">
        <p>No transaction history found.</p>
      </div>
    );
  }

  return (
    <div className="table-container card-glass">
      <h3>Transaction Logs (Audit Trail)</h3>
      <table className="custom-table">
        <thead>
          <tr>
            <th>Timestamp</th>
            <th>Symbol</th>
            <th>Type</th>
            <th className="text-right">Qty</th>
            <th className="text-right">Execution Price</th>
            <th className="text-right">Total Amount</th>
          </tr>
        </thead>
        <tbody>
          {transactions.map((tx) => {
            const isBuy = tx.type === 'BUY';
            const total = tx.price * tx.quantity;
            return (
              <tr key={tx.id}>
                <td className="font-mono text-secondary">
                  {new Date(tx.timestamp).toLocaleString('en-IN', {
                    day: '2-digit',
                    month: 'short',
                    year: 'numeric',
                    hour: '2-digit',
                    minute: '2-digit',
                    second: '2-digit'
                  })}
                </td>
                <td className="symbol-cell">{tx.symbol}</td>
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
