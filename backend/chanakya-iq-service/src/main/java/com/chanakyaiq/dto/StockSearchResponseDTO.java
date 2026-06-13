package com.chanakyaiq.dto;

public record StockSearchResponseDTO(
        String instrumentKey,
        String tradingSymbol,
        String name
) {}
