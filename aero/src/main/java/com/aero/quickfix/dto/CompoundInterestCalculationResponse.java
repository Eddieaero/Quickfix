package com.aero.quickfix.dto;

import java.math.BigDecimal;

/**
 * DTO for compound interest calculation response.
 */
public class CompoundInterestCalculationResponse {
    private BigDecimal principal;
    private BigDecimal rate;
    private Integer years;
    private Integer compoundingFrequency;
    private BigDecimal finalAmount;
    private BigDecimal totalInterest;
    private String compoundingPeriod;

    public CompoundInterestCalculationResponse() {
    }

    public CompoundInterestCalculationResponse(BigDecimal principal, BigDecimal rate, Integer years,
                                              Integer compoundingFrequency, BigDecimal finalAmount,
                                              BigDecimal totalInterest, String compoundingPeriod) {
        this.principal = principal;
        this.rate = rate;
        this.years = years;
        this.compoundingFrequency = compoundingFrequency;
        this.finalAmount = finalAmount;
        this.totalInterest = totalInterest;
        this.compoundingPeriod = compoundingPeriod;
    }

    public BigDecimal getPrincipal() {
        return principal;
    }

    public void setPrincipal(BigDecimal principal) {
        this.principal = principal;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public Integer getYears() {
        return years;
    }

    public void setYears(Integer years) {
        this.years = years;
    }

    public Integer getCompoundingFrequency() {
        return compoundingFrequency;
    }

    public void setCompoundingFrequency(Integer compoundingFrequency) {
        this.compoundingFrequency = compoundingFrequency;
    }

    public BigDecimal getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(BigDecimal finalAmount) {
        this.finalAmount = finalAmount;
    }

    public BigDecimal getTotalInterest() {
        return totalInterest;
    }

    public void setTotalInterest(BigDecimal totalInterest) {
        this.totalInterest = totalInterest;
    }

    public String getCompoundingPeriod() {
        return compoundingPeriod;
    }

    public void setCompoundingPeriod(String compoundingPeriod) {
        this.compoundingPeriod = compoundingPeriod;
    }
}
