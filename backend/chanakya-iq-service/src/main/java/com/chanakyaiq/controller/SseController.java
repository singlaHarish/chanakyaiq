package com.chanakyaiq.controller;

import com.chanakyaiq.service.SseEmitterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

@Log4j2
@RestController
@RequestMapping("/api/sse")
@RequiredArgsConstructor
public class SseController {
    
    private final SseEmitterRepository emitterRepository;
    
    @GetMapping(value = "/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter connect() {
        SseEmitter emitter = new SseEmitter(30_000L);
        
        emitterRepository.addEmitter(emitter);
        
        try {
            emitter.send(SseEmitter.event()
                .name("connected")
                .data(Map.of("message", "Connected to real-time updates")));
            
            log.info("New SSE connection established");
        } catch (Exception e) {
            log.error("Failed to send connection event", e);
            emitter.complete();
        }
        
        return emitter;
    }
    
    @PostMapping("/disconnect")
    public Map<String, String> disconnect(@RequestBody Map<String, String> request) {
        String id = request.get("emitterId");
        if (id != null) {
            emitterRepository.removeEmitter(id);
        }
        return Map.of("status", "disconnected");
    }
    
    @GetMapping("/status")
    public Map<String, Object> getStatus() {
        return Map.of(
            "connected", emitterRepository.getSize() > 0,
            "activeConnections", emitterRepository.getSize()
        );
    }
}
