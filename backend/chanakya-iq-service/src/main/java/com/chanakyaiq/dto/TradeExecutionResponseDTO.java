package com.chanakyaiq.dto;

import java.math.BigDecimal;

public record TradeExecutionResponseDTO(
        boolean success,
        String message,
        String transactionType,
        String symbol,
        int quantity,
        BigDecimal executedPrice,
        BigDecimal totalAmount,
        BigDecimal remainingCashBalance
) {}
