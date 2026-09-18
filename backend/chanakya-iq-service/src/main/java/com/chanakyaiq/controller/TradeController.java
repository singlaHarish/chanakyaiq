package com.chanakyaiq.controller;

import com.chanakyaiq.dto.TradeExecutionResponseDTO;
import com.chanakyaiq.dto.TradeOrderRequestDTO;
import com.chanakyaiq.service.api.TradeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequestMapping("/api/trade")
@RequiredArgsConstructor
public class TradeController {

    private final TradeService tradeService;

    @PostMapping("/buy")
    public ResponseEntity<TradeExecutionResponseDTO> buyStock(
            @AuthenticationPrincipal OAuth2User oauth2User,
            @RequestBody TradeOrderRequestDTO request) {

        if (oauth2User == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String userId = oauth2User.getAttribute("sub");
        log.info("Processing BUY order for user {}: symbol={}, quantity={}", userId, request.symbol(), request.quantity());
        
        TradeExecutionResponseDTO response = tradeService.executeBuyOrder(userId, request.symbol(), request.quantity());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/sell")
    public ResponseEntity<TradeExecutionResponseDTO> sellStock(
            @AuthenticationPrincipal OAuth2User oauth2User,
            @RequestBody TradeOrderRequestDTO request) {

        if (oauth2User == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String userId = oauth2User.getAttribute("sub");
        log.info("Processing SELL order for user {}: symbol={}, quantity={}", userId, request.symbol(), request.quantity());

        TradeExecutionResponseDTO response = tradeService.executeSellOrder(userId, request.symbol(), request.quantity());
        return ResponseEntity.ok(response);
    }
}
