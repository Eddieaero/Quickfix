package com.aero.quickfix.dto;

import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * EODHD End-of-Day price data response.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class EodhPriceResponse {
    private String date;
    
    @JsonProperty("open")
    private Double open;
    
    @JsonProperty("high")
    private Double high;
    
    @JsonProperty("low")
    private Double low;
    
    @JsonProperty("close")
    private Double close;
    
    @JsonProperty("adjusted_close")
    private Double adjustedClose;
    
    @JsonProperty("volume")
    private Long volume;

    public EodhPriceResponse() {}

    public EodhPriceResponse(String date, Double close) {
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

    public Double getOpen() {
        return open;
    }

    public void setOpen(Double open) {
        this.open = open;
    }

    public Double getHigh() {
        return high;
    }

    public void setHigh(Double high) {
        this.high = high;
    }

    public Double getLow() {
        return low;
    }

    public void setLow(Double low) {
        this.low = low;
    }

    public Double getClose() {
        return close;
    }

    public void setClose(Double close) {
        this.close = close;
    }

    public Double getAdjustedClose() {
        return adjustedClose;
    }

    public void setAdjustedClose(Double adjustedClose) {
        this.adjustedClose = adjustedClose;
    }

    public Long getVolume() {
        return volume;
    }

    public void setVolume(Long volume) {
        this.volume = volume;
    }
}
