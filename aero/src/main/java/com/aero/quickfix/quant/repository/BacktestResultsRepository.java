package com.aero.quickfix.quant.repository;

import com.aero.quickfix.quant.model.BacktestResults;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Repository for Backtest Results
 */
@Repository
public interface BacktestResultsRepository extends JpaRepository<BacktestResults, UUID> {

    /**
     * Find all backtests for a specific strategy
     */
    List<BacktestResults> findByStrategyName(String strategyName);

    /**
     * Find all backtests for a specific symbol
     */
    List<BacktestResults> findBySymbol(String symbol);

    /**
     * Find backtests by strategy and symbol
     */
    List<BacktestResults> findByStrategyNameAndSymbol(String strategyName, String symbol);

    /**
     * Find best performing backtest by Sharpe ratio
     */
    @Query("SELECT b FROM BacktestResults b WHERE b.strategyName = :strategyName ORDER BY b.sharpeRatio DESC LIMIT 1")
    BacktestResults findBestByStrategyNameBySharpe(@Param("strategyName") String strategyName);

    /**
     * Find recent backtests
     */
    @Query("SELECT b FROM BacktestResults b WHERE b.createdAt >= :since ORDER BY b.createdAt DESC")
    List<BacktestResults> findRecentBacktests(@Param("since") LocalDateTime since);
}
