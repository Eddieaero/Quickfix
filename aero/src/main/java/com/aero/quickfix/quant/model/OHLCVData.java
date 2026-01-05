package com.aero.quickfix.quant.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * OHLCV Data Entity for TimescaleDB
 * Stores Open, High, Low, Close, Volume data for backtesting
 */
@Entity
@Table(name = "ohlcv_data", indexes = {
    @Index(name = "idx_ohlcv_symbol_time", columnList = "symbol, time DESC")
})
public class OHLCVData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime time;

    @Column(nullable = false)
    private String symbol;

    @Column(nullable = false, precision = 15, scale = 8)
    private BigDecimal open;

    @Column(nullable = false, precision = 15, scale = 8)
    private BigDecimal high;

    @Column(nullable = false, precision = 15, scale = 8)
    private BigDecimal low;

    @Column(nullable = false, precision = 15, scale = 8)
    private BigDecimal close;

    @Column(nullable = false)
    private Long volume;

    @Column(precision = 15, scale = 8)
    private BigDecimal adjustedClose;

    @Column(precision = 15, scale = 8)
    private BigDecimal dividend;

    @Column(precision = 15, scale = 8)
    private BigDecimal splitCoefficient;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    // Constructors
    public OHLCVData() {}

    public OHLCVData(LocalDateTime time, String symbol, BigDecimal open, BigDecimal high, 
                     BigDecimal low, BigDecimal close, Long volume) {
        this.time = time;
        this.symbol = symbol;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDateTime getTime() { return time; }
    public void setTime(LocalDateTime time) { this.time = time; }

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public BigDecimal getOpen() { return open; }
    public void setOpen(BigDecimal open) { this.open = open; }

    public BigDecimal getHigh() { return high; }
    public void setHigh(BigDecimal high) { this.high = high; }

    public BigDecimal getLow() { return low; }
    public void setLow(BigDecimal low) { this.low = low; }

    public BigDecimal getClose() { return close; }
    public void setClose(BigDecimal close) { this.close = close; }

    public Long getVolume() { return volume; }
    public void setVolume(Long volume) { this.volume = volume; }

    public BigDecimal getAdjustedClose() { return adjustedClose; }
    public void setAdjustedClose(BigDecimal adjustedClose) { this.adjustedClose = adjustedClose; }

    public BigDecimal getDividend() { return dividend; }
    public void setDividend(BigDecimal dividend) { this.dividend = dividend; }

    public BigDecimal getSplitCoefficient() { return splitCoefficient; }
    public void setSplitCoefficient(BigDecimal splitCoefficient) { this.splitCoefficient = splitCoefficient; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
