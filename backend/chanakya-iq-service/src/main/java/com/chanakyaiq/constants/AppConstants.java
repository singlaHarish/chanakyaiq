package com.chanakyaiq.constants;

public class AppConstants {
    private AppConstants() {
        // Restrict instantiation
    }

    // HTTP Headers
    public static final String HEADER_ACCEPT = "Accept";
    public static final String MEDIA_TYPE_JSON = "application/json";
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    // API URLs
    public static final String API_BASE_URL = "https://api.upstox.com/v2";
    public static final String API_BASE_URL_V3 = "https://api.upstox.com/v3";
    public static final String INSTRUMENT_SEARCH_ENDPOINT = "/instruments/search";
    public static final String MARKET_QUOTE_ENDPOINT = "/market-quote/quotes";
    public static final String HISTORICAL_CANDLE_ENDPOINT = "/historical-candle";
    public static final String CANDLE_UNIT_DAYS = "days";
    public static final String CANDLE_INTERVAL_1 = "1";

    // Query Parameters
    public static final String QUERY_PARAM_QUERY = "query";
    public static final String QUERY_PARAM_EXCHANGE = "exchanges";
    public static final String QUERY_PARAM_SEGMENT = "segments";
    public static final String QUERY_PARAM_INSTRUMENT_TYPE = "instrument_types";
    public static final String QUERY_PARAM_INSTRUMENT_KEY = "instrument_key";

    // Exchange and Segment Values
    public static final String EXCHANGE_NSE = "NSE";
    public static final String SEGMENT_EQ = "EQ";
    public static final String INSTRUMENT_TYPE_EQ = "EQ";

    // API Response Status
    public static final String STATUS_SUCCESS = "success";

    // Market Hours (IST)
    public static final String TIMEZONE_IST = "Asia/Kolkata";
    public static final int MARKET_OPEN_HOUR = 9;
    public static final int MARKET_OPEN_MINUTE = 15;
    public static final int MARKET_CLOSE_HOUR = 15;
    public static final int MARKET_CLOSE_MINUTE = 30;

    // Historical Data
    public static final int HISTORICAL_DATA_POINTS = 20;
    public static final String DEFAULT_PRICE = "100.00";

    // Decimal Scales
    public static final int PRICE_SCALE = 2;
    public static final int PERCENTAGE_SCALE = 4;
    public static final String PERCENTAGE_MULTIPLIER = "100";

    // Default Values
    public static final String UNKNOWN_SYMBOL = "Unknown";
}
