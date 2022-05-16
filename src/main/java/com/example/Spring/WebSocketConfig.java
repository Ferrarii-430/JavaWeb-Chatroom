package com.example.Spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // 添加处理器
        registry.addHandler(myHandler(), "/myHandler").addInterceptors(new MyHandshakeInterceptor()).setAllowedOrigins("*");
    }

    @Bean
    public WebSocketHandler myHandler() {
        return new myHandler();
    }

    @Bean
    public WebSocketHandler echoWebSocketHandler() {
        return new MyWebSocketHandler();
    }
}
