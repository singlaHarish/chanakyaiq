// Type declarations for React components
declare module '*.tsx' {
  const component: React.FC<any>;
  export default component;
}

declare module '*.jsx' {
  const component: React.FC<any>;
  export default component;
}

// Type declaration for TransactionHistory
declare module './components/TransactionHistory' {
  const component: React.FC<{ transactions: any[] }>;
  export default component;
}

// Type declaration for WebSocketStatus
declare module './components/WebSocketStatus' {
  const component: React.FC<{ connected: boolean; mode: 'LIVE' | 'FALLBACK_REST' }>;
  export default component;
}
