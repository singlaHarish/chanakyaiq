// Types for ChanakyaIQ front-end
export interface Holding {
  symbol: string;
  quantity: number;
  avgPrice: number;
  currentPrice?: number;
}

export interface PortfolioSummary {
  cashBalance: number;
  totalInvested: number;
  totalCurrentValue: number;
  overallProfitLoss: number;
  overallProfitLossPercent: number;
  totalPortfolioValue: number;
  holdings: Holding[];
}

export interface Transaction {
  id: number;
  symbol: string;
  quantity: number;
  price: number;
  type: 'BUY' | 'SELL';
  timestamp: string;
}
