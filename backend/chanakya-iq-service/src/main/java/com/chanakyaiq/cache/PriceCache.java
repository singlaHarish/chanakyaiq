package com.chanakyaiq.cache;

import com.chanakyaiq.dto.StockDetailsDTO;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class PriceCache {
    
    @Data
    static class CachedPrice {
        private final StockDetailsDTO data;
        private final Instant lastUpdated;
    }
    
    private final Map<String, CachedPrice> prices = new ConcurrentHashMap<>();
    private static final long CACHE_TTL_SECONDS = 5;
    
    public void update(String instrumentKey, StockDetailsDTO data) {
        prices.put(instrumentKey, new CachedPrice(data, Instant.now()));
    }
    
    public StockDetailsDTO get(String instrumentKey) {
        CachedPrice cached = prices.get(instrumentKey);
        if (cached != null && !isExpired(cached)) {
            return cached.getData();
        }
        return null;
    }
    
    public Map<String, StockDetailsDTO> getAll() {
        Instant now = Instant.now();
        return prices.entrySet().stream()
                .filter(e -> !isExpired(e.getValue()))
                .collect(java.util.stream.Collectors.toMap(
                    Map.Entry::getKey,
                    e -> e.getValue().getData()
                ));
    }
    
    public void remove(String instrumentKey) {
        prices.remove(instrumentKey);
    }
    
    public void clear() {
        prices.clear();
    }
    
    public int size() {
        return prices.size();
    }
    
    private boolean isExpired(CachedPrice cached) {
        return Instant.now().isAfter(cached.getLastUpdated().plusSeconds(CACHE_TTL_SECONDS));
    }
}
