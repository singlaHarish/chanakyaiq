package com.chanakyaiq.dto;

import java.math.BigDecimal;

public record StockCandleDTO(
        String timestamp,
        BigDecimal open,
        BigDecimal high,
        BigDecimal low,
        BigDecimal close,
        Long volume
) {}
