package com.chanakyaiq.util;

import com.chanakyaiq.constants.AppConstants;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Log4j2
@Component
public class RestUtil {

    /**
     * Executes a GET request utilizing the provided RestClient, with an Authorization Bearer token.
     * Maps the JSON response strictly to the given target Class.
     *
     * @param restClient The Spring RestClient instance to use
     * @param url The full URL string to call
     * @param token The Bearer token for Authorization
     * @param responseType The Class type to map the JSON response to
     * @return The typed Response object, or null if an error occurs
     */
    public <T> T executeGetWithToken(RestClient restClient, String url, String token, Class<T> responseType) {
        try {
            return restClient.get()
                    .uri(url)
                    .header(AppConstants.HEADER_ACCEPT, AppConstants.MEDIA_TYPE_JSON)
                    .header(AppConstants.HEADER_AUTHORIZATION, AppConstants.BEARER_PREFIX + token)
                    .retrieve()
                    .body(responseType);
        } catch (Exception e) {
            log.error("Error executing GET request to URL: {}", url, e);
            return null;
        }
    }
}
