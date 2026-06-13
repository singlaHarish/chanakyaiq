/**
 * Service layer interface for trade operations.
 *
 * Provides methods to execute BUY and SELL orders for a given user.
 */
package com.chanakyaiq.service.api;

public interface TradeService {
    /**
     * Execute a market BUY order.
     *
     * @param userId   the unique identifier of the user (OAuth2 sub claim)
     * @param symbol   stock symbol (e.g., "RELIANCE")
     * @param quantity number of shares to purchase (must be > 0)
     */
    void executeBuyOrder(String userId, String symbol, int quantity);

    /**
     * Execute a market SELL order.
     *
     * @param userId   the unique identifier of the user
     * @param symbol   stock symbol
     * @param quantity number of shares to sell (must be > 0)
     */
    void executeSellOrder(String userId, String symbol, int quantity);
}
