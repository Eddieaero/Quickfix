package com.aero.quickfix.quant.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * BacktestResultDTO
 * Response DTO for backtest results
 */
public class BacktestResultDTO {

    private UUID id;
    private String strategyName;
    private String symbol;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal initialCapital;
    private BigDecimal finalValue;
    private BigDecimal totalReturn; // Percentage
    private BigDecimal annualReturn; // Percentage
    private BigDecimal sharpeRatio;
    private BigDecimal sortinoRatio;
    private BigDecimal maxDrawdown; // Percentage
    private Long totalTrades;
    private Long winningTrades;
    private Long losingTrades;
    private BigDecimal winRate; // Percentage
    private BigDecimal averageWin;
    private BigDecimal averageLoss;
    private BigDecimal profitFactor;
    private LocalDateTime createdAt;

    // Constructors
    public BacktestResultDTO() {}

    public BacktestResultDTO(UUID id, String strategyName, String symbol, LocalDate startDate,
                           LocalDate endDate, BigDecimal initialCapital, BigDecimal finalValue) {
        this.id = id;
        this.strategyName = strategyName;
        this.symbol = symbol;
        this.startDate = startDate;
        this.endDate = endDate;
        this.initialCapital = initialCapital;
        this.finalValue = finalValue;
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getStrategyName() { return strategyName; }
    public void setStrategyName(String strategyName) { this.strategyName = strategyName; }

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public BigDecimal getInitialCapital() { return initialCapital; }
    public void setInitialCapital(BigDecimal initialCapital) { this.initialCapital = initialCapital; }

    public BigDecimal getFinalValue() { return finalValue; }
    public void setFinalValue(BigDecimal finalValue) { this.finalValue = finalValue; }

    public BigDecimal getTotalReturn() { return totalReturn; }
    public void setTotalReturn(BigDecimal totalReturn) { this.totalReturn = totalReturn; }

    public BigDecimal getAnnualReturn() { return annualReturn; }
    public void setAnnualReturn(BigDecimal annualReturn) { this.annualReturn = annualReturn; }

    public BigDecimal getSharpeRatio() { return sharpeRatio; }
    public void setSharpeRatio(BigDecimal sharpeRatio) { this.sharpeRatio = sharpeRatio; }

    public BigDecimal getSortinoRatio() { return sortinoRatio; }
    public void setSortinoRatio(BigDecimal sortinoRatio) { this.sortinoRatio = sortinoRatio; }

    public BigDecimal getMaxDrawdown() { return maxDrawdown; }
    public void setMaxDrawdown(BigDecimal maxDrawdown) { this.maxDrawdown = maxDrawdown; }

    public Long getTotalTrades() { return totalTrades; }
    public void setTotalTrades(Long totalTrades) { this.totalTrades = totalTrades; }

    public Long getWinningTrades() { return winningTrades; }
    public void setWinningTrades(Long winningTrades) { this.winningTrades = winningTrades; }

    public Long getLosingTrades() { return losingTrades; }
    public void setLosingTrades(Long losingTrades) { this.losingTrades = losingTrades; }

    public BigDecimal getWinRate() { return winRate; }
    public void setWinRate(BigDecimal winRate) { this.winRate = winRate; }

    public BigDecimal getAverageWin() { return averageWin; }
    public void setAverageWin(BigDecimal averageWin) { this.averageWin = averageWin; }

    public BigDecimal getAverageLoss() { return averageLoss; }
    public void setAverageLoss(BigDecimal averageLoss) { this.averageLoss = averageLoss; }

    public BigDecimal getProfitFactor() { return profitFactor; }
    public void setProfitFactor(BigDecimal profitFactor) { this.profitFactor = profitFactor; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
