package com.chanakyaiq.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.chanakyaiq.api.model.UpstoxInstrument;
import com.chanakyaiq.api.model.UpstoxQuoteData;
import com.chanakyaiq.api.model.UpstoxQuoteResponse;
import com.chanakyaiq.api.model.UpstoxSearchResponse;
import com.chanakyaiq.config.ChanakyaIqProperties;
import static com.chanakyaiq.constants.AppConstants.API_BASE_URL;
import static com.chanakyaiq.constants.AppConstants.DEFAULT_PRICE;
import static com.chanakyaiq.constants.AppConstants.EXCHANGE_NSE;
import static com.chanakyaiq.constants.AppConstants.HISTORICAL_DATA_POINTS;
import static com.chanakyaiq.constants.AppConstants.INSTRUMENT_SEARCH_ENDPOINT;
import static com.chanakyaiq.constants.AppConstants.INSTRUMENT_TYPE_EQ;
import static com.chanakyaiq.constants.AppConstants.MARKET_CLOSE_HOUR;
import static com.chanakyaiq.constants.AppConstants.MARKET_CLOSE_MINUTE;
import static com.chanakyaiq.constants.AppConstants.MARKET_OPEN_HOUR;
import static com.chanakyaiq.constants.AppConstants.MARKET_OPEN_MINUTE;
import static com.chanakyaiq.constants.AppConstants.MARKET_QUOTE_ENDPOINT;
import static com.chanakyaiq.constants.AppConstants.PERCENTAGE_MULTIPLIER;
import static com.chanakyaiq.constants.AppConstants.PERCENTAGE_SCALE;
import static com.chanakyaiq.constants.AppConstants.PRICE_SCALE;
import static com.chanakyaiq.constants.AppConstants.QUERY_PARAM_EXCHANGE;
import static com.chanakyaiq.constants.AppConstants.QUERY_PARAM_INSTRUMENT_KEY;
import static com.chanakyaiq.constants.AppConstants.QUERY_PARAM_INSTRUMENT_TYPE;
import static com.chanakyaiq.constants.AppConstants.QUERY_PARAM_QUERY;
import static com.chanakyaiq.constants.AppConstants.QUERY_PARAM_SEGMENT;
import static com.chanakyaiq.constants.AppConstants.SEGMENT_EQ;
import static com.chanakyaiq.constants.AppConstants.STATUS_SUCCESS;
import static com.chanakyaiq.constants.AppConstants.TIMEZONE_IST;
import static com.chanakyaiq.constants.AppConstants.UNKNOWN_SYMBOL;
import com.chanakyaiq.dto.StockDetailsDTO;
import com.chanakyaiq.dto.StockSearchResponseDTO;
import com.chanakyaiq.service.api.UpstoxService;
import com.chanakyaiq.util.RestUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * Implementation of the {@link UpstoxService} interface using real Upstox APIs
 * via Spring's RestClient backed by Apache HttpComponents.
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class UpstoxServiceImpl implements UpstoxService {

    private final ChanakyaIqProperties properties;
    private final RestClient restClient;
    private final RestUtil restUtil;

    private final Random random = new Random();
    private final ZoneId IST_ZONE = ZoneId.of(TIMEZONE_IST);

    @Override
    public boolean isMarketOpen() {
        ZonedDateTime nowIST = ZonedDateTime.now(IST_ZONE);
        DayOfWeek day = nowIST.getDayOfWeek();
        if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
            return false;
        }
        LocalTime time = nowIST.toLocalTime();
        LocalTime marketOpen = LocalTime.of(MARKET_OPEN_HOUR, MARKET_OPEN_MINUTE);
        LocalTime marketClose = LocalTime.of(MARKET_CLOSE_HOUR, MARKET_CLOSE_MINUTE);
        return !time.isBefore(marketOpen) && !time.isAfter(marketClose);
    }

    @Override
    public BigDecimal getStockPrice(String instrumentKey) {
        StockDetailsDTO details = getStockDetails(instrumentKey);
        return details != null ? details.lastPrice() : BigDecimal.ZERO;
    }

    @Override
    public List<StockSearchResponseDTO> searchStocks(String query) {
        log.info("Searching stocks with query: {}", query);
        List<StockSearchResponseDTO> results = new ArrayList<>();

        // Build URL with filters: exchange=NSE, segment=EQ, instrument_type=EQ
        String url = API_BASE_URL + INSTRUMENT_SEARCH_ENDPOINT
                + "?" + QUERY_PARAM_QUERY + "=" + query
                + "&" + QUERY_PARAM_EXCHANGE + "=" + EXCHANGE_NSE
                + "&" + QUERY_PARAM_SEGMENT + "=" + SEGMENT_EQ
                + "&" + QUERY_PARAM_INSTRUMENT_TYPE + "=" + INSTRUMENT_TYPE_EQ;

        UpstoxSearchResponse response = restUtil.executeGetWithToken(
                restClient, url, properties.getApi().getToken(), UpstoxSearchResponse.class);
        
        log.info("Received response for search query: {}, {} ", query, response.toString());

        if (STATUS_SUCCESS.equals(response.getStatus()) && response.getData() != null) {
            log.info("Search API returned {} instruments", response.getData().size());
            
            for (UpstoxInstrument instrument : response.getData()) {
                results.add(new StockSearchResponseDTO(
                        instrument.getInstrumentKey(),
                        instrument.getTradingSymbol(),
                        instrument.getName()
                ));
            }
            
            log.info("Returning {} stocks", results.size());
        } else {
            log.warn("Search API returned null or unsuccessful response");
        }
        return results;
    }

    @Override
    public StockDetailsDTO getStockDetails(String instrumentKey) {
        log.info("Fetching stock details for instrument: {}", instrumentKey);
        String url = API_BASE_URL + MARKET_QUOTE_ENDPOINT + "?" + QUERY_PARAM_INSTRUMENT_KEY + "=" + instrumentKey;
        UpstoxQuoteResponse response = restUtil.executeGetWithToken(
                restClient, url, properties.getApi().getToken(), UpstoxQuoteResponse.class);

        if (response != null && STATUS_SUCCESS.equals(response.getStatus()) && response.getData() != null) {
            Map<String, UpstoxQuoteData> dataMap = response.getData();
            if(dataMap.size() != 1) {
                log.warn("Unexpected response for instrument: {}:{}. Returning default values.", instrumentKey, response);
                return new StockDetailsDTO(instrumentKey, UNKNOWN_SYMBOL, UNKNOWN_SYMBOL, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                        BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, 0, BigDecimal.ZERO, isMarketOpen());
            }
            UpstoxQuoteData quoteData = dataMap.values().iterator().next();

            if (quoteData != null) {
                log.debug("Quote data found for {}: lastPrice={}, netChange={}", 
                        instrumentKey, quoteData.getLastPrice(), quoteData.getNetChange());
                
                BigDecimal lastPrice = BigDecimal.valueOf(quoteData.getLastPrice() != null ? quoteData.getLastPrice() : 0.0);
                BigDecimal netChange = BigDecimal.valueOf(quoteData.getNetChange() != null ? quoteData.getNetChange() : 0.0);

                BigDecimal changePercent = BigDecimal.ZERO;
                if (lastPrice.subtract(netChange).compareTo(BigDecimal.ZERO) != 0) {
                    BigDecimal prevClose = lastPrice.subtract(netChange);
                    changePercent = netChange.divide(prevClose, PERCENTAGE_SCALE, RoundingMode.HALF_UP)
                            .multiply(new BigDecimal(PERCENTAGE_MULTIPLIER));
                }

                String symbol = quoteData.getSymbol() != null ? quoteData.getSymbol() : instrumentKey;
                
                // Extract OHLC data
                BigDecimal open = BigDecimal.ZERO;
                BigDecimal high = BigDecimal.ZERO;
                BigDecimal low = BigDecimal.ZERO;
                BigDecimal close = BigDecimal.ZERO;
                
                if (quoteData.getOhlc() != null) {
                    open = BigDecimal.valueOf(quoteData.getOhlc().getOpen() != null ? quoteData.getOhlc().getOpen() : 0.0);
                    high = BigDecimal.valueOf(quoteData.getOhlc().getHigh() != null ? quoteData.getOhlc().getHigh() : 0.0);
                    low = BigDecimal.valueOf(quoteData.getOhlc().getLow() != null ? quoteData.getOhlc().getLow() : 0.0);
                    close = BigDecimal.valueOf(quoteData.getOhlc().getClose() != null ? quoteData.getOhlc().getClose() : 0.0);
                }
                
                Integer volume = quoteData.getVolume() != null ? quoteData.getVolume() : 0;
                BigDecimal averagePrice = BigDecimal.valueOf(quoteData.getAveragePrice() != null ? quoteData.getAveragePrice() : 0.0);

                log.info("Successfully fetched details for {}: lastPrice={}, netChange={}, changePercent={}%", 
                        symbol, lastPrice, netChange, changePercent);
                
                return new StockDetailsDTO(
                        instrumentKey,
                        symbol,
                        symbol, // Using symbol as name for now (could be enhanced with full company name)
                        lastPrice.setScale(PRICE_SCALE, RoundingMode.HALF_UP),
                        netChange.setScale(PRICE_SCALE, RoundingMode.HALF_UP),
                        changePercent.setScale(PRICE_SCALE, RoundingMode.HALF_UP),
                        open.setScale(PRICE_SCALE, RoundingMode.HALF_UP),
                        high.setScale(PRICE_SCALE, RoundingMode.HALF_UP),
                        low.setScale(PRICE_SCALE, RoundingMode.HALF_UP),
                        close.setScale(PRICE_SCALE, RoundingMode.HALF_UP),
                        volume,
                        averagePrice.setScale(PRICE_SCALE, RoundingMode.HALF_UP),
                        isMarketOpen()
                );
            }
        }

        log.warn("Could not fetch stock details for instrument: {}. Returning default values.", instrumentKey);
        return new StockDetailsDTO(instrumentKey, UNKNOWN_SYMBOL, UNKNOWN_SYMBOL, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, 
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, 0, BigDecimal.ZERO, isMarketOpen());
    }

    @Override
    public List<BigDecimal> getHistoricalPrices(String symbol) {
        log.info("Generating historical prices for symbol: {}", symbol);
        BigDecimal currentPrice = getStockPrice(symbol);
        List<BigDecimal> list = new ArrayList<>();
        BigDecimal temp = currentPrice != null && currentPrice.compareTo(BigDecimal.ZERO) > 0
                ? currentPrice
                : new BigDecimal(DEFAULT_PRICE);

        log.debug("Starting price for historical data: {}", temp);
        
        for (int i = 0; i < HISTORICAL_DATA_POINTS; i++) {
            list.add(0, temp.setScale(PRICE_SCALE, RoundingMode.HALF_UP));
            double changePercent = (random.nextDouble() - 0.49) * 0.015;
            temp = temp.multiply(BigDecimal.valueOf(1.0 - changePercent));
        }
        
        log.info("Generated {} historical price points for {}", list.size(), symbol);
        return list;
    }
}
