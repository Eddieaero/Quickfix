package com.aero.quickfix.dto;

import java.math.BigDecimal;

/**
 * DTO for creating a new portfolio.
 */
public class CreatePortfolioRequest {
    private String name;
    private BigDecimal initialInvestment;

    public CreatePortfolioRequest() {
    }

    public CreatePortfolioRequest(String name, BigDecimal initialInvestment) {
        this.name = name;
        this.initialInvestment = initialInvestment;
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
}
