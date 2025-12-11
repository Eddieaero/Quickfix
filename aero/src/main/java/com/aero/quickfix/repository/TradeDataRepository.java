package com.aero.quickfix.repository;

import com.aero.quickfix.model.TradeData;
import org.springframework.stereotype.Repository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory repository for storing FIX trade data.
 */
@Repository
public class TradeDataRepository {
    
    private final Map<String, TradeData> trades = new ConcurrentHashMap<>();
    private final List<TradeData> tradeHistory = Collections.synchronizedList(new ArrayList<>());
    private static final int MAX_HISTORY_SIZE = 1000;

    public void save(TradeData tradeData) {
        trades.put(tradeData.getOrderId(), tradeData);
        tradeHistory.add(tradeData);
        
        // Keep history bounded
        if (tradeHistory.size() > MAX_HISTORY_SIZE) {
            tradeHistory.removeFirst();
        }
    }

    public TradeData findById(String orderId) {
        return trades.get(orderId);
    }

    public Collection<TradeData> findAll() {
        return new ArrayList<>(trades.values());
    }

    public List<TradeData> getHistory() {
        return new ArrayList<>(tradeHistory);
    }

    public List<TradeData> getRecentTrades(int limit) {
        return tradeHistory.stream()
                .skip(Math.max(0, tradeHistory.size() - limit))
                .toList();
    }

    public void clear() {
        trades.clear();
        tradeHistory.clear();
    }

    public int getTotalTradeCount() {
        return tradeHistory.size();
    }

    public long getTotalVolume() {
        return Math.round(tradeHistory.stream()
                .mapToDouble(t -> t.getQuantity() != null ? t.getQuantity() : 0)
                .sum());
    }

    public double getAveragePrice() {
        if (tradeHistory.isEmpty()) return 0;
        return tradeHistory.stream()
                .mapToDouble(t -> t.getPrice() != null ? t.getPrice() : 0)
                .average()
                .orElse(0);
    }
}
