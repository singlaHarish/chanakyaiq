import React from 'react';

interface WebSocketStatusProps {
  connected: boolean;
  mode: 'LIVE' | 'FALLBACK_REST';
}

export default function WebSocketStatus({ connected, mode }: WebSocketStatusProps) {
  if (connected && mode === 'LIVE') {
    return (
      <div className="websocket-status connected">
        <span className="status-dot live"></span>
        <span>Live Prices (Real-time)</span>
      </div>
    );
  }
  
  return (
    <div className="websocket-status disconnected">
      <span className="status-dot degraded"></span>
      <span>Degraded Mode (Cached Data)</span>
    </div>
  );
}
