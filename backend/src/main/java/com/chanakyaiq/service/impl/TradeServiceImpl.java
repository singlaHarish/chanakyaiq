package com.chanakyaiq.service.impl;

import com.chanakyaiq.model.Holding;
import com.chanakyaiq.model.Transaction;
import com.chanakyaiq.model.User;
import com.chanakyaiq.repository.HoldingRepository;
import com.chanakyaiq.repository.TransactionRepository;
import com.chanakyaiq.repository.UserRepository;
import com.chanakyaiq.service.api.TradeService;
import com.chanakyaiq.service.api.UpstoxService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Implementation of the {@link TradeService} interface.
 * Provides business logic for executing BUY and SELL orders.
 */
@Service
@RequiredArgsConstructor
public class TradeServiceImpl implements TradeService {

    private final UserRepository userRepository;
    private final HoldingRepository holdingRepository;
    private final TransactionRepository transactionRepository;
    private final UpstoxService upstoxService;

    @Transactional
    @Override
    public void executeBuyOrder(String userId, String symbol, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        BigDecimal price = upstoxService.getStockPrice(symbol);
        BigDecimal totalCost = price.multiply(BigDecimal.valueOf(quantity));
        if (user.getCashBalance().compareTo(totalCost) < 0) {
            throw new IllegalStateException("Insufficient funds. Required: ₹" + totalCost + ", Available: ₹" + user.getCashBalance());
        }
        // Deduct cash balance
        user.setCashBalance(user.getCashBalance().subtract(totalCost));
        userRepository.save(user);
        // Update holdings
        Optional<Holding> existingHoldingOpt = holdingRepository.findByUserIdAndSymbol(userId, symbol.toUpperCase());
        if (existingHoldingOpt.isPresent()) {
            Holding holding = existingHoldingOpt.get();
            int newQty = holding.getQuantity() + quantity;
            BigDecimal oldCost = holding.getAveragePrice().multiply(BigDecimal.valueOf(holding.getQuantity()));
            BigDecimal newAveragePrice = oldCost.add(totalCost).divide(BigDecimal.valueOf(newQty), 2, RoundingMode.HALF_UP);
            holding.setQuantity(newQty);
            holding.setAveragePrice(newAveragePrice);
            holdingRepository.save(holding);
        } else {
            Holding newHolding = Holding.builder()
                    .userId(userId)
                    .symbol(symbol.toUpperCase())
                    .quantity(quantity)
                    .averagePrice(price)
                    .build();
            holdingRepository.save(newHolding);
        }
        // Log transaction
        Transaction transaction = Transaction.builder()
                .userId(userId)
                .symbol(symbol.toUpperCase())
                .type("BUY")
                .quantity(quantity)
                .price(price)
                .timestamp(LocalDateTime.now())
                .build();
        transactionRepository.save(transaction);
    }

    @Transactional
    @Override
    public void executeSellOrder(String userId, String symbol, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Holding holding = holdingRepository.findByUserIdAndSymbol(userId, symbol.toUpperCase())
                .orElseThrow(() -> new IllegalArgumentException("You do not own any shares of " + symbol));
        if (holding.getQuantity() < quantity) {
            throw new IllegalStateException("Insufficient shares to sell. Available: " + holding.getQuantity() + ", Requested: " + quantity);
        }
        BigDecimal price = upstoxService.getStockPrice(symbol);
        BigDecimal totalProceeds = price.multiply(BigDecimal.valueOf(quantity));
        // Add proceeds to cash balance
        user.setCashBalance(user.getCashBalance().add(totalProceeds));
        userRepository.save(user);
        // Update holdings
        int remainingQty = holding.getQuantity() - quantity;
        if (remainingQty == 0) {
            holdingRepository.delete(holding);
        } else {
            holding.setQuantity(remainingQty);
            holdingRepository.save(holding);
        }
        // Log transaction
        Transaction transaction = Transaction.builder()
                .userId(userId)
                .symbol(symbol.toUpperCase())
                .type("SELL")
                .quantity(quantity)
                .price(price)
                .timestamp(LocalDateTime.now())
                .build();
        transactionRepository.save(transaction);
    }
}
