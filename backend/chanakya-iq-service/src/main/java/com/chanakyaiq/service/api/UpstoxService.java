package com.chanakyaiq.service.api;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Service layer interface for market data and trading operations.
 */
public interface UpstoxService {
    /**
     * Checks whether the Indian stock market is currently open.
     *
     * @return true if open, false otherwise
     */
    boolean isMarketOpen();

    /**
     * Returns the current price for the given stock symbol.
     *
     * @param symbol stock ticker (e.g., "RELIANCE")
     * @return current price as {@link BigDecimal}
     */
    BigDecimal getStockPrice(String symbol);

    /**
     * Searches available stocks by a query string.
     *
     * @param query partial symbol or name
     * @return list of search responses
     */
    List<com.chanakyaiq.dto.StockSearchResponseDTO> searchStocks(String query);

    /**
     * Retrieves detailed information for a specific stock.
     *
     * @param instrumentKey stock identifier
     * @return detailed fields including price and market status
     */
    com.chanakyaiq.dto.StockDetailsDTO getStockDetails(String instrumentKey);

    /**
     * Generates a list of historical price points for charting.
     *
     * @param symbol stock ticker
     * @return list of {@link BigDecimal} prices (oldest to newest)
     */
    List<BigDecimal> getHistoricalPrices(String symbol);
}
