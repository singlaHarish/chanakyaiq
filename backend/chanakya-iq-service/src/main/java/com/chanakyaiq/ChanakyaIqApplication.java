package com.chanakyaiq;

import com.chanakyaiq.websocket.UpstoxWebSocketManager;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@Log4j2
public class ChanakyaIqApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChanakyaIqApplication.class, args);
	}
	
	@Bean
	public CommandLineRunner initWebSocket(UpstoxWebSocketManager webSocketManager) {
		return args -> {
			// Start WebSocket connection when app starts
			webSocketManager.start();
			log.info("WebSocket connection initialized");
		};
	}

}