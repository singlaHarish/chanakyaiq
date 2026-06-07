import React from 'react';

export default function Dashboard({ summary }) {
  const {
    cashBalance = 0,
    totalInvested = 0,
    totalCurrentValue = 0,
    overallProfitLoss = 0,
    overallProfitLossPercent = 0,
    totalPortfolioValue = 0,
  } = summary;

  const isProfit = overallProfitLoss >= 0;

  return (
    <div className="dashboard-summary">
      <div className="summary-card main-card">
        <span className="card-label">Total Portfolio Value</span>
        <h2 className="card-value">₹{Number(totalPortfolioValue).toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}</h2>
        <div className={`overall-pl ${isProfit ? 'profit' : 'loss'}`}>
          <span className="trend-arrow">{isProfit ? '▲' : '▼'}</span>
          <span>
            {isProfit ? '+' : ''}₹{Number(overallProfitLoss).toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })} ({Number(overallProfitLossPercent).toFixed(2)}%)
          </span>
        </div>
      </div>

      <div className="summary-grid">
        <div className="summary-card">
          <span className="card-label">Invested Amount</span>
          <h3 className="card-subvalue">₹{Number(totalInvested).toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}</h3>
        </div>

        <div className="summary-card">
          <span className="card-label">Current Value</span>
          <h3 className="card-subvalue">₹{Number(totalCurrentValue).toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}</h3>
        </div>

        <div className="summary-card">
          <span className="card-label">Available Virtual Cash</span>
          <h3 className="card-subvalue cash">₹{Number(cashBalance).toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}</h3>
        </div>
      </div>
    </div>
  );
}
