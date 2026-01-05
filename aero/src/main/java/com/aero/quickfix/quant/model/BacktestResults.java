package com.aero.quickfix.quant.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Backtest Results Entity
 * Stores comprehensive results from strategy backtests
 */
@Entity
@Table(name = "backtest_results", indexes = {
    @Index(name = "idx_backtest_strategy", columnList = "strategy_name"),
    @Index(name = "idx_backtest_symbol", columnList = "symbol")
})
public class BacktestResults {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String strategyName;

    @Column(nullable = false)
    private String symbol;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal initialCapital;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal finalValue;

    @Column(nullable = false, precision = 10, scale = 4)
    private BigDecimal totalReturn;

    @Column(precision = 10, scale = 4)
    private BigDecimal annualReturn;

    @Column(precision = 10, scale = 4)
    private BigDecimal sharpeRatio;

    @Column(precision = 10, scale = 4)
    private BigDecimal sortinoRatio;

    @Column(precision = 10, scale = 4)
    private BigDecimal maxDrawdown;

    @Column(precision = 10, scale = 4)
    private BigDecimal winRate;

    @Column(precision = 10, scale = 4)
    private BigDecimal profitFactor;

    @Column(nullable = false)
    private Integer totalTrades;

    @Column(nullable = false)
    private Integer winningTrades;

    @Column(nullable = false)
    private Integer losingTrades;

    @Column(precision = 15, scale = 2)
    private BigDecimal avgWin;

    @Column(precision = 15, scale = 2)
    private BigDecimal avgLoss;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // Constructors
    public BacktestResults() {}

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

    public BigDecimal getWinRate() { return winRate; }
    public void setWinRate(BigDecimal winRate) { this.winRate = winRate; }

    public BigDecimal getProfitFactor() { return profitFactor; }
    public void setProfitFactor(BigDecimal profitFactor) { this.profitFactor = profitFactor; }

    public Integer getTotalTrades() { return totalTrades; }
    public void setTotalTrades(Integer totalTrades) { this.totalTrades = totalTrades; }

    public Integer getWinningTrades() { return winningTrades; }
    public void setWinningTrades(Integer winningTrades) { this.winningTrades = winningTrades; }

    public Integer getLosingTrades() { return losingTrades; }
    public void setLosingTrades(Integer losingTrades) { this.losingTrades = losingTrades; }

    public BigDecimal getAvgWin() { return avgWin; }
    public void setAvgWin(BigDecimal avgWin) { this.avgWin = avgWin; }

    public BigDecimal getAvgLoss() { return avgLoss; }
    public void setAvgLoss(BigDecimal avgLoss) { this.avgLoss = avgLoss; }

    public LocalDateTime getCreatedAt() { return createdAt; }
}
