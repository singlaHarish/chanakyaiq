package com.chanakyaiq.service;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class SseEmitterRepository {
    
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    private static final long DEFAULT_TIMEOUT = 30_000L;
    
    public String addEmitter(SseEmitter emitter) {
        String id = java.util.UUID.randomUUID().toString();
        emitter.onCompletion(() -> emitters.remove(id));
        emitter.onTimeout(() -> {
            log.warn("SSE emitter timeout: {}", id);
            emitters.remove(id);
        });
        emitter.onError(throwable -> {
            log.error("SSE emitter error: {}", id, throwable);
            emitters.remove(id);
        });
        emitters.put(id, emitter);
        log.info("SSE emitter added. Total active: {}", emitters.size());
        return id;
    }
    
    public void removeEmitter(String id) {
        emitters.remove(id);
    }
    
    public void broadcast(String event, Object data) {
        emitters.values().forEach(emitter -> {
            try {
                emitter.send(SseEmitter.event()
                    .name(event)
                    .data(data));
            } catch (IOException | IllegalStateException e) {
                log.error("Failed to send SSE event", e);
                emitter.complete();
            }
        });
    }
    
    public int getSize() {
        return emitters.size();
    }
}
