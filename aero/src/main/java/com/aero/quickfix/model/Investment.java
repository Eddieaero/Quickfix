package com.aero.quickfix.model;

import java.math.BigDecimal;

/**
 * Investment entity representing an individual investment within a portfolio.
 */
public class Investment {
    private String investmentId;
    private String portfolioId;
    private String assetSymbol;
    private String assetType;
    private BigDecimal principal;
    private BigDecimal currentValue;
    private BigDecimal annualInterestRate;
    private int compoundingFrequency;
    private int yearsOfInvestment;
    private long investmentDate;
    private long maturityDate;
    private BigDecimal totalInterest;
    private String status;

    // Market data fields (from EODHD)
    private BigDecimal currentMarketPrice;
    private BigDecimal dividendYield;
    private BigDecimal peRatio;
    private BigDecimal realizedGain;
    private long lastPriceUpdate;
    private boolean marketDataAvailable;

    public Investment() {
        this.compoundingFrequency = 12; // Monthly by default
        this.yearsOfInvestment = 1;
        this.status = "ACTIVE";
    }

    public Investment(String portfolioId, String assetSymbol, String assetType,
                     BigDecimal principal, BigDecimal annualInterestRate) {
        this();
        this.investmentId = "INV_" + System.currentTimeMillis();
        this.portfolioId = portfolioId;
        this.assetSymbol = assetSymbol;
        this.assetType = assetType;
        this.principal = principal;
        this.currentValue = principal;
        this.annualInterestRate = annualInterestRate;
        this.investmentDate = System.currentTimeMillis();
        this.totalInterest = BigDecimal.ZERO;
    }

    // Getters and Setters
    public String getInvestmentId() {
        return investmentId;
    }

    public void setInvestmentId(String investmentId) {
        this.investmentId = investmentId;
    }

    public String getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
    }

    public String getAssetSymbol() {
        return assetSymbol;
    }

    public void setAssetSymbol(String assetSymbol) {
        this.assetSymbol = assetSymbol;
    }

    public String getAssetType() {
        return assetType;
    }

    public void setAssetType(String assetType) {
        this.assetType = assetType;
    }

    public BigDecimal getPrincipal() {
        return principal;
    }

    public void setPrincipal(BigDecimal principal) {
        this.principal = principal;
    }

    public BigDecimal getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(BigDecimal currentValue) {
        this.currentValue = currentValue;
    }

    public BigDecimal getAnnualInterestRate() {
        return annualInterestRate;
    }

    public void setAnnualInterestRate(BigDecimal annualInterestRate) {
        this.annualInterestRate = annualInterestRate;
    }

    public int getCompoundingFrequency() {
        return compoundingFrequency;
    }

    public void setCompoundingFrequency(int compoundingFrequency) {
        this.compoundingFrequency = compoundingFrequency;
    }

    public int getYearsOfInvestment() {
        return yearsOfInvestment;
    }

    public void setYearsOfInvestment(int yearsOfInvestment) {
        this.yearsOfInvestment = yearsOfInvestment;
    }

    public long getInvestmentDate() {
        return investmentDate;
    }

    public void setInvestmentDate(long investmentDate) {
        this.investmentDate = investmentDate;
    }

    public long getMaturityDate() {
        return maturityDate;
    }

    public void setMaturityDate(long maturityDate) {
        this.maturityDate = maturityDate;
    }

    public BigDecimal getTotalInterest() {
        return totalInterest;
    }

    public void setTotalInterest(BigDecimal totalInterest) {
        this.totalInterest = totalInterest;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getCurrentMarketPrice() {
        return currentMarketPrice;
    }

    public void setCurrentMarketPrice(BigDecimal currentMarketPrice) {
        this.currentMarketPrice = currentMarketPrice;
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

    public BigDecimal getRealizedGain() {
        return realizedGain;
    }

    public void setRealizedGain(BigDecimal realizedGain) {
        this.realizedGain = realizedGain;
    }

    public long getLastPriceUpdate() {
        return lastPriceUpdate;
    }

    public void setLastPriceUpdate(long lastPriceUpdate) {
        this.lastPriceUpdate = lastPriceUpdate;
    }

    public boolean isMarketDataAvailable() {
        return marketDataAvailable;
    }

    public void setMarketDataAvailable(boolean marketDataAvailable) {
        this.marketDataAvailable = marketDataAvailable;
    }
}
