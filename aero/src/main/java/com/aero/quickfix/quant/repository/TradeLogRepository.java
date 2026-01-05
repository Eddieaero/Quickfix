package com.aero.quickfix.quant.repository;

import com.aero.quickfix.quant.model.TradeLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository for Trade Logs
 */
@Repository
public interface TradeLogRepository extends JpaRepository<TradeLog, UUID> {

    /**
     * Find all trades for a specific backtest
     */
    List<TradeLog> findByBacktestId(UUID backtestId);

    /**
     * Find all closed trades for a backtest
     */
    @Query("SELECT t FROM TradeLog t WHERE t.backtestId = :backtestId AND t.tradeStatus = 'CLOSED' ORDER BY t.tradeDate ASC")
    List<TradeLog> findClosedTradesByBacktestId(@Param("backtestId") UUID backtestId);

    /**
     * Find all open trades for a backtest
     */
    @Query("SELECT t FROM TradeLog t WHERE t.backtestId = :backtestId AND t.tradeStatus = 'OPEN' ORDER BY t.tradeDate ASC")
    List<TradeLog> findOpenTradesByBacktestId(@Param("backtestId") UUID backtestId);

    /**
     * Find trades by symbol
     */
    List<TradeLog> findBySymbol(String symbol);

    /**
     * Count winning trades for a backtest
     */
    @Query("SELECT COUNT(t) FROM TradeLog t WHERE t.backtestId = :backtestId AND t.profitLoss > 0")
    Long countWinningTrades(@Param("backtestId") UUID backtestId);

    /**
     * Count losing trades for a backtest
     */
    @Query("SELECT COUNT(t) FROM TradeLog t WHERE t.backtestId = :backtestId AND t.profitLoss < 0")
    Long countLosingTrades(@Param("backtestId") UUID backtestId);
}
