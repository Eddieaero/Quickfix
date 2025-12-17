package com.aero.quickfix.dto;

import java.math.BigDecimal;

/**
 * EODHD Company fundamental data response.
 */
public class EodhCompanyDataResponse {
    private General general;
    private Highlights highlights;
    private Valuation valuation;

    public static class General {
        private String code;
        private String name;
        private String exchange;
        private String currencyCode;
        private String sector;
        private String industry;
        private String description;
        private String webURL;
        private String logoURL;

        // Getters and Setters
        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getExchange() {
            return exchange;
        }

        public void setExchange(String exchange) {
            this.exchange = exchange;
        }

        public String getCurrencyCode() {
            return currencyCode;
        }

        public void setCurrencyCode(String currencyCode) {
            this.currencyCode = currencyCode;
        }

        public String getSector() {
            return sector;
        }

        public void setSector(String sector) {
            this.sector = sector;
        }

        public String getIndustry() {
            return industry;
        }

        public void setIndustry(String industry) {
            this.industry = industry;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getWebURL() {
            return webURL;
        }

        public void setWebURL(String webURL) {
            this.webURL = webURL;
        }

        public String getLogoURL() {
            return logoURL;
        }

        public void setLogoURL(String logoURL) {
            this.logoURL = logoURL;
        }
    }

    public static class Highlights {
        private BigDecimal marketCapitalization;
        private BigDecimal peRatio;
        private BigDecimal dividendYield;
        private BigDecimal dividendShare;

        // Getters and Setters
        public BigDecimal getMarketCapitalization() {
            return marketCapitalization;
        }

        public void setMarketCapitalization(BigDecimal marketCapitalization) {
            this.marketCapitalization = marketCapitalization;
        }

        public BigDecimal getPeRatio() {
            return peRatio;
        }

        public void setPeRatio(BigDecimal peRatio) {
            this.peRatio = peRatio;
        }

        public BigDecimal getDividendYield() {
            return dividendYield;
        }

        public void setDividendYield(BigDecimal dividendYield) {
            this.dividendYield = dividendYield;
        }

        public BigDecimal getDividendShare() {
            return dividendShare;
        }

        public void setDividendShare(BigDecimal dividendShare) {
            this.dividendShare = dividendShare;
        }
    }

    public static class Valuation {
        private BigDecimal trailingPE;
        private BigDecimal forwardPE;
        private BigDecimal enterpriseValue;

        // Getters and Setters
        public BigDecimal getTrailingPE() {
            return trailingPE;
        }

        public void setTrailingPE(BigDecimal trailingPE) {
            this.trailingPE = trailingPE;
        }

        public BigDecimal getForwardPE() {
            return forwardPE;
        }

        public void setForwardPE(BigDecimal forwardPE) {
            this.forwardPE = forwardPE;
        }

        public BigDecimal getEnterpriseValue() {
            return enterpriseValue;
        }

        public void setEnterpriseValue(BigDecimal enterpriseValue) {
            this.enterpriseValue = enterpriseValue;
        }
    }

    // Getters and Setters
    public General getGeneral() {
        return general;
    }

    public void setGeneral(General general) {
        this.general = general;
    }

    public Highlights getHighlights() {
        return highlights;
    }

    public void setHighlights(Highlights highlights) {
        this.highlights = highlights;
    }

    public Valuation getValuation() {
        return valuation;
    }

    public void setValuation(Valuation valuation) {
        this.valuation = valuation;
    }
}
