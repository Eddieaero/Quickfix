package com.aero.quickfix.dto;

import java.math.BigDecimal;

/**
 * DTO for portfolio overview response.
 */
public class PortfolioDto {
    private String portfolioId;
    private String name;
    private BigDecimal initialInvestment;
    private BigDecimal currentValue;
    private BigDecimal totalGains;
    private BigDecimal totalGainsPercentage;
    private long createdAt;
    private long lastUpdatedAt;
    private int investmentCount;
    private boolean active;

    public PortfolioDto() {
    }

    public String getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getInitialInvestment() {
        return initialInvestment;
    }

    public void setInitialInvestment(BigDecimal initialInvestment) {
        this.initialInvestment = initialInvestment;
    }

    public BigDecimal getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(BigDecimal currentValue) {
        this.currentValue = currentValue;
    }

    public BigDecimal getTotalGains() {
        return totalGains;
    }

    public void setTotalGains(BigDecimal totalGains) {
        this.totalGains = totalGains;
    }

    public BigDecimal getTotalGainsPercentage() {
        return totalGainsPercentage;
    }

    public void setTotalGainsPercentage(BigDecimal totalGainsPercentage) {
        this.totalGainsPercentage = totalGainsPercentage;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getLastUpdatedAt() {
        return lastUpdatedAt;
    }

    public void setLastUpdatedAt(long lastUpdatedAt) {
        this.lastUpdatedAt = lastUpdatedAt;
    }

    public int getInvestmentCount() {
        return investmentCount;
    }

    public void setInvestmentCount(int investmentCount) {
        this.investmentCount = investmentCount;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
