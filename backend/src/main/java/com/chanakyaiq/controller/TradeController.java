package com.chanakyaiq.controller;

import com.chanakyaiq.service.api.TradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/trade")
public class TradeController {

    @Autowired
    private TradeService tradeService;

    public static class OrderRequest {
        public String symbol;
        public int quantity;
    }

    @PostMapping("/buy")
    public ResponseEntity<Map<String, Object>> buyStock(
            @AuthenticationPrincipal OAuth2User oauth2User,
            @RequestBody OrderRequest request) {
        
        Map<String, Object> response = new HashMap<>();
        if (oauth2User == null) {
            response.put("error", "Unauthorized");
            return ResponseEntity.status(401).body(response);
        }

        String userId = oauth2User.getAttribute("sub");
        try {
            tradeService.executeBuyOrder(userId, request.symbol, request.quantity);
            response.put("success", true);
            response.put("message", "Market BUY order executed successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/sell")
    public ResponseEntity<Map<String, Object>> sellStock(
            @AuthenticationPrincipal OAuth2User oauth2User,
            @RequestBody OrderRequest request) {

        Map<String, Object> response = new HashMap<>();
        if (oauth2User == null) {
            response.put("error", "Unauthorized");
            return ResponseEntity.status(401).body(response);
        }

        String userId = oauth2User.getAttribute("sub");
        try {
            tradeService.executeSellOrder(userId, request.symbol, request.quantity);
            response.put("success", true);
            response.put("message", "Market SELL order executed successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
