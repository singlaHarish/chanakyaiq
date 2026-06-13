package com.chanakyaiq.controller;

import com.chanakyaiq.service.api.UpstoxService;
import com.chanakyaiq.dto.StockDetailsDTO;
import com.chanakyaiq.dto.StockSearchResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
public class StockController {

    private final UpstoxService upstoxService;

    @GetMapping("/search")
    public ResponseEntity<List<StockSearchResponseDTO>> searchStocks(@RequestParam String query) {
        return ResponseEntity.ok(upstoxService.searchStocks(query));
    }

    @GetMapping("/price/{symbol}")
    public ResponseEntity<StockDetailsDTO> getStockDetails(@PathVariable String symbol) {
        return ResponseEntity.ok(upstoxService.getStockDetails(symbol));
    }

    @GetMapping("/history/{symbol}")
    public ResponseEntity<List<BigDecimal>> getHistoricalPrices(@PathVariable String symbol) {
        return ResponseEntity.ok(upstoxService.getHistoricalPrices(symbol));
    }
}
