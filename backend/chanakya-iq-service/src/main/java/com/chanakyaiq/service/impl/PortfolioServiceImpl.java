package com.chanakyaiq.service.impl;

import com.chanakyaiq.model.Holding;
import com.chanakyaiq.model.User;
import com.chanakyaiq.repository.HoldingRepository;
import com.chanakyaiq.repository.UserRepository;
import com.chanakyaiq.service.api.PortfolioService;
import com.chanakyaiq.service.api.UpstoxService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of the {@link PortfolioService} interface.
 * Provides aggregation of a user's portfolio data.
 */
@Service
@RequiredArgsConstructor
public class PortfolioServiceImpl implements PortfolioService {

    private final HoldingRepository holdingRepository;
    private final UserRepository userRepository;
    private final UpstoxService upstoxService;

    @Override
    public Map<String, Object> getPortfolioSummary(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<Holding> holdings = holdingRepository.findByUserId(userId);
        List<Map<String, Object>> holdingsDetails = new ArrayList<>();

        BigDecimal totalInvested = BigDecimal.ZERO;
        BigDecimal totalCurrentValue = BigDecimal.ZERO;

        for (Holding holding : holdings) {
            BigDecimal currentPrice = upstoxService.getStockPrice(holding.getSymbol());
            BigDecimal costBasis = holding.getAveragePrice().multiply(BigDecimal.valueOf(holding.getQuantity()));
            BigDecimal currentValue = currentPrice.multiply(BigDecimal.valueOf(holding.getQuantity()));

            totalInvested = totalInvested.add(costBasis);
            totalCurrentValue = totalCurrentValue.add(currentValue);

            BigDecimal profitLoss = currentValue.subtract(costBasis);
            BigDecimal profitLossPercent = BigDecimal.ZERO;
            if (costBasis.compareTo(BigDecimal.ZERO) > 0) {
                profitLossPercent = profitLoss.divide(costBasis, 4, RoundingMode.HALF_UP)
                        .multiply(new BigDecimal("100"));
            }

            Map<String, Object> detail = new HashMap<>();
            detail.put("symbol", holding.getSymbol());
            detail.put("quantity", holding.getQuantity());
            detail.put("averagePrice", holding.getAveragePrice());
            detail.put("currentPrice", currentPrice);
            detail.put("investedAmount", costBasis.setScale(2, RoundingMode.HALF_UP));
            detail.put("currentValue", currentValue.setScale(2, RoundingMode.HALF_UP));
            detail.put("profitLoss", profitLoss.setScale(2, RoundingMode.HALF_UP));
            detail.put("profitLossPercent", profitLossPercent.setScale(2, RoundingMode.HALF_UP));

            holdingsDetails.add(detail);
        }

        BigDecimal overallProfitLoss = totalCurrentValue.subtract(totalInvested);
        BigDecimal overallProfitLossPercent = BigDecimal.ZERO;
        if (totalInvested.compareTo(BigDecimal.ZERO) > 0) {
            overallProfitLossPercent = overallProfitLoss.divide(totalInvested, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"));
        }

        BigDecimal totalPortfolioValue = totalCurrentValue.add(user.getCashBalance());

        Map<String, Object> summary = new HashMap<>();
        summary.put("holdings", holdingsDetails);
        summary.put("cashBalance", user.getCashBalance().setScale(2, RoundingMode.HALF_UP));
        summary.put("totalInvested", totalInvested.setScale(2, RoundingMode.HALF_UP));
        summary.put("totalCurrentValue", totalCurrentValue.setScale(2, RoundingMode.HALF_UP));
        summary.put("overallProfitLoss", overallProfitLoss.setScale(2, RoundingMode.HALF_UP));
        summary.put("overallProfitLossPercent", overallProfitLossPercent.setScale(2, RoundingMode.HALF_UP));
        summary.put("totalPortfolioValue", totalPortfolioValue.setScale(2, RoundingMode.HALF_UP));

        return summary;
    }
}
