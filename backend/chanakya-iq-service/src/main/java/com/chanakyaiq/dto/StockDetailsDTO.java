package com.chanakyaiq.dto;

import java.math.BigDecimal;

public record StockDetailsDTO(
        String instrumentKey,
        String symbol,
        String name,
        BigDecimal lastPrice,
        BigDecimal netChange,
        BigDecimal changePercent,
        BigDecimal open,
        BigDecimal high,
        BigDecimal low,
        BigDecimal close,
        Integer volume,
        BigDecimal averagePrice,
        boolean isMarketOpen
) {}
