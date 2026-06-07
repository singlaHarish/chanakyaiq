package com.chanakyaiq.controller;

import com.chanakyaiq.service.api.UpstoxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stocks")
public class StockController {

    @Autowired
    private UpstoxService upstoxService;

    @GetMapping("/search")
    public ResponseEntity<List<Map<String, Object>>> searchStocks(@RequestParam String query) {
        return ResponseEntity.ok(upstoxService.searchStocks(query));
    }

    @GetMapping("/price/{symbol}")
    public ResponseEntity<Map<String, Object>> getStockDetails(@PathVariable String symbol) {
        return ResponseEntity.ok(upstoxService.getStockDetails(symbol));
    }

    @GetMapping("/history/{symbol}")
    public ResponseEntity<List<BigDecimal>> getHistoricalPrices(@PathVariable String symbol) {
        return ResponseEntity.ok(upstoxService.getHistoricalPrices(symbol));
    }
}
