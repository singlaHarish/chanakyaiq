package com.chanakyaiq.constants;

public class AppConstants {
    private AppConstants() {
        // Restrict instantiation
    }

    public static final String HEADER_ACCEPT = "Accept";
    public static final String MEDIA_TYPE_JSON = "application/json";
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    public static final String API_BASE_URL = "https://api.upstox.com/v2";
    public static final String INSTRUMENT_SEARCH_ENDPOINT = "/instruments/search";
    public static final String MARKET_QUOTE_ENDPOINT = "/market-quote/quotes";
}
