package com.chanakyaiq.service.impl;

import com.chanakyaiq.dto.TradeExecutionResponseDTO;
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
    public TradeExecutionResponseDTO executeBuyOrder(String userId, String symbol, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        BigDecimal price = upstoxService.getStockPrice(symbol);
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException("Unable to retrieve live market price for trade execution. Please try again.");
        }

        BigDecimal totalCost = price.multiply(BigDecimal.valueOf(quantity)).setScale(2, RoundingMode.HALF_UP);
        if (user.getCashBalance().compareTo(totalCost) < 0) {
            throw new IllegalStateException("Insufficient cash balance. Required: ₹" + totalCost + ", Available: ₹" + user.getCashBalance());
        }

        // Deduct totalCost from user cash balance
        user.setCashBalance(user.getCashBalance().subtract(totalCost));
        userRepository.save(user);

        // Update holdings
        Optional<Holding> existingHoldingOpt = holdingRepository.findByUserIdAndSymbol(userId, symbol.toUpperCase());
        if (existingHoldingOpt.isPresent()) {
            Holding holding = existingHoldingOpt.get();
            if (holding.getQuantity() == 0) {
                holding.setQuantity(quantity);
                holding.setAveragePrice(price);
            } else {
                int newQty = holding.getQuantity() + quantity;
                BigDecimal oldCost = holding.getAveragePrice().multiply(BigDecimal.valueOf(holding.getQuantity()));
                BigDecimal newAveragePrice = oldCost.add(totalCost).divide(BigDecimal.valueOf(newQty), 2, RoundingMode.HALF_UP);
                holding.setQuantity(newQty);
                holding.setAveragePrice(newAveragePrice);
            }
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

        // Record Transaction
        Transaction transaction = Transaction.builder()
                .userId(userId)
                .symbol(symbol.toUpperCase())
                .type("BUY")
                .quantity(quantity)
                .price(price)
                .timestamp(LocalDateTime.now())
                .build();
        transactionRepository.save(transaction);

        return new TradeExecutionResponseDTO(
                true,
                "Market BUY order executed successfully",
                "BUY",
                symbol.toUpperCase(),
                quantity,
                price.setScale(2, RoundingMode.HALF_UP),
                totalCost,
                user.getCashBalance().setScale(2, RoundingMode.HALF_UP)
        );
    }

    @Transactional
    @Override
    public TradeExecutionResponseDTO executeSellOrder(String userId, String symbol, int quantity) {
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
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException("Unable to retrieve live market price for trade execution. Please try again.");
        }

        BigDecimal totalProceeds = price.multiply(BigDecimal.valueOf(quantity)).setScale(2, RoundingMode.HALF_UP);

        // Add proceeds to user cash balance
        user.setCashBalance(user.getCashBalance().add(totalProceeds));
        userRepository.save(user);

        // Update holding quantity - DO NOT delete holding record when quantity becomes 0
        holding.setQuantity(holding.getQuantity() - quantity);
        holdingRepository.save(holding);

        // Record Transaction
        Transaction transaction = Transaction.builder()
                .userId(userId)
                .symbol(symbol.toUpperCase())
                .type("SELL")
                .quantity(quantity)
                .price(price)
                .timestamp(LocalDateTime.now())
                .build();
        transactionRepository.save(transaction);

        return new TradeExecutionResponseDTO(
                true,
                "Market SELL order executed successfully",
                "SELL",
                symbol.toUpperCase(),
                quantity,
                price.setScale(2, RoundingMode.HALF_UP),
                totalProceeds,
                user.getCashBalance().setScale(2, RoundingMode.HALF_UP)
        );
    }
}
