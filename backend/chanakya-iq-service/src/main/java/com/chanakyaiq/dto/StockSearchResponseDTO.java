package com.chanakyaiq.dto;

public record StockSearchResponseDTO(
        String symbol, // Will hold the instrument_key
        String name
) {}
