package com.chanakyaiq.service.api;

import java.util.Map;

/**
 * Service interface for portfolio aggregation.
 */
public interface PortfolioService {
    /**
     * Returns a summary of the user's portfolio.
     *
     * @param userId the user's identifier
     * @return map containing portfolio details
     */
    Map<String, Object> getPortfolioSummary(String userId);
}
