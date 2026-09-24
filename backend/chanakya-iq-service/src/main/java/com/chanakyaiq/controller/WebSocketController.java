package com.chanakyaiq.controller;

import com.chanakyaiq.dto.StockDetailsDTO;
import com.chanakyaiq.websocket.UpstoxWebSocketManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Log4j2
@RestController
@RequestMapping("/api/websocket")
@RequiredArgsConstructor
public class WebSocketController {
    
    private final UpstoxWebSocketManager webSocketManager;
    
    @GetMapping("/status")
    public Map<String, Object> getStatus() {
        Map<String, Object> response = new HashMap<>();
        response.put("connected", webSocketManager.isConnected());
        response.put("mode", webSocketManager.isConnected() ? "LIVE" : "FALLBACK_REST");
        response.put("subscribedInstruments", webSocketManager.getSubscribedInstruments().size());
        return response;
    }
    
    @GetMapping("/prices")
    public Map<String, StockDetailsDTO> getAllPrices() {
        return webSocketManager.getAllCachedPrices();
    }
    
    @PostMapping("/subscribe")
    public Map<String, Object> subscribe(@RequestBody Map<String, Object> request) {
        Object instrumentsObj = request.get("instrumentKeys");
        if (!(instrumentsObj instanceof java.util.List)) {
            return Map.of("success", false, "error", "instrumentKeys must be a list");
        }
        
        @SuppressWarnings("unchecked")
        java.util.List<String> instruments = (java.util.List<String>) instrumentsObj;
        
        if (instruments.isEmpty()) {
            return Map.of("success", false, "error", "No instruments specified");
        }
        
        webSocketManager.subscribe(instruments);
        return Map.of("success", true, "subscribed", instruments.size());
    }
    
    @GetMapping("/subscribe/{instrumentKey}")
    public Map<String, Object> subscribe(@PathVariable String instrumentKey) {
        webSocketManager.subscribe(instrumentKey);
        return Map.of("success", true, "subscribed", instrumentKey);
    }
}
