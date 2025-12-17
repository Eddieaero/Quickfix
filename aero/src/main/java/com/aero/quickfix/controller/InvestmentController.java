package com.aero.quickfix.controller;

import com.aero.quickfix.dto.CompoundInterestCalculationResponse;
import com.aero.quickfix.dto.CreateInvestmentRequest;
import com.aero.quickfix.dto.CreatePortfolioRequest;
import com.aero.quickfix.dto.PortfolioDto;
import com.aero.quickfix.model.Investment;
import com.aero.quickfix.model.Portfolio;
import com.aero.quickfix.service.InvestmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for investment and portfolio management operations.
 */
@RestController
@RequestMapping("/api/investments")
@CrossOrigin(origins = "*", maxAge = 3600)
public class InvestmentController {

    @Autowired
    private InvestmentService investmentService;

    /**
     * Create a new portfolio.
     * POST /api/investments/portfolios
     */
    @PostMapping("/portfolios")
    public ResponseEntity<Portfolio> createPortfolio(
            @RequestHeader("X-User-Id") String userId,
            @RequestBody CreatePortfolioRequest request) {
        Portfolio portfolio = investmentService.createPortfolio(userId, request);
        return ResponseEntity.ok(portfolio);
    }

    /**
     * Get all portfolios for the authenticated user.
     * GET /api/investments/portfolios
     */
    @GetMapping("/portfolios")
    public ResponseEntity<List<PortfolioDto>> getUserPortfolios(
            @RequestHeader("X-User-Id") String userId) {
        List<PortfolioDto> portfolios = investmentService.getUserPortfolios(userId);
        return ResponseEntity.ok(portfolios);
    }

    /**
     * Get a specific portfolio.
     * GET /api/investments/portfolios/{portfolioId}
     */
    @GetMapping("/portfolios/{portfolioId}")
    public ResponseEntity<Portfolio> getPortfolio(@PathVariable String portfolioId) {
        Portfolio portfolio = investmentService.getPortfolio(portfolioId);
        if (portfolio == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(portfolio);
    }

    /**
     * Delete a portfolio.
     * DELETE /api/investments/portfolios/{portfolioId}
     */
    @DeleteMapping("/portfolios/{portfolioId}")
    public ResponseEntity<Void> deletePortfolio(@PathVariable String portfolioId) {
        investmentService.deletePortfolio(portfolioId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Add an investment to a portfolio.
     * POST /api/investments/portfolios/{portfolioId}/investments
     */
    @PostMapping("/portfolios/{portfolioId}/investments")
    public ResponseEntity<Investment> addInvestment(
            @PathVariable String portfolioId,
            @RequestBody CreateInvestmentRequest request) {
        try {
            Investment investment = investmentService.addInvestment(portfolioId, request);
            return ResponseEntity.ok(investment);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get all investments in a portfolio.
     * GET /api/investments/portfolios/{portfolioId}/investments
     */
    @GetMapping("/portfolios/{portfolioId}/investments")
    public ResponseEntity<List<Investment>> getPortfolioInvestments(@PathVariable String portfolioId) {
        List<Investment> investments = investmentService.getPortfolioInvestments(portfolioId);
        return ResponseEntity.ok(investments);
    }

    /**
     * Get a specific investment.
     * GET /api/investments/{investmentId}
     */
    @GetMapping("/{investmentId}")
    public ResponseEntity<Investment> getInvestment(@PathVariable String investmentId) {
        Investment investment = investmentService.getInvestment(investmentId);
        if (investment == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(investment);
    }

    /**
     * Update an investment.
     * PUT /api/investments/{investmentId}
     */
    @PutMapping("/{investmentId}")
    public ResponseEntity<Investment> updateInvestment(
            @PathVariable String investmentId,
            @RequestBody CreateInvestmentRequest request) {
        try {
            Investment investment = investmentService.updateInvestment(investmentId, request);
            return ResponseEntity.ok(investment);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Remove an investment from portfolio.
     * DELETE /api/investments/{investmentId}
     */
    @DeleteMapping("/{investmentId}")
    public ResponseEntity<Void> removeInvestment(@PathVariable String investmentId) {
        investmentService.removeInvestment(investmentId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Calculate compound interest without creating an investment.
     * POST /api/investments/calculate
     * Request body: {
     *   "principal": 10000,
     *   "annualRate": 5,
     *   "years": 10,
     *   "compoundingFrequency": 12
     * }
     */
    @PostMapping("/calculate")
    public ResponseEntity<CompoundInterestCalculationResponse> calculateCompoundInterest(
            @RequestBody Map<String, Object> request) {
        try {
            BigDecimal principal = new BigDecimal(request.get("principal").toString());
            BigDecimal annualRate = new BigDecimal(request.get("annualRate").toString());
            Integer years = Integer.parseInt(request.get("years").toString());
            Integer compoundingFrequency = Integer.parseInt(request.get("compoundingFrequency").toString());

            CompoundInterestCalculationResponse response = investmentService.calculateCompoundInterest(
                    principal, annualRate, years, compoundingFrequency);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Calculate investment returns projection.
     * GET /api/investments/projection?principal=10000&rate=5&years=10&frequency=12
     */
    @GetMapping("/projection")
    public ResponseEntity<CompoundInterestCalculationResponse> getInvestmentProjection(
            @RequestParam BigDecimal principal,
            @RequestParam BigDecimal rate,
            @RequestParam(defaultValue = "10") Integer years,
            @RequestParam(defaultValue = "12") Integer frequency) {
        CompoundInterestCalculationResponse response = investmentService.calculateCompoundInterest(
                principal, rate, years, frequency);
        return ResponseEntity.ok(response);
    }
}
