package com.aero.quickfix.quant.repository;

import com.aero.quickfix.quant.model.OHLCVData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for OHLCV Data
 * Handles all database operations for OHLCV historical data
 */
@Repository
public interface OHLCVDataRepository extends JpaRepository<OHLCVData, Long> {

    /**
     * Find all OHLCV data for a symbol within a date range
     */
    @Query("SELECT o FROM OHLCVData o WHERE o.symbol = :symbol AND o.time >= :startDate AND o.time <= :endDate ORDER BY o.time ASC")
    List<OHLCVData> findBySymbolAndDateRange(
        @Param("symbol") String symbol,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    /**
     * Find the latest price data for a symbol
     */
    @Query("SELECT o FROM OHLCVData o WHERE o.symbol = :symbol ORDER BY o.time DESC LIMIT 1")
    Optional<OHLCVData> findLatestBySymbol(@Param("symbol") String symbol);

    /**
     * Find all symbols in the database
     */
    @Query("SELECT DISTINCT o.symbol FROM OHLCVData o ORDER BY o.symbol")
    List<String> findAllSymbols();

    /**
     * Find count of records for a symbol
     */
    @Query("SELECT COUNT(o) FROM OHLCVData o WHERE o.symbol = :symbol")
    Long countBySymbol(@Param("symbol") String symbol);

    /**
     * Check if data exists for a symbol on a specific date
     */
    @Query("SELECT COUNT(o) FROM OHLCVData o WHERE o.symbol = :symbol AND DATE(o.time) = :date")
    Long countBySymbolAndDate(@Param("symbol") String symbol, @Param("date") LocalDate date);
}
