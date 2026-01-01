package com.aero.quickfix.service;

import com.aero.quickfix.dto.CompoundInterestCalculationResponse;
import com.aero.quickfix.dto.CreateInvestmentRequest;
import com.aero.quickfix.dto.CreatePortfolioRequest;
import com.aero.quickfix.dto.MarketPriceDto;
import com.aero.quickfix.dto.PortfolioDto;
import com.aero.quickfix.model.Investment;
import com.aero.quickfix.model.Portfolio;
import com.aero.quickfix.repository.IInvestmentRepository;
import com.aero.quickfix.repository.IPortfolioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing investments and portfolio operations.
 * Handles compound interest calculations and portfolio management.
 */
@Service
public class InvestmentService {

    @Autowired
    private IPortfolioRepository portfolioRepository;

    @Autowired
    private IInvestmentRepository investmentRepository;

    @Autowired
    private MarketDataService marketDataService;

    /**
     * Create a new portfolio for a user.
     */
    public Portfolio createPortfolio(String userId, CreatePortfolioRequest request) {
        Portfolio portfolio = new Portfolio(userId, request.getName(), request.getInitialInvestment());
        portfolioRepository.save(portfolio);
        return portfolio;
    }

    /**
     * Get a specific portfolio by ID.
     */
    public Portfolio getPortfolio(String portfolioId) {
        return portfolioRepository.findById(portfolioId);
    }

    /**
     * Get all portfolios for a user.
     */
    public List<PortfolioDto> getUserPortfolios(String userId) {
        return portfolioRepository.findByUserId(userId).stream()
                .map(this::convertToPortfolioDto)
                .collect(Collectors.toList());
    }

    /**
     * Add an investment to a portfolio.
     */
    public Investment addInvestment(String portfolioId, CreateInvestmentRequest request) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId);
        if (portfolio == null) {
            throw new IllegalArgumentException("Portfolio not found: " + portfolioId);
        }

        Investment investment = new Investment(
                portfolioId,
                request.getAssetSymbol(),
                request.getAssetType(),
                request.getPrincipal(),
                request.getAnnualInterestRate()
        );

        if (request.getCompoundingFrequency() != null) {
            investment.setCompoundingFrequency(request.getCompoundingFrequency());
        }
        if (request.getYearsOfInvestment() != null) {
            investment.setYearsOfInvestment(request.getYearsOfInvestment());
        }

        // Calculate compound interest
        CompoundInterestCalculationResponse calc = calculateCompoundInterest(
                investment.getPrincipal(),
                investment.getAnnualInterestRate(),
                investment.getYearsOfInvestment(),
                investment.getCompoundingFrequency()
        );

        investment.setCurrentValue(calc.getFinalAmount());
        investment.setTotalInterest(calc.getTotalInterest());
        investment.setMaturityDate(System.currentTimeMillis() + (investment.getYearsOfInvestment() * 365L * 24 * 60 * 60 * 1000));

        investmentRepository.save(investment);
        portfolio.addInvestment(investment);
        updatePortfolioMetrics(portfolio);
        portfolioRepository.save(portfolio);

        return investment;
    }

    /**
     * Calculate compound interest.
     * Formula: A = P(1 + r/n)^(nt)
     * Where:
     * - A = Final Amount
     * - P = Principal
     * - r = Annual Interest Rate (as decimal)
     * - n = Compounding Frequency per year
     * - t = Time in years
     */
    public CompoundInterestCalculationResponse calculateCompoundInterest(
            BigDecimal principal,
            BigDecimal annualRate,
            int years,
            int compoundingFrequency) {

        // Normalize rate to decimal (e.g., 5% becomes 0.05)
        BigDecimal rate = annualRate.divide(new BigDecimal(100), 10, RoundingMode.HALF_UP);

        // Calculate: r/n
        BigDecimal ratePerPeriod = rate.divide(new BigDecimal(compoundingFrequency), 10, RoundingMode.HALF_UP);

        // Calculate: (1 + r/n)
        BigDecimal onePlusRate = BigDecimal.ONE.add(ratePerPeriod);

        // Calculate exponent: n*t
        int exponent = compoundingFrequency * years;

        // Calculate: (1 + r/n)^(n*t)
        BigDecimal finalMultiplier = onePlusRate.pow(exponent);

        // Calculate final amount: P * (1 + r/n)^(n*t)
        BigDecimal finalAmount = principal.multiply(finalMultiplier)
                .setScale(2, RoundingMode.HALF_UP);

        // Calculate total interest
        BigDecimal totalInterest = finalAmount.subtract(principal)
                .setScale(2, RoundingMode.HALF_UP);

        String compoundingPeriod = getCompoundingPeriodName(compoundingFrequency);

        return new CompoundInterestCalculationResponse(
                principal,
                annualRate,
                years,
                compoundingFrequency,
                finalAmount,
                totalInterest,
                compoundingPeriod
        );
    }

    /**
     * Get an investment by ID.
     */
    public Investment getInvestment(String investmentId) {
        return investmentRepository.findById(investmentId);
    }

    /**
     * Get all investments in a portfolio.
     */
    public List<Investment> getPortfolioInvestments(String portfolioId) {
        return investmentRepository.findByPortfolioId(portfolioId);
    }

    /**
     * Update an investment.
     */
    public Investment updateInvestment(String investmentId, CreateInvestmentRequest request) {
        Investment investment = investmentRepository.findById(investmentId);
        if (investment == null) {
            throw new IllegalArgumentException("Investment not found: " + investmentId);
        }

        investment.setAssetSymbol(request.getAssetSymbol());
        investment.setAssetType(request.getAssetType());
        investment.setPrincipal(request.getPrincipal());
        investment.setAnnualInterestRate(request.getAnnualInterestRate());

        if (request.getCompoundingFrequency() != null) {
            investment.setCompoundingFrequency(request.getCompoundingFrequency());
        }
        if (request.getYearsOfInvestment() != null) {
            investment.setYearsOfInvestment(request.getYearsOfInvestment());
        }

        // Recalculate compound interest
        CompoundInterestCalculationResponse calc = calculateCompoundInterest(
                investment.getPrincipal(),
                investment.getAnnualInterestRate(),
                investment.getYearsOfInvestment(),
                investment.getCompoundingFrequency()
        );

        investment.setCurrentValue(calc.getFinalAmount());
        investment.setTotalInterest(calc.getTotalInterest());

        investmentRepository.update(investment);

        // Update portfolio metrics
        Portfolio portfolio = portfolioRepository.findById(investment.getPortfolioId());
        if (portfolio != null) {
            updatePortfolioMetrics(portfolio);
            portfolioRepository.save(portfolio);
        }

        return investment;
    }

    /**
     * Remove an investment from portfolio.
     */
    public void removeInvestment(String investmentId) {
        Investment investment = investmentRepository.findById(investmentId);
        if (investment != null) {
            investmentRepository.delete(investmentId);
            Portfolio portfolio = portfolioRepository.findById(investment.getPortfolioId());
            if (portfolio != null) {
                portfolio.removeInvestment(investmentId);
                updatePortfolioMetrics(portfolio);
                portfolioRepository.save(portfolio);
            }
        }
    }

    /**
     * Update portfolio total gains and metrics.
     */
    private void updatePortfolioMetrics(Portfolio portfolio) {
        BigDecimal totalCurrentValue = BigDecimal.ZERO;
        BigDecimal totalInvested = BigDecimal.ZERO;

        for (Investment investment : portfolio.getInvestments()) {
            totalCurrentValue = totalCurrentValue.add(investment.getCurrentValue());
            totalInvested = totalInvested.add(investment.getPrincipal());
        }

        BigDecimal totalGains = totalCurrentValue.subtract(totalInvested)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal gainsPercentage = BigDecimal.ZERO;
        if (totalInvested.compareTo(BigDecimal.ZERO) > 0) {
            gainsPercentage = totalGains.divide(totalInvested, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal(100))
                    .setScale(2, RoundingMode.HALF_UP);
        }

        portfolio.setCurrentValue(totalCurrentValue);
        portfolio.setTotalGains(totalGains);
        portfolio.setTotalGainsPercentage(gainsPercentage);
        portfolio.setLastUpdatedAt(System.currentTimeMillis());
    }

    /**
     * Delete a portfolio.
     */
    public void deletePortfolio(String portfolioId) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId);
        if (portfolio != null) {
            // Delete all investments in the portfolio
            for (Investment investment : portfolio.getInvestments()) {
                investmentRepository.delete(investment.getInvestmentId());
            }
            portfolioRepository.delete(portfolioId);
        }
    }

    /**
     * Get compounding period name from frequency.
     */
    private String getCompoundingPeriodName(int frequency) {
        return switch (frequency) {
            case 1 -> "Annually";
            case 2 -> "Semi-Annually";
            case 4 -> "Quarterly";
            case 12 -> "Monthly";
            case 52 -> "Weekly";
            case 365 -> "Daily";
            default -> "Unknown";
        };
    }

    /**
     * Convert Portfolio to PortfolioDto.
     */
    private PortfolioDto convertToPortfolioDto(Portfolio portfolio) {
        PortfolioDto dto = new PortfolioDto();
        dto.setPortfolioId(portfolio.getPortfolioId());
        dto.setName(portfolio.getName());
        dto.setInitialInvestment(portfolio.getInitialInvestment());
        dto.setCurrentValue(portfolio.getCurrentValue());
        dto.setTotalGains(portfolio.getTotalGains());
        dto.setTotalGainsPercentage(portfolio.getTotalGainsPercentage());
        dto.setCreatedAt(portfolio.getCreatedAt());
        dto.setLastUpdatedAt(portfolio.getLastUpdatedAt());
        dto.setInvestmentCount(portfolio.getInvestments().size());
        dto.setActive(portfolio.isActive());
        return dto;
    }

    /**
     * Enrich investment with real-time market data from EODHD.
     */
    public void enrichInvestmentWithMarketData(Investment investment) {
        try {
            MarketPriceDto priceData = marketDataService.getCurrentPrice(investment.getAssetSymbol());

            if (priceData.isValid()) {
                investment.setCurrentMarketPrice(priceData.getPrice());
                investment.setLastPriceUpdate(priceData.getLastUpdated());
                investment.setMarketDataAvailable(true);

                // Calculate realized gain
                if (investment.getCurrentMarketPrice() != null) {
                    BigDecimal gain = investment.getCurrentMarketPrice()
                            .subtract(investment.getPrincipal())
                            .multiply(BigDecimal.ONE); // Assume 1 share for now
                    investment.setRealizedGain(gain);
                }

                // Add dividend yield
                BigDecimal dividendIncome = marketDataService.calculateDividendIncome(
                        investment.getAssetSymbol(),
                        investment.getPrincipal()
                );
                if (dividendIncome.compareTo(BigDecimal.ZERO) > 0) {
                    investment.setDividendYield(dividendIncome.divide(investment.getPrincipal(), 6, RoundingMode.HALF_UP));
                }
            } else {
                investment.setMarketDataAvailable(false);
            }
        } catch (Exception e) {
            investment.setMarketDataAvailable(false);
        }
    }

    /**
     * Get current market price for a symbol.
     */
    public MarketPriceDto getMarketPrice(String symbol) {
        return marketDataService.getCurrentPrice(symbol);
    }

    /**
     * Validate if a symbol is tradeable.
     */
    public boolean isValidSymbol(String symbol) {
        return marketDataService.isValidSymbol(symbol);
    }

    /**
     * Get all cached prices.
     */
    public List<MarketPriceDto> getCachedPrices() {
        return marketDataService.getAllCachedPrices().values().stream().toList();
    }

    /**
     * Clear market data cache.
     */
    public void clearMarketDataCache() {
        marketDataService.clearCache();
    }
}
