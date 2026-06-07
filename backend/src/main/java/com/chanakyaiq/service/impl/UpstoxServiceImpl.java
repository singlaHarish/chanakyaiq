package com.chanakyaiq.service.impl;

import com.chanakyaiq.service.api.UpstoxService;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Implementation of the {@link UpstoxService} interface.
 * Provides mock market data and price simulation.
 */
@Service
@RequiredArgsConstructor
public class UpstoxServiceImpl implements UpstoxService {

    private final Map<String, StockInfo> stockDatabase = new HashMap<>();
    private final Random random = new Random();
    private final ZoneId IST_ZONE = ZoneId.of("Asia/Kolkata");

    public static class StockInfo {
        public String symbol;
        public String name;
        public BigDecimal currentPrice;
        public BigDecimal dayOpen;
        public BigDecimal previousClose;

        public StockInfo(String symbol, String name, BigDecimal basePrice) {
            this.symbol = symbol;
            this.name = name;
            this.currentPrice = basePrice;
            this.dayOpen = basePrice;
            this.previousClose = basePrice.multiply(new BigDecimal("0.99")); // slightly lower close
        }
    }

    @Override
    public boolean isMarketOpen() {
        ZonedDateTime nowIST = ZonedDateTime.now(IST_ZONE);
        DayOfWeek day = nowIST.getDayOfWeek();
        if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
            return false;
        }
        LocalTime time = nowIST.toLocalTime();
        LocalTime marketOpen = LocalTime.of(9, 15);
        LocalTime marketClose = LocalTime.of(15, 30);
        return !time.isBefore(marketOpen) && !time.isAfter(marketClose);
    }

    @Override
    public synchronized BigDecimal getStockPrice(String symbol) {
        String cleanSymbol = symbol.toUpperCase().trim();
        StockInfo info = stockDatabase.get(cleanSymbol);
        if (info == null) {
            // Create a dynamic new stock if not in database to allow flexibility
            BigDecimal randomBase = new BigDecimal(100 + random.nextInt(1900));
            info = new StockInfo(cleanSymbol, cleanSymbol + " Ltd.", randomBase);
            stockDatabase.put(cleanSymbol, info);
        }
        if (isMarketOpen()) {
            double changePercent = (random.nextDouble() - 0.5) * 0.004; // max +-0.2% tick change
            BigDecimal multiplier = BigDecimal.valueOf(1.0 + changePercent);
            info.currentPrice = info.currentPrice.multiply(multiplier).setScale(2, RoundingMode.HALF_UP);
        }
        return info.currentPrice;
    }

    @Override
    public List<Map<String, Object>> searchStocks(String query) {
        String lowerQuery = query.toLowerCase().trim();
        List<Map<String, Object>> results = new ArrayList<>();
        for (StockInfo info : stockDatabase.values()) {
            if (info.symbol.toLowerCase().contains(lowerQuery) || info.name.toLowerCase().contains(lowerQuery)) {
                BigDecimal price = getStockPrice(info.symbol);
                Map<String, Object> map = new HashMap<>();
                map.put("symbol", info.symbol);
                map.put("name", info.name);
                map.put("price", price);
                BigDecimal changeAmount = price.subtract(info.previousClose);
                BigDecimal changePercent = changeAmount.divide(info.previousClose, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));
                map.put("change", changeAmount.setScale(2, RoundingMode.HALF_UP));
                map.put("changePercent", changePercent.setScale(2, RoundingMode.HALF_UP));
                results.add(map);
            }
        }
        return results;
    }

    @Override
    public Map<String, Object> getStockDetails(String symbol) {
        String cleanSymbol = symbol.toUpperCase().trim();
        BigDecimal price = getStockPrice(cleanSymbol);
        StockInfo info = stockDatabase.get(cleanSymbol);
        Map<String, Object> details = new HashMap<>();
        details.put("symbol", info.symbol);
        details.put("name", info.name);
        details.put("price", price);
        BigDecimal changeAmount = price.subtract(info.previousClose);
        BigDecimal changePercent = changeAmount.divide(info.previousClose, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));
        details.put("change", changeAmount.setScale(2, RoundingMode.HALF_UP));
        details.put("changePercent", changePercent.setScale(2, RoundingMode.HALF_UP));
        details.put("isMarketOpen", isMarketOpen());
        return details;
    }

    @Override
    public List<BigDecimal> getHistoricalPrices(String symbol) {
        BigDecimal currentPrice = getStockPrice(symbol);
        List<BigDecimal> list = new ArrayList<>();
        BigDecimal temp = currentPrice;
        for (int i = 0; i < 20; i++) {
            list.add(0, temp.setScale(2, RoundingMode.HALF_UP));
            double changePercent = (random.nextDouble() - 0.49) * 0.015; // slightly skewed
            temp = temp.multiply(BigDecimal.valueOf(1.0 - changePercent));
        }
        return list;
    }
}
