package com.aero.quickfix.websocket;

import com.aero.quickfix.dto.TradeStatsDto;
import com.aero.quickfix.repository.TradeDataRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * WebSocket handler for real-time trade data streaming.
 */
@Component
public class TradeWebSocketHandler extends TextWebSocketHandler {
    
    private static final Logger log = LoggerFactory.getLogger(TradeWebSocketHandler.class);
    private final List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
    private final TradeDataRepository tradeDataRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    public TradeWebSocketHandler(TradeDataRepository tradeDataRepository) {
        this.tradeDataRepository = tradeDataRepository;
    }
    
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        log.info("WebSocket client connected: {}", session.getId());
        
        // Send initial trade data
        sendTradeUpdate(session);
    }
    
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        log.info("WebSocket client disconnected: {}", session.getId());
    }
    
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.debug("Received WebSocket message: {}", payload);
        
        if ("refresh".equals(payload)) {
            sendTradeUpdate(session);
        }
    }
    
    /**
     * Broadcast trade update to all connected clients.
     */
    public void broadcastTradeUpdate() {
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                try {
                    sendTradeUpdate(session);
                } catch (IOException e) {
                    log.error("Error sending trade update to session {}: {}", session.getId(), e.getMessage());
                }
            }
        }
    }
    
    private void sendTradeUpdate(WebSocketSession session) throws IOException {
        int totalTrades = tradeDataRepository.getTotalTradeCount();
        long totalVolume = tradeDataRepository.getTotalVolume();
        double averagePrice = tradeDataRepository.getAveragePrice();
        var recentTrades = tradeDataRepository.getRecentTrades(10);
        
        TradeStatsDto stats = new TradeStatsDto(totalTrades, totalVolume, averagePrice, recentTrades);
        
        String json = objectMapper.writeValueAsString(stats);
        session.sendMessage(new TextMessage(json));
    }
}
