package com.aero.quickfix.quant.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Trade Log Entity
 * Tracks individual trades executed during backtesting or live trading
 */
@Entity
@Table(name = "trade_log", indexes = {
    @Index(name = "idx_trade_log_backtest", columnList = "backtest_id"),
    @Index(name = "idx_trade_log_symbol", columnList = "symbol")
})
public class TradeLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "backtest_id")
    private UUID backtestId;

    @Column(nullable = false)
    private String symbol;

    @Column(nullable = false)
    private LocalDate tradeDate;

    @Column(nullable = false, precision = 15, scale = 8)
    private BigDecimal entryPrice;

    @Column(precision = 15, scale = 8)
    private BigDecimal exitPrice;

    @Column(nullable = false, precision = 15, scale = 8)
    private BigDecimal quantity;

    @Column(nullable = false)
    private String tradeType; // LONG or SHORT

    @Column(precision = 15, scale = 2)
    private BigDecimal profitLoss;

    @Column(name = "profit_loss_pct", precision = 10, scale = 4)
    private BigDecimal profitLossPct;

    @Column(nullable = false)
    private String tradeStatus; // OPEN or CLOSED

    @Column(name = "entry_signal")
    private String entrySignal;

    @Column(name = "exit_signal")
    private String exitSignal;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // Constructors
    public TradeLog() {}

    public TradeLog(String symbol, LocalDate tradeDate, BigDecimal entryPrice, 
                    BigDecimal quantity, String tradeType) {
        this.symbol = symbol;
        this.tradeDate = tradeDate;
        this.entryPrice = entryPrice;
        this.quantity = quantity;
        this.tradeType = tradeType;
        this.tradeStatus = "OPEN";
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getBacktestId() { return backtestId; }
    public void setBacktestId(UUID backtestId) { this.backtestId = backtestId; }

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public LocalDate getTradeDate() { return tradeDate; }
    public void setTradeDate(LocalDate tradeDate) { this.tradeDate = tradeDate; }

    public BigDecimal getEntryPrice() { return entryPrice; }
    public void setEntryPrice(BigDecimal entryPrice) { this.entryPrice = entryPrice; }

    public BigDecimal getExitPrice() { return exitPrice; }
    public void setExitPrice(BigDecimal exitPrice) { this.exitPrice = exitPrice; }

    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }

    public String getTradeType() { return tradeType; }
    public void setTradeType(String tradeType) { this.tradeType = tradeType; }

    public BigDecimal getProfitLoss() { return profitLoss; }
    public void setProfitLoss(BigDecimal profitLoss) { this.profitLoss = profitLoss; }

    public BigDecimal getProfitLossPct() { return profitLossPct; }
    public void setProfitLossPct(BigDecimal profitLossPct) { this.profitLossPct = profitLossPct; }

    public String getTradeStatus() { return tradeStatus; }
    public void setTradeStatus(String tradeStatus) { this.tradeStatus = tradeStatus; }

    public String getEntrySignal() { return entrySignal; }
    public void setEntrySignal(String entrySignal) { this.entrySignal = entrySignal; }

    public String getExitSignal() { return exitSignal; }
    public void setExitSignal(String exitSignal) { this.exitSignal = exitSignal; }

    public LocalDateTime getCreatedAt() { return createdAt; }
}
