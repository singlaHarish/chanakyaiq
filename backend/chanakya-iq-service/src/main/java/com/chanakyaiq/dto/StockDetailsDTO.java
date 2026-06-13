package com.chanakyaiq.dto;

import java.math.BigDecimal;

public record StockDetailsDTO(
        String symbol,
        String name,
        BigDecimal price,
        BigDecimal change,
        BigDecimal changePercent,
        boolean isMarketOpen
) {}
