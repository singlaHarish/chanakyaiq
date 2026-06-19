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

export interface StockSearchResponse {
  instrumentKey: string;
  tradingSymbol: string;
  name: string;
}

export interface OHLC {
  open: number;
  high: number;
  low: number;
  close: number;
}

export interface DepthItem {
  quantity: number;
  price: number;
  orders: number;
}

export interface Depth {
  buy: DepthItem[];
  sell: DepthItem[];
}

export interface StockDetails {
  instrumentKey: string;
  symbol: string;
  name: string;
  lastPrice: number;
  netChange: number;
  changePercent: number;
  open: number;
  high: number;
  low: number;
  close: number;
  volume: number;
  averagePrice: number;
  isMarketOpen: boolean;
}

export interface StockCandle {
  timestamp: string;
  open: number;
  high: number;
  low: number;
  close: number;
  volume: number;
}
