package com.chanakyaiq.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "chanakyaiq")
public class ChanakyaIqProperties {
    private Upstox api = new Upstox();

    @Data
    public static class Upstox {
        private String token;
    }
}
