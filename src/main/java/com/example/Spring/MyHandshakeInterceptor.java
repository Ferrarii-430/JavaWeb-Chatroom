package com.example.Spring;


import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import javax.annotation.Resource;
import java.util.Map;

public class MyHandshakeInterceptor extends HttpSessionHandshakeInterceptor {


    @Override //握手前
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        System.out.println("握手前");
        return super.beforeHandshake(request, response, wsHandler, attributes);
    }

    @Override //握手后
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception ex) {
        System.out.println("握手后");
        super.afterHandshake(request, response, wsHandler, ex);
    }
}

