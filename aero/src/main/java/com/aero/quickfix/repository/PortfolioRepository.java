package com.aero.quickfix.repository;

import com.aero.quickfix.model.Portfolio;
import org.springframework.stereotype.Repository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory repository for storing portfolios.
 */
@Repository
public class PortfolioRepository implements IPortfolioRepository {
    
    private final Map<String, Portfolio> portfolios = new ConcurrentHashMap<>();

    public void save(Portfolio portfolio) {
        portfolios.put(portfolio.getPortfolioId(), portfolio);
    }

    public Portfolio findById(String portfolioId) {
        return portfolios.get(portfolioId);
    }

    public List<Portfolio> findByUserId(String userId) {
        return portfolios.values().stream()
                .filter(p -> p.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    public void delete(String portfolioId) {
        portfolios.remove(portfolioId);
    }

    public List<Portfolio> findAll() {
        return new ArrayList<>(portfolios.values());
    }

    public boolean exists(String portfolioId) {
        return portfolios.containsKey(portfolioId);
    }

    public int count() {
        return portfolios.size();
    }
}
