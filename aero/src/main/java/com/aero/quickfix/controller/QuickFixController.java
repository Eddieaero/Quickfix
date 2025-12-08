package com.aero.quickfix.controller;

import com.aero.quickfix.dto.TradeStatsDto;
import com.aero.quickfix.model.TradeData;
import com.aero.quickfix.repository.TradeDataRepository;
import com.aero.quickfix.service.QuickFixService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Collection;
import java.util.List;

/**
 * REST Controller for QuickFIX operations.
 * Provides endpoints to manage FIX protocol connections and message handling.
 */
@RestController
@RequestMapping("/api/quickfix")
public class QuickFixController {

    private static final Logger log = LoggerFactory.getLogger(QuickFixController.class);
    private final QuickFixService quickFixService;
    private final TradeDataRepository tradeDataRepository;

    public QuickFixController(QuickFixService quickFixService, TradeDataRepository tradeDataRepository) {
        this.quickFixService = quickFixService;
        this.tradeDataRepository = tradeDataRepository;
    }

    /**
     * Health check endpoint for QuickFIX service.
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("QuickFIX service is running");
    }

    /**
     * Start FIX protocol connection.
     */
    @PostMapping("/start")
    public ResponseEntity<String> startConnection() {
        try {
            quickFixService.startConnection();
            return ResponseEntity.ok("QuickFIX connection started successfully");
        } catch (Exception e) {
            log.error("Failed to start QuickFIX connection", e);
            return ResponseEntity.internalServerError().body("Failed to start connection: " + e.getMessage());
        }
    }

    /**
     * Stop FIX protocol connection.
     */
    @PostMapping("/stop")
    public ResponseEntity<String> stopConnection() {
        try {
            quickFixService.stopConnection();
            return ResponseEntity.ok("QuickFIX connection stopped successfully");
        } catch (Exception e) {
            log.error("Failed to stop QuickFIX connection", e);
            return ResponseEntity.internalServerError().body("Failed to stop connection: " + e.getMessage());
        }
    }

    /**
     * Get connection status.
     */
    @GetMapping("/status")
    public ResponseEntity<String> getStatus() {
        boolean isConnected = quickFixService.isConnected();
        String status = isConnected ? "CONNECTED" : "DISCONNECTED";
        return ResponseEntity.ok("QuickFIX Status: " + status);
    }

    /**
     * Get trade data statistics and recent trades.
     */
    @GetMapping("/trades")
    public ResponseEntity<TradeStatsDto> getTrades() {
        int totalTrades = tradeDataRepository.getTotalTradeCount();
        long totalVolume = tradeDataRepository.getTotalVolume();
        double averagePrice = tradeDataRepository.getAveragePrice();
        List<TradeData> recentTrades = tradeDataRepository.getRecentTrades(20);
        
        TradeStatsDto stats = new TradeStatsDto(totalTrades, totalVolume, averagePrice, recentTrades);
        return ResponseEntity.ok(stats);
    }

    /**
     * Get all trades.
     */
    @GetMapping("/trades/all")
    public ResponseEntity<Collection<TradeData>> getAllTrades() {
        return ResponseEntity.ok(tradeDataRepository.findAll());
    }

}
