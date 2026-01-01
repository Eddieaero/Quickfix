package com.aero.quickfix.repository;

import com.aero.quickfix.model.Portfolio;
import java.util.List;

/**
 * Interface for Portfolio repository operations.
 * Enables mocking in unit tests.
 */
public interface IPortfolioRepository {
    void save(Portfolio portfolio);
    
    Portfolio findById(String portfolioId);
    
    List<Portfolio> findByUserId(String userId);
    
    void delete(String portfolioId);
    
    List<Portfolio> findAll();
    
    boolean exists(String portfolioId);
    
    int count();
}
