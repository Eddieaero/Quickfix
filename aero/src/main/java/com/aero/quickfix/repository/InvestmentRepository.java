package com.aero.quickfix.repository;

import com.aero.quickfix.model.Investment;
import org.springframework.stereotype.Repository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory repository for storing investments.
 */
@Repository
public class InvestmentRepository {
    
    private final Map<String, Investment> investments = new ConcurrentHashMap<>();

    public void save(Investment investment) {
        investments.put(investment.getInvestmentId(), investment);
    }

    public Investment findById(String investmentId) {
        return investments.get(investmentId);
    }

    public List<Investment> findByPortfolioId(String portfolioId) {
        return investments.values().stream()
                .filter(i -> i.getPortfolioId().equals(portfolioId))
                .collect(Collectors.toList());
    }

    public void delete(String investmentId) {
        investments.remove(investmentId);
    }

    public List<Investment> findAll() {
        return new ArrayList<>(investments.values());
    }

    public boolean exists(String investmentId) {
        return investments.containsKey(investmentId);
    }

    public void update(Investment investment) {
        investments.put(investment.getInvestmentId(), investment);
    }
}
