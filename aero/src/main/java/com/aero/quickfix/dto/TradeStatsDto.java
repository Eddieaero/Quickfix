package com.aero.quickfix.dto;

import com.aero.quickfix.model.TradeData;
import java.util.List;

/**
 * DTO for trade statistics and data response.
 */
public class TradeStatsDto {
    
    private int totalTrades;
    private long totalVolume;
    private double averagePrice;
    private List<TradeData> recentTrades;

    public TradeStatsDto() {}

    public TradeStatsDto(int totalTrades, long totalVolume, double averagePrice, List<TradeData> recentTrades) {
        this.totalTrades = totalTrades;
        this.totalVolume = totalVolume;
        this.averagePrice = averagePrice;
        this.recentTrades = recentTrades;
    }

    public int getTotalTrades() {
        return totalTrades;
    }

    public void setTotalTrades(int totalTrades) {
        this.totalTrades = totalTrades;
    }

    public long getTotalVolume() {
        return totalVolume;
    }

    public void setTotalVolume(long totalVolume) {
        this.totalVolume = totalVolume;
    }

    public double getAveragePrice() {
        return averagePrice;
    }

    public void setAveragePrice(double averagePrice) {
        this.averagePrice = averagePrice;
    }

    public List<TradeData> getRecentTrades() {
        return recentTrades;
    }

    public void setRecentTrades(List<TradeData> recentTrades) {
        this.recentTrades = recentTrades;
    }
}
