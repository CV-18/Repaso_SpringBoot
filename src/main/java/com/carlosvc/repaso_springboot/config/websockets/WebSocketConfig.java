package com.carlosvc.repaso_springboot.config.websockets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Value("${api.version}")
    private String apiVersion;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry){
        registry.addHandler(webSocketAlbumesHandler(), "/ws/" + apiVersion + "/Albumes");
    }

    @Bean
    public WebSocketHandler webSocketAlbumesHandler(){
        return new com.carlosvc.repaso_springboot.config.websockets.WebSocketHandler("Albumes");
    }
}
