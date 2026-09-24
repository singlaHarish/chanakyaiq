package com.chanakyaiq.websocket;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.chanakyaiq.cache.PriceCache;
import com.chanakyaiq.config.ChanakyaIqProperties;
import com.chanakyaiq.dto.StockDetailsDTO;
import com.chanakyaiq.model.Holding;
import com.chanakyaiq.model.User;
import com.chanakyaiq.repository.HoldingRepository;
import com.chanakyaiq.repository.UserRepository;
import com.chanakyaiq.service.SseEmitterRepository;
import com.chanakyaiq.service.api.UpstoxService;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class UpstoxWebSocketManager {
    
    private final PriceCache priceCache;
    private final SseEmitterRepository sseEmitterRepository;
    private final UpstoxService upstoxService;
    private final ChanakyaIqProperties properties;
    private final UserRepository userRepository;
    private final HoldingRepository holdingRepository;
    
    private WebSocketClient webSocketClient;
    private final Set<String> subscribedInstruments = new CopyOnWriteArraySet<>();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    
    private boolean connected = false;
    private int reconnectAttempts = 0;
    private static final int MAX_RECONNECT_ATTEMPTS = 5;
    private static final long INITIAL_RECONNECT_DELAY = 2L;
    private final Object reconnectLock = new Object();
    
    @Autowired
    public UpstoxWebSocketManager(
            PriceCache priceCache,
            SseEmitterRepository sseEmitterRepository,
            UpstoxService upstoxService,
            ChanakyaIqProperties properties,
            UserRepository userRepository,
            HoldingRepository holdingRepository) {
        this.priceCache = priceCache;
        this.sseEmitterRepository = sseEmitterRepository;
        this.upstoxService = upstoxService;
        this.properties = properties;
        this.userRepository = userRepository;
        this.holdingRepository = holdingRepository;
    }
    
    public void start() {
        log.info("Starting Upstox WebSocket connection...");
        connect();
        
        // Subscribe to all user holdings after initial connection (hybrid approach)
        scheduler.schedule(this::subscribeToAllUserHoldings, 5, TimeUnit.SECONDS);
        
        // Check connection every 30 seconds
        scheduler.scheduleAtFixedRate(this::checkConnection, 30, 30, TimeUnit.SECONDS);
        
        // Refresh subscriptions every 5 minutes (for new users)
        scheduler.scheduleAtFixedRate(this::subscribeToAllUserHoldings, 300, 300, TimeUnit.SECONDS);
    }
    
    private void connect() {
        synchronized (reconnectLock) {
            if (webSocketClient != null && webSocketClient.isOpen()) {
                log.debug("WebSocket already connected");
                return;
            }
            
            try {
                String token = properties.getApi().getToken();
                String authorizeUrl = "https://api.upstox.com/v3/feed/market-data-feed/authorize";
                
                // Get authorized redirect URI from Upstox
                String websocketUrl = getAuthorizedWebSocketUrl(authorizeUrl, token);
                if (websocketUrl == null) {
                    log.error("Failed to get WebSocket URL from authorize endpoint");
                    scheduleReconnect();
                    return;
                }
                
                log.info("Connecting to WebSocket: {}", websocketUrl);
                
                URI uri = new URI(websocketUrl);
                
                webSocketClient = new WebSocketClient(uri) {
                    @Override
                    public void onOpen(ServerHandshake handshakedata) {
                        log.info("WebSocket opened");
                        connected = true;
                        reconnectAttempts = 0;
                        
                        // Re-subscribe to instruments
                        if (!subscribedInstruments.isEmpty()) {
                            subscribe(new ArrayList<>(subscribedInstruments));
                        }
                        
                        // Notify frontend of connection status
                        sseEmitterRepository.broadcast("websocket_status", Map.of(
                            "connected", true,
                            "mode", "LIVE"
                        ));
                    }
                    
                    @Override
                    public void onMessage(String message) {
                        log.warn("Received text message (expected binary): {}", message);
                    }
                    
                    @Override
                    public void onMessage(java.nio.ByteBuffer message) {
                        handleBinaryMessage(message.array());
                    }
                    
                    @Override
                    public void onClose(int code, String reason, boolean remote) {
                        log.info("WebSocket closed: code={}, reason={}", code, reason);
                        connected = false;
                        notifyFrontendDisconnected();
                        scheduleReconnect();
                    }
                    
                    @Override
                    public void onError(Exception ex) {
                        log.error("WebSocket error", ex);
                        connected = false;
                        notifyFrontendDisconnected();
                        scheduleReconnect();
                    }
                };
                
                webSocketClient.connect();
                
            } catch (Exception e) {
                log.error("Failed to connect to WebSocket", e);
                scheduleReconnect();
            }
        }
    }
    
    private String getAuthorizedWebSocketUrl(String url, String token) {
        try {
            // Use Spring RestClient (existing in UpstoxServiceImpl)
            var restClient = upstoxService.getRestClient();
            var response = restClient.get()
                    .uri(url)
                    .header("Accept", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .body(Map.class);
            
            log.debug("Authorize endpoint response: {}", response);
            
            if (response == null) {
                log.error("Authorize endpoint returned null response");
                return null;
            }
            
            // Check if status is success
            Object status = response.get("status");
            if (!"success".equals(status)) {
                log.error("Authorize endpoint returned non-success status: {}", status);
                return null;
            }
            
            // Get data object
            Object data = response.get("data");
            if (data instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> dataMap = (Map<String, Object>) data;
                
                // Try both camelCase and snake_case variants
                Object urlFromResponse = dataMap.get("authorizedRedirectUri");
                if (urlFromResponse == null) {
                    urlFromResponse = dataMap.get("authorized_redirect_uri");
                }
                
                if (urlFromResponse instanceof String) {
                    return (String) urlFromResponse;
                }
            }
            
            log.error("Failed to get WebSocket URL from response data. Response: {}", response);
            return null;
            
        } catch (Exception e) {
            log.error("Error calling authorize endpoint", e);
            return null;
        }
    }
    
    private void handleBinaryMessage(byte[] data) {
        try {
            // For now, log the raw data
            // In production, you'd parse Protobuf using the MarketDataFeed.proto file
            log.debug("Received binary message ({} bytes)", data.length);
            
            // Temporary: Use Upstox REST API to get current prices when WebSocket receives data
            // This ensures data flows to frontend even without Protobuf parsing
            for (String instrumentKey : subscribedInstruments) {
                StockDetailsDTO priceData = upstoxService.getStockDetails(instrumentKey);
                if (priceData != null) {
                    priceCache.update(instrumentKey, priceData);
                    sseEmitterRepository.broadcast("price_update", Map.of(
                        "instrumentKey", instrumentKey,
                        "price", priceData
                    ));
                }
            }
            
        } catch (Exception e) {
            log.error("Error handling binary message", e);
        }
    }
    
    private void subscribeToAllUserHoldings() {
        try {
            log.info("Fetching all user holdings to subscribe to instruments...");
            
            // Get all users from database
            List<User> allUsers = userRepository.findAll();
            log.debug("Found {} users", allUsers.size());
            
            // Collect all unique instrument keys from all users' holdings
            Set<String> allInstruments = new HashSet<>();
            for (User user : allUsers) {
                List<Holding> holdings = holdingRepository.findByUserId(user.getId());
                for (Holding holding : holdings) {
                    allInstruments.add(holding.getSymbol());
                }
            }
            
            log.info("Found {} unique instruments from {} users' holdings", 
                    allInstruments.size(), allUsers.size());
            
            if (!allInstruments.isEmpty()) {
                subscribe(new ArrayList<>(allInstruments));
            }
            
        } catch (Exception e) {
            log.error("Error fetching user holdings for subscription", e);
        }
    }
    
    public void subscribe(List<String> instrumentKeys) {
        if (instrumentKeys.isEmpty()) return;
        
        List<String> newInstruments = new ArrayList<>();
        for (String key : instrumentKeys) {
            if (!subscribedInstruments.contains(key)) {
                newInstruments.add(key);
                subscribedInstruments.add(key);
            }
        }
        
        if (newInstruments.isEmpty()) {
            log.debug("All instruments already subscribed");
            return;
        }
        
        log.info("Subscribing to {} new instruments", newInstruments.size());
        
        if (connected && webSocketClient != null && webSocketClient.isOpen()) {
            String subscriptionMessage = buildSubscriptionMessage(newInstruments);
            webSocketClient.send(subscriptionMessage);
        }
    }
    
    public void subscribe(String instrumentKey) {
        subscribe(Collections.singletonList(instrumentKey));
    }
    
    private String buildSubscriptionMessage(List<String> instrumentKeys) {
        Map<String, Object> message = new HashMap<>();
        message.put("guid", UUID.randomUUID().toString());
        message.put("method", "sub");
        message.put("data", Map.of(
            "mode", "LTPC",
            "instrumentKeys", instrumentKeys
        ));
        
        try {
            return new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(message);
        } catch (Exception e) {
            log.error("Error serializing subscription message", e);
            return "{}";
        }
    }
    
    private void scheduleReconnect() {
        synchronized (reconnectLock) {
            if (reconnectAttempts >= MAX_RECONNECT_ATTEMPTS) {
                log.error("Max reconnection attempts reached. Using REST fallback.");
                notifyFrontendDisconnected();
                return;
            }
            
            long delay = INITIAL_RECONNECT_DELAY * (long) Math.pow(2, reconnectAttempts);
            log.info("Scheduling reconnection in {} seconds (attempt {}/{})", 
                    delay, reconnectAttempts + 1, MAX_RECONNECT_ATTEMPTS);
            
            scheduler.schedule(() -> {
                reconnectAttempts++;
                connect();
            }, delay, TimeUnit.SECONDS);
        }
    }
    
    public void checkConnection() {
        synchronized (reconnectLock) {
            if (!connected && reconnectAttempts < MAX_RECONNECT_ATTEMPTS) {
                log.info("WebSocket connection lost. Attempting to reconnect...");
                connect();
            }
        }
    }
    
    private void notifyFrontendDisconnected() {
        sseEmitterRepository.broadcast("websocket_status", Map.of(
            "connected", false,
            "mode", "FALLBACK_REST"
        ));
    }
    
    public boolean isConnected() {
        return connected && webSocketClient != null && webSocketClient.isOpen();
    }
    
    public StockDetailsDTO getCachedPrice(String instrumentKey) {
        return priceCache.get(instrumentKey);
    }
    
    public Map<String, StockDetailsDTO> getAllCachedPrices() {
        return priceCache.getAll();
    }
    
    public Set<String> getSubscribedInstruments() {
        return subscribedInstruments;
    }
}
