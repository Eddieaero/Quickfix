package com.aero.quickfix.dto;

import java.math.BigDecimal;

/**
 * EODHD End-of-Day price data response.
 */
public class EodhPriceResponse {
    private String date;
    private BigDecimal open;
    private BigDecimal high;
    private BigDecimal low;
    private BigDecimal close;
    private BigDecimal adjustedClose;
    private Long volume;

    public EodhPriceResponse() {}

    public EodhPriceResponse(String date, BigDecimal close) {
        this.date = date;
        this.close = close;
        this.adjustedClose = close;
    }

    // Getters and Setters
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public BigDecimal getOpen() {
        return open;
    }

    public void setOpen(BigDecimal open) {
        this.open = open;
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

    public BigDecimal getClose() {
        return close;
    }

    public void setClose(BigDecimal close) {
        this.close = close;
    }

    public BigDecimal getAdjustedClose() {
        return adjustedClose;
    }

    public void setAdjustedClose(BigDecimal adjustedClose) {
        this.adjustedClose = adjustedClose;
    }

    public Long getVolume() {
        return volume;
    }

    public void setVolume(Long volume) {
        this.volume = volume;
    }
}
