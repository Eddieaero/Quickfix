package com.aero.quickfix.dto;

import java.math.BigDecimal;

/**
 * DTO for creating a new investment.
 */
public class CreateInvestmentRequest {
    private String assetSymbol;
    private String assetType;
    private BigDecimal principal;
    private BigDecimal annualInterestRate;
    private Integer compoundingFrequency;
    private Integer yearsOfInvestment;

    public CreateInvestmentRequest() {
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

    public BigDecimal getAnnualInterestRate() {
        return annualInterestRate;
    }

    public void setAnnualInterestRate(BigDecimal annualInterestRate) {
        this.annualInterestRate = annualInterestRate;
    }

    public Integer getCompoundingFrequency() {
        return compoundingFrequency;
    }

    public void setCompoundingFrequency(Integer compoundingFrequency) {
        this.compoundingFrequency = compoundingFrequency;
    }

    public Integer getYearsOfInvestment() {
        return yearsOfInvestment;
    }

    public void setYearsOfInvestment(Integer yearsOfInvestment) {
        this.yearsOfInvestment = yearsOfInvestment;
    }
}
