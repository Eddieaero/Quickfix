package com.aero.quickfix.dto;

import java.math.BigDecimal;

/**
 * Market price data with metadata for a specific asset.
 * Used for real-time and historical price tracking.
 */
public class MarketPriceDto {
    private String symbol;
    private String exchange;
    private BigDecimal price;
    private BigDecimal high;
    private BigDecimal low;
    private BigDecimal volume;
    private BigDecimal dividendYield;
    private BigDecimal peRatio;
    private BigDecimal marketCap;
    private String currency;
    private long lastUpdated;
    private boolean valid;
    private String errorMessage;

    public MarketPriceDto() {}

    public MarketPriceDto(String symbol, BigDecimal price) {
        this.symbol = symbol;
        this.price = price;
        this.valid = true;
        this.lastUpdated = System.currentTimeMillis();
    }

    public MarketPriceDto(String symbol, String errorMessage) {
        this.symbol = symbol;
        this.errorMessage = errorMessage;
        this.valid = false;
        this.lastUpdated = System.currentTimeMillis();
    }

    // Getters and Setters
    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getHigh() {
        return high;
    }

    public void setHigh(BigDecimal high) {
        this.high = high;
    }

    public BigDecimal getLow() {
        return low;
    }

    public void setLow(BigDecimal low) {
        this.low = low;
    }

    public BigDecimal getVolume() {
        return volume;
    }

    public void setVolume(BigDecimal volume) {
        this.volume = volume;
    }

    public BigDecimal getDividendYield() {
        return dividendYield;
    }

    public void setDividendYield(BigDecimal dividendYield) {
        this.dividendYield = dividendYield;
    }

    public BigDecimal getPeRatio() {
        return peRatio;
    }

    public void setPeRatio(BigDecimal peRatio) {
        this.peRatio = peRatio;
    }

    public BigDecimal getMarketCap() {
        return marketCap;
    }

    public void setMarketCap(BigDecimal marketCap) {
        this.marketCap = marketCap;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
