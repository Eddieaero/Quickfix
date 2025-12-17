package com.aero.quickfix.model;

import java.math.BigDecimal;
import java.util.*;

/**
 * Portfolio entity representing a user's investment portfolio.
 */
public class Portfolio {
    private String portfolioId;
    private String userId;
    private String name;
    private BigDecimal initialInvestment;
    private BigDecimal currentValue;
    private BigDecimal totalGains;
    private BigDecimal totalGainsPercentage;
    private long createdAt;
    private long lastUpdatedAt;
    private List<Investment> investments;
    private boolean active;

    public Portfolio() {
        this.investments = new ArrayList<>();
        this.currentValue = BigDecimal.ZERO;
        this.totalGains = BigDecimal.ZERO;
        this.totalGainsPercentage = BigDecimal.ZERO;
        this.active = true;
    }

    public Portfolio(String userId, String name, BigDecimal initialInvestment) {
        this();
        this.portfolioId = "PORTFOLIO_" + System.currentTimeMillis();
        this.userId = userId;
        this.name = name;
        this.initialInvestment = initialInvestment;
        this.currentValue = initialInvestment;
        this.createdAt = System.currentTimeMillis();
        this.lastUpdatedAt = System.currentTimeMillis();
    }

    // Getters and Setters
    public String getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public List<Investment> getInvestments() {
        return investments;
    }

    public void setInvestments(List<Investment> investments) {
        this.investments = investments;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void addInvestment(Investment investment) {
        this.investments.add(investment);
        this.lastUpdatedAt = System.currentTimeMillis();
    }

    public void removeInvestment(String investmentId) {
        this.investments.removeIf(inv -> inv.getInvestmentId().equals(investmentId));
        this.lastUpdatedAt = System.currentTimeMillis();
    }
}
