package com.aero.quickfix.quant.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * BacktestRequestDTO
 * Request body for starting a new backtest
 */
public class BacktestRequestDTO {

    private String strategyName; // e.g., "SMA Crossover"
    private String symbol; // e.g., "AAPL.US"
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    
    private BigDecimal initialCapital; // Starting cash

    // Constructors
    public BacktestRequestDTO() {}

    public BacktestRequestDTO(String strategyName, String symbol, LocalDate startDate, 
                             LocalDate endDate, BigDecimal initialCapital) {
        this.strategyName = strategyName;
        this.symbol = symbol;
        this.startDate = startDate;
        this.endDate = endDate;
        this.initialCapital = initialCapital;
    }

    // Getters and Setters
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
}
