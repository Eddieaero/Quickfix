package com.aero.quickfix.service;

import com.aero.quickfix.dto.CompoundInterestCalculationResponse;
import com.aero.quickfix.dto.CreateInvestmentRequest;
import com.aero.quickfix.dto.CreatePortfolioRequest;
import com.aero.quickfix.model.Investment;
import com.aero.quickfix.model.Portfolio;
import com.aero.quickfix.repository.InvestmentRepository;
import com.aero.quickfix.repository.PortfolioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the InvestmentService.
 */
@DisplayName("Investment Service Tests")
public class InvestmentServiceTest {

    @Mock
    private PortfolioRepository portfolioRepository;

    @Mock
    private InvestmentRepository investmentRepository;

    @InjectMocks
    private InvestmentService investmentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should calculate compound interest correctly")
    void testCompoundInterestCalculation() {
        BigDecimal principal = new BigDecimal("10000");
        BigDecimal rate = new BigDecimal("5");
        int years = 10;
        int frequency = 12;

        CompoundInterestCalculationResponse result = investmentService.calculateCompoundInterest(
                principal, rate, years, frequency);

        assertNotNull(result);
        assertEquals(principal, result.getPrincipal());
        assertEquals(rate, result.getRate());
        assertEquals(years, result.getYears());
        assertEquals(frequency, result.getCompoundingFrequency());
        
        // Verify the final amount is greater than principal
        assertTrue(result.getFinalAmount().compareTo(principal) > 0);
        
        // Verify total interest is positive
        assertTrue(result.getTotalInterest().compareTo(BigDecimal.ZERO) > 0);
        
        // Expected final amount: ~12820.37
        assertTrue(result.getFinalAmount().compareTo(new BigDecimal("12800")) > 0);
        assertTrue(result.getFinalAmount().compareTo(new BigDecimal("12900")) < 0);
    }

    @Test
    @DisplayName("Should calculate daily compounding correctly")
    void testDailyCompounding() {
        BigDecimal principal = new BigDecimal("1000");
        BigDecimal rate = new BigDecimal("3");
        int years = 1;
        int frequency = 365;

        CompoundInterestCalculationResponse result = investmentService.calculateCompoundInterest(
                principal, rate, years, frequency);

        assertEquals("Daily", result.getCompoundingPeriod());
        assertTrue(result.getFinalAmount().compareTo(principal) > 0);
    }

    @Test
    @DisplayName("Should calculate zero interest for zero rate")
    void testZeroInterestRate() {
        BigDecimal principal = new BigDecimal("5000");
        BigDecimal rate = new BigDecimal("0");
        int years = 5;
        int frequency = 12;

        CompoundInterestCalculationResponse result = investmentService.calculateCompoundInterest(
                principal, rate, years, frequency);

        assertEquals(principal, result.getFinalAmount());
        assertEquals(BigDecimal.ZERO, result.getTotalInterest());
    }

    @Test
    @DisplayName("Should create portfolio successfully")
    void testCreatePortfolio() {
        String userId = "USER_123";
        String portfolioName = "My Portfolio";
        BigDecimal initialInvestment = new BigDecimal("50000");

        CreatePortfolioRequest request = new CreatePortfolioRequest(portfolioName, initialInvestment);
        Portfolio portfolio = investmentService.createPortfolio(userId, request);

        assertNotNull(portfolio);
        assertEquals(userId, portfolio.getUserId());
        assertEquals(portfolioName, portfolio.getName());
        assertEquals(initialInvestment, portfolio.getInitialInvestment());
        assertTrue(portfolio.isActive());
    }

    @Test
    @DisplayName("Should return correct compounding period name")
    void testCompoundingPeriodNames() {
        // Testing different frequencies
        CompoundInterestCalculationResponse annual = investmentService.calculateCompoundInterest(
                new BigDecimal("1000"), new BigDecimal("5"), 1, 1);
        assertEquals("Annually", annual.getCompoundingPeriod());

        CompoundInterestCalculationResponse monthly = investmentService.calculateCompoundInterest(
                new BigDecimal("1000"), new BigDecimal("5"), 1, 12);
        assertEquals("Monthly", monthly.getCompoundingPeriod());

        CompoundInterestCalculationResponse daily = investmentService.calculateCompoundInterest(
                new BigDecimal("1000"), new BigDecimal("5"), 1, 365);
        assertEquals("Daily", daily.getCompoundingPeriod());
    }
}
