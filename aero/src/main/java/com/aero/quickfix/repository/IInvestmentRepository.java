package com.aero.quickfix.repository;

import com.aero.quickfix.model.Investment;
import java.util.List;

/**
 * Interface for Investment repository operations.
 * Enables mocking in unit tests.
 */
public interface IInvestmentRepository {
    void save(Investment investment);
    
    Investment findById(String investmentId);
    
    List<Investment> findByPortfolioId(String portfolioId);
    
    void delete(String investmentId);
    
    List<Investment> findAll();
    
    boolean exists(String investmentId);
    
    void update(Investment investment);
}
